#!/usr/bin/env bb
(require '[babashka.cli :as cli])
(require '[babashka.curl :as curl])
(require '[clojure.data.xml :as xml])
(require '[clojure.tools.logging :as log])
(require '[clojure.java.io :as io])
(require '[clojure.pprint :refer [pprint]])
(require '[clojure.string :as str])

(defn in? [coll elm] (some #(= elm %) coll))

(defn format-lib [lib]
  (if (.contains lib "/")
    lib
    (str lib "/" lib)))

(defn format-url [artifact]
  (str "https://repo.clojars.org/" artifact "/maven-metadata.xml"))

(defn parse-body
  "parses the XML metadata returned by clojars and returns a list of versions"
  [body]
  (-> (xml/parse-str body)
      (:content)
      (as-> content
            (filter #(= :versioning (:tag %)) content))
      (first)
      (:content)
      (as-> content
            (filter #(= :versions (:tag %)) content))
      (first)
      (:content)
      (as-> versions
            (filter map? versions))
      (as-> version-maps
            (map #(first (:content %)) version-maps))
      (vec)))

(defn find-versions [lib]
  (parse-body (:body (curl/get (format-url lib)))))

(defn validate-version [version versions]
  (if-not (in? versions version)
    (do (log/error (str "Version " version " not found"))
        (System/exit 1))
    version))

(defn validate-file [file]
  (if-not (.exists (io/file file))
    (do (log/error (str "Config file: " file " not found"))
        (System/exit 1))
    file))

(defn get-config []
  (cond (.exists (io/file "deps.edn")) "deps.edn"
        (.exists (io/file "project.clj")) "project.clj"
        (.exists (io/file "shadow-cljs.edn")) "shadow-cljs.edn"))

(defn add-deps-edn [file artifact version]
  (let [edn (read-string (slurp file))
        new-edn (assoc-in edn [:deps (symbol artifact)] {:mvn/version version})]
    (binding [*print-namespace-maps* false]
      (pprint new-edn (io/writer file))
      (println "Added" {artifact {:mvn/version version}}))))

(defn shorten [artifact]
  (let [[group name] (str/split artifact #"/")]
    (if (= group name) name artifact)))

(defn add-project-clj [file artifact version]
  (let [config (read-string (slurp file))
        [deps-index deps] (loop [clj (map-indexed #(vec [%1 %2]) config)]
                            (if (= :dependencies (second (first clj)))
                              (second clj)
                              (recur (rest clj))))
        new-deps (vec (distinct (conj deps [(symbol artifact) version])))
        new-config (concat
                    (take deps-index config)
                    `[~new-deps]
                    (drop (inc deps-index) config))]
    (binding [*print-namespace-maps* false]
      (pprint new-config (io/writer file))
      (println "Added" [artifact (str "\"" version "\"")]))))

(defn add-shadow-edn [file artifact version]
  (let [edn (read-string (slurp file))
        artifact (shorten artifact)
        deps (if (:dependencies edn) (:dependencies edn) [])
        new-deps (vec (distinct (conj deps [(symbol artifact) version])))
        new-edn (assoc edn :dependencies new-deps)]
    (binding [*print-namespace-maps* false]
      (pprint new-edn (io/writer file)))
    (println "Added" [artifact (str "\"" version "\"")])))

(defn add-dependencies [file artifact version]
  (let [fav [file artifact version]]
    (case file
      "deps.edn" (apply add-deps-edn fav)
      "project.clj" (apply add-project-clj fav)
      "shadow-cljs.edn" (apply add-shadow-edn  fav))))

(defn add
  [{:keys [:opts]
    {:keys [lib version file]} :opts}]
  (let [artifact (format-lib lib)
        versions (find-versions artifact)
        version (if version
                  (validate-version version versions)
                  (last versions))
        file (if file (validate-file file) (get-config))]
    (add-dependencies file artifact version)))

(def table
  [{:cmds ["add"] :fn add :args->opts [:lib :version] :alias {:f :file}}])

(cli/dispatch table *command-line-args*)
