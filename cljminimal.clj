#!/usr/bin/env bb
(require '[clojure.java.io :as io]
         '[clojure.string :as str])

(def args *command-line-args*)
(when-not args
  (println "You need a name for your project!")
  (System/exit 1))

(def project-name (first args))
(when (.exists (io/file project-name))
  (println "This directory already exists. Aborting.")
  (System/exit 1))
(when (str/includes? project-name "/")
  (println "Project name cannot include \"/\" or be a directory path.")
  (System/exit 1))

(io/make-parents (str project-name "/src/core.clj"))
(spit (str project-name "/deps.edn") '{})
(let [core-path (str project-name "/src/core.clj")]
  (spit core-path '(ns core))
  (spit core-path '(defn -main [opts] (println "Hello, world!")) :append true))

(doall (map println [(str project-name "was created")
                     "try this:"
                     (str "cd " project-name)
                     "clj -X core/-main"]))


