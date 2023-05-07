#!/usr/bin/env bb
(require '[babashka.cli :as cli]
         '[clojure.string :as str])

(def spec {:alias {:f :filepath
                   :b :batch}
           :require [:filepath]})

; Mode checkers
(defn single-entry? [args]
  (and (not-empty (:args args))
       (not (contains? (:opts args) :batch))))

(defn interactive? [args]
  (and (not (contains? (:opts args) :batch))
       (empty? (:args args))))

(defn batch? [args]
  (contains? (:opts args) :batch))

; Argument checkers
(defn string-is-number? [s]
  (try
    (number? (Float/parseFloat s))
    (catch Exception _ false)))

(defn valid-amount? [amt]
  (string-is-number? amt))

(defn valid-date? [date]
  (let [components (str/split date #"/")]
    (and (= (count components) 3)
         (every? true? (map string-is-number? components)))))

(defn clean-string? [s]
  (not (str/includes? s ";")))

; Validators
(defn validate-string [s]
  (when-not (clean-string? s)
    (throw (Exception. (str s " contains invalid character ';'")))))

(defn validate-date [date]
  (validate-string date)
  (when-not (valid-date? date)
    (throw (Exception. (str date " is an invalid date")))))

(defn validate-amount [amt]
  (validate-string amt)
  (when-not (valid-amount? amt)
    (throw (Exception. (str amt " is an invalid amount")))))

(defn validate-single-txn [txn]
  (let [args (:args txn)
        num-args (count args)
        rules [validate-date
               validate-string
               validate-string
               validate-string
               validate-amount
               validate-string]]
    (when (> num-args 6)
      (throw (Exception. "Too many arguments!")))
    (when (< num-args 4)
      (throw (Exception. "There are some missing arguments.")))
    (mapv #(%1 %2) (take-last num-args rules) args)))

; transaction builder

(defn fill-args [args]
  (let [num-args (count args)]
    (concat (repeat (- 6 num-args) nil) args)))

(defn today
  "Creates a string in a format compatible with ledger"
  []
  (let [date (java.util.Date.)
        year (+ 1900 (.getYear date))
        month (inc (.getMonth date))
        day (.getDate date)]
    (str/join "/" [year month day])))

(defn format-txn [account amount currency]
  (let [account-length (count account)
        amount-length (count amount)
        currency-length (count currency)
        whitespace-length (- 50 (+ account-length amount-length currency-length))]
    (str "  " account (str/join "" (repeat whitespace-length " ")) amount " " currency)))

(defn build-txn [args]
  (let [date (if (first args) (first args) (today))
        payee (if (second args) (second args) "")
        line1 (str/join " " [date payee])
        line2 (format-txn (nth args 2) (nth args 4) (nth args 5))
        line3 (str "  "  (nth args 3))]
    (str/join "\n" ["" line1 line2 line3 ""])))

(defn single-entry-txn [txn]
  (validate-single-txn txn)
  (let [args (fill-args (:args txn))
        path (:filepath (:opts txn))
        ledger-entry (build-txn args)]
    (println ledger-entry)
    (spit path ledger-entry :append true)))

; entry point
(let [parsed (cli/parse-args *command-line-args* spec)]
  (cond
    (single-entry? parsed) (single-entry-txn parsed)
    (batch? parsed) (println "batch-entry coming soon!")
    (interactive? parsed) (println "interactive entry coming soon!")))
