#!/usr/bin/env bb
(require '[clojure.java.shell :as sh]
         '[cheshire.core :as json])

(when (< (count *command-line-args*) 2)
  (println "You need atleast two arguments."))

(defn get-task-attribute
  [id attr]
  (-> (sh/sh "task" id "export")
      (:out)
      (json/parse-string) 
      (first)
      (get attr)))

(let [[id attr] *command-line-args*]
  (println (get-task-attribute id attr)))
