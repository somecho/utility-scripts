#!/usr/bin/env bb
(require '[clojure.string :as str]
         '[clojure.java.io :as io]
         '[clojure.java.shell :as sh])

(defn get-taskwarrior-data-location []
  (let [home (System/getProperty "user.home")
        taskrc (-> (try (slurp (str home "/.taskrc"))
                        (catch Exception _
                          (slurp (str home "/.config/task/taskrc"))))
                   (as-> rc (str/split rc #"\n")))
        ;On NixOS or using Homemanager, the taskrc may be
        ;in another place. The following lines tries to find
        ;that place.
        config (-> taskrc
                   (as-> lines
                         (filter #(str/includes? % "include") lines))
                   (as-> lines
                         (map #(drop 8 %) lines))
                   (as-> lines
                         (map #(str/join "" %) lines))
                   (as-> paths
                         (map #(slurp %) paths))
                   (as-> lines
                         (map #(str/split % #"\n") lines))
                   (flatten)
                   (concat taskrc))
        datalocation (-> config
                         (as-> c
                               (filter #(str/includes? % "data.location") c))
                         (first)
                         (as-> line (drop 14 line))
                         (as-> ch (str/join "" ch))
                         (str/replace "~" home))]
    datalocation))

(let [data-path (get-taskwarrior-data-location)
      modified-path (str data-path "/last-modified.data")]
  (when-not (.exists (io/file modified-path))
    (println "No last modified data. Have you installed and used the hook?")
    (System/exit 1))
  (-> (slurp modified-path)
      (as-> id (sh/sh "task" id "start"))
      (:out)
      (println)))
