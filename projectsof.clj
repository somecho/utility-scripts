#!/usr/bin/env bb
(require '[clojure.java.shell :refer [sh]]
         '[clojure.string :as str]
         '[babashka.fs :as fs])

(defn find-java-projects
  []
  (-> (sh "rg" 
      "--type-add" "settings:settings.gradle"
      "--type-add" "pom:pom.xml"
      "-tsettings" "-tpom" "--files")
      (:out)
      (str/split #"\n")
      (as-> paths (map #(str (fs/cwd) "/" %) paths))
      (as-> fullpaths (map fs/parent fullpaths))
      (as-> parent (mapv #(println (str %)) parent))))

(defn assign [project-type]
  (case project-type
    "java" (find-java-projects)
    (println "Project type not supported")))

(try (sh "rg") (catch Exception _ (println "rg is not installed")))

(assign (first *command-line-args*))
