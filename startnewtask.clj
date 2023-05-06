#!/usr/bin/env bb
(require '[clojure.java.shell :as sh]
         '[clojure.string :as str])

(defn get-task-id
  [stdout]
  (-> stdout
      (last)
      (drop-last)
      (drop-last)
      (as-> chars (str/join "" chars))))

(defn add-task [args]
  (apply sh/sh (conj args "add" "task")))

(let [result (add-task *command-line-args*)]
  (when (not= (:exit result) 0)
    (println (:err result))
    (System/exit 1))
  (->> (str/split (:out result) #" ")
       (get-task-id)
       (sh/sh "task" "start")))
