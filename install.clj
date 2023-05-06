#!/usr/bin/env bb
(require '[clojure.java.shell :refer [sh]]
         '[clojure.string :refer [split includes? join]])

(defn drop-extension [filename]
  (first (split filename #"\.")))

(let [files (-> (sh "ls")
                (:out)
                (split #"\n"))
      cljfiles (filter #(includes? % ".clj") files)
      scripts (filter #(not= % "install.clj") cljfiles)
      home (System/getProperty "user.home")
      path "/.local/bin/"]
  (doseq [script scripts]
    (sh "cp" script (str home path (drop-extension script))))
  (println "The following scripts have been copied to '~/.local/bin':\n")
  (doseq [script scripts]
    (println (drop-extension script)))
  (println)
  (println "To use them please ensure that '~/.local/bin` is in your PATH.")
  (println "i.e. export PATH=~/.local/bin:$PATH"))
