#!/usr/bin/env bb
(require '[clojure.java.shell :refer [sh]]
         '[clojure.string :as str]
         '[babashka.fs :as fs]
         '[babashka.cli :as cli])

(def spec {:alias {:n :numbered
                   :i :id}
           :coerce {:numbered :boolean}
           :args->opts [:project-type]
           :validate {:id {:pred number?
                           :ex-msg (fn [m] "-g --goto has to be a valid number")}}})

(defn ripgrep [types]
  (let [type-adds (map #(vec ["--type-add"
                              (str (:alias %) ":" (:glob %))]) types)
        t (map #(str "-t" (:alias %)) types)
        args (concat ["rg"] (flatten type-adds) t ["--files"])]
    (-> (apply sh args)
        (:out)
        (str/split #"\n")
        (as-> paths (map #(str (fs/cwd) "/" %) paths))
        (as-> fullpaths (map fs/parent fullpaths))
        (as-> pathobjs (map str pathobjs))
        (sort))))

(defn find-java-projects []
  (ripgrep [{:alias "settings" :glob "settings.gradle"}
            {:alias "pom" :glob "pom.xml"}]))

(defn find-clj-projects []
  (ripgrep [{:alias "deps" :glob "deps.edn"}
            {:alias "lein" :glob "project.cl"}]))

(defn assign [project-type]
  (case project-type
    "clj" (find-clj-projects)
    "clojure" (find-clj-projects)
    "java" (find-java-projects)
    (println "Project type not supported")))

(try (sh "rg") (catch Exception _ (println "rg is not installed")))

(let [parsed (cli/parse-opts *command-line-args* spec)
      numbered (:numbered parsed)
      id (:id parsed)
      project-type (:project-type parsed)]
  (-> (assign project-type)
      (as-> paths (if numbered
                    (map-indexed #(str %1 ": " %2) paths)
                    paths))
      (as-> paths (if id
                    (println (nth paths id))
                    (mapv println paths)))))
