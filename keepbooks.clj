#!/usr/bin/env bb
(require '[babashka.cli :as cli]
         '[clojure.string :as str])

(def spec {:alias {:f :filepath
                   :b :batch
                   :d :date}
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

; Validators
(defn validate-string [s]
  (when (str/includes? s ";")
    (throw (Exception. (str s " contains invalid character ';'")))))

(defn validate-date [date]
  (validate-string date)
  (when-not (valid-date? date)
    (throw (Exception. (str date " is an invalid date")))))

(defn validate-amount [amt]
  (validate-string amt)
  (when-not (valid-amount? amt)
    (throw (Exception. (str amt " is an invalid amount")))))

(defn validate-txn [txn]
  (validate-date (:date txn))
  (mapv validate-string (:payee txn))
  (validate-string (:debit txn))
  (validate-string (:credit txn))
  (validate-amount (:amount txn))
  (validate-string (:currency txn)))

; transaction builder
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

(defn build-txn [parsed-args]
  (let [args (:args parsed-args)
        num-args (count args)
        date (if (:date (:opts parsed-args))
               (:date (:opts parsed-args))
               (today))
        payee (drop-last 4 args)
        debit (nth args (- num-args 4))
        credit (nth args (- num-args 3))
        amount (nth args (- num-args 2))
        currency (nth args (- num-args 1))]
    {:date date
     :payee payee
     :debit debit
     :credit credit
     :amount amount
     :currency currency}))

(defn build-entry [txn]
  (let [payee (str/join " " (:payee txn))
        line1 (str/join " " [(:date txn) payee])
        line2 (format-txn (:debit txn) (:amount txn) (:currency txn))
        line3 (str "  " (:credit txn))]
    (str/join "\n" ["" line1 line2 line3 ""])))

(defn single-entry-txn [parsed-args]
  (when (< (count (:args parsed-args)) 4)
    (throw (Exception. "Not enough arguments to make an entry.")))
  (let [path (:filepath (:opts parsed-args))
        txn (build-txn parsed-args)
        entry (build-entry txn)]
    (validate-txn txn)
    (spit path entry :append true)
    (println entry)))

(defn prompt
  "Reads user input and validates it. If input is invalid, 
  prompts again."
  [validate-fn validatee error-msg prompt-fn]
  (try (validate-fn @validatee)
       (catch Exception _ (do
                            (println error-msg)
                            (reset! validatee (prompt-fn))))))
(defn prompt-date []
  (println "Date (leave blank for today):")
  (let [date (atom nil)]
    (reset! date (read-line))
    (if (empty? @date)
      (reset! date (today))
      (prompt validate-date date "Invalid date\n" prompt-date))
    @date))

(defn prompt-payee []
  (println "Payee:")
  (let [payee (atom nil)]
    (reset! payee (read-line))
    (prompt validate-string payee "Field cannot contain ; character" prompt-payee)
    @payee))

(defn prompt-account [account-msg]
  (println account-msg)
  (let [acct (atom nil)]
    (reset! acct (read-line))
    (prompt validate-string
            acct
            "Field cannot contain ; character"
            #(prompt-account "Account to debit:"))
    @acct))

(defn prompt-amount []
  (println "Ammount (without currency):")
  (let [amt (atom nil)]
    (reset! amt (read-line))
    (prompt validate-amount amt "Invalid amount" prompt-amount)
    @amt))

(defn prompt-currency []
  (println "Currency:")
  (let [currency (atom nil)]
    (reset! currency (read-line))
    (prompt validate-string
            currency
            "Field cannot contain ; character"
            prompt-currency)
    @currency))

(defn affirmative? [s]
  (case s
    "" true
    "y" true
    "yes" true
    false))

(defn interactive-txn [parsed-args]
  (let [date (prompt-date)
        payee (prompt-payee)
        debit (prompt-account "Account to debit:")
        credit (prompt-account "Account to credit:")
        amount (prompt-amount)
        currency (prompt-currency)
        path (:filepath (:opts parsed-args))
        entry (build-entry {:date date
                            :payee (str/split payee #" ")
                            :debit debit
                            :credit credit
                            :amount amount
                            :currency currency})]
    (spit path entry :append true)
    (println entry "\n")
    (println "Make another entry? (y/n)")
    (when (affirmative? (str/lower-case (read-line)))
      (interactive-txn parsed-args))))

; entry point
(let [parsed (cli/parse-args *command-line-args* spec)]
  (cond
    (single-entry? parsed) (single-entry-txn parsed)
    (batch? parsed) (println "batch-entry coming soon!")
    (interactive? parsed) (interactive-txn parsed)))
