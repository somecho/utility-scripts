#!/usr/bin/env bb
(require '[clojure.java.shell :refer [sh]]
         '[clojure.string :refer [split]])

(defn also [args f & {:keys [skip]
                      :or {skip false}}]
  (if skip
    (f)
    (f args))
  args)

(-> (sh "task" "active")
    (:out)
    (split #"\n")
    (nthrest 3)
    (drop-last)
    (as-> cols (map #(split % #" ") cols))
    (as-> cols (map first cols))
    (as-> cols (filter not-empty cols))
    (also (fn [ids] (doall (map #(println "Task" % "is active") ids))))
    (also #(println "stopping tasks...") :skip true)
    (as-> ids (map #(sh "task" "stop" %) ids))
    (doall))

(println "Tasks stopped")



