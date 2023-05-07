#!/usr/bin/env bb
(require '[babashka.cli :as cli]
         '[clojure.string :as str])

(def spec {:alias {:f :filepath
                   :b :batch}})

(defn single-entry? [args]
  (and (not-empty (:args args))
       (not (contains? (:opts args) :batch))))

(defn interactive? [args]
  (and (not (contains? (:opts args) :batch))
       (empty? (:args args))))

(defn batch? [args]
  (contains? (:opts args) :batch))
(defn string-is-number? [s]
  (try
    (number? (Float/parseFloat s))
    (catch Exception _ false)))

(defn valid-amount? [amt]
  (let [parts (str/split amt #" ")]
    (and (= (count parts) 2)
         (string-is-number? (first parts)))))

(defn valid-date? [date]
  (let [components (str/split date #"/")]
    (and (= (count components) 3)
         (every? true? (map string-is-number? components)))))

(defn clean-string? [s]
  (not (str/includes? s ";")))

(defn validate-string [s]
  (when-not (clean-string? s)
    (throw (Exception. "String contains invalid character ';'"))))

(defn validate-date [date]
  (validate-string date)
  (when-not (valid-date? date)
    (throw (Exception. "Date is invalid!"))))

(defn validate-amount [amt]
  (validate-string amt)
  (when-not (valid-amount? amt)
    (throw (Exception. "Invalid amount."))))

;for testing
(def cmds [["-f" "2023.ledger" "2023/05/23" "McDonald's" "Expenses:Food" "Assets:Bank" "10.00 EUR"]
           ["-f" "2023.ledger" "-b" "txns.csv"]
           ["-f" "2023.ledger"]
           ["-f" "2023.ledger" "2023/05/23" "McDonald's" "Expenses:Food" "Assets:Bank" "10.00 EUR" "extra:args"]
           ["-f" "2023.ledger" "2023/05/23" "McDonald's"]
           ["-f" "2023.ledger" "2023;05/23" "McDonald's" "Expenses:Food" "Assets:Bank" "10.00 EUR"]
           ["-f" "2023.ledger" "2023/05/23" "McDo;nald's" "Expenses:Food" "Assets:Bank" "10.00 EUR"]
           ["-f" "2023.ledger" "2023/05/23" "McDonald's" "Expenses:;Food" "Assets:Bank" "10.00 EUR"]
           ["-f" "2023.ledger" "2023/05/23" "McDonald's" "Expenses:Food" "Assets:;Bank" "10.00 EUR"]
           ["-f" "2023.ledger" "2023/05/23" "McDonald's" "Expenses:Food" "Assets:Bank" "10.00EUR"]])

(defn validate-single-txn [txn]
  (let [args (:args txn)
        num-args (count args)
        rules [validate-date
               validate-string
               validate-string
               validate-string
               validate-amount]]
    (when (> num-args 5)
      (throw (Exception. "Too many arguments!")))
    (when (< num-args 3)
      (throw (Exception. "There are some missing arguments.")))
    (map #(%1 %2) (take-last num-args rules) args)))

(validate-single-txn (cli/parse-args (get cmds 3) spec))

(defn single-entry-txn [txn]
  (validate-single-txn txn))

(let [parsed (cli/parse-args (get cmds 0) spec)]
  (cond
    (single-entry? parsed) (single-entry-txn parsed)
    (batch? parsed) (println "batch-entry")
    (interactive? parsed) (println "interactive")))







