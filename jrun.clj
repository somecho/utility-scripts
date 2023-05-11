#!/usr/bin/env bb
(require '[clojure.java.shell :as sh]
         '[babashka.fs :as fs])

(fs/glob "." "*App*")
(defn matchfile
  [s]
  (-> (fs/glob "." (str "*" s "*"))
      (first)
      (str)))

(let [file (matchfile (first *command-line-args*))]
  (sh/sh "javac" file)
  (-> (sh/sh "java" (first (fs/split-ext file)))
      (:out)
      (println)))

