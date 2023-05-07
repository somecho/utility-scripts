#!/usr/bin/env bb
(require '[clojure.java.shell :refer [sh]]
         '[clojure.string :refer [split includes?]])

(defn drop-extension [filename]
  (first (split filename #"\.")))

(defn create-uninstall-script [scripts path filename]
  (spit (str path filename) "#!/usr/bin/env bb\n")
  (spit (str path filename)
        `(do
           (def a# ~scripts)
           (doseq [b# a#]
             (clojure.java.io/delete-file (str ~path b#)))
           (clojure.java.io/delete-file (str ~path ~filename))) :append true))

(let [files (-> (sh "ls")
                (:out)
                (split #"\n"))
      cljfiles (filter #(includes? % ".clj") files)
      scripts (filter #(and (not= % "install.clj")
                            (not= % "on-modify-log.clj")) cljfiles)
      extensionless  (vec (map drop-extension scripts))
      home (System/getProperty "user.home")
      path "/.local/bin/"
      uninstall "uninstall-some-scripts"]
  (doseq [script scripts]
    (sh "cp" script (str home path (drop-extension script))))
  (create-uninstall-script extensionless (str home path) uninstall)
  (sh "chmod" "+x" (str home path uninstall))
  (println "The following scripts have been copied to '~/.local/bin':\n")
  (doseq [script scripts]
    (println (drop-extension script)))
  (println)
  (println "To use them please ensure that '~/.local/bin` is in your PATH.")
  (println "i.e. export PATH=~/.local/bin:$PATH\n")
  (println "To uninstall call the" uninstall "script"))
