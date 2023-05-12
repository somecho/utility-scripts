#!/usr/bin/env bb
(require '[clojure.java.shell :refer [sh]]
         '[clojure.string :as str])

(defn get-accounts [ledger-file]
  (-> (sh "ledger" "-f" ledger-file "bal" "--flat")
      (:out)
      (str/split #"\n")
      (as-> entries (map #(str/split % #" ") entries))
      (as-> entry-arrays (map #(filter not-empty %) entry-arrays))
      (as-> clean-arrays (filter #(>= (count %) 3) clean-arrays))
      (as-> entry-arrays  (map #(nthrest % 2) entry-arrays))
      (as-> accounts (map #(str/join " " %) accounts))))

(let [ledger-file (first *command-line-args*)]
  (->> (get-accounts ledger-file)
       (mapv println)))


