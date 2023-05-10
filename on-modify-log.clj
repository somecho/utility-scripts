#!/usr/bin/env bb
(require '[cheshire.core :as json]
         '[babashka.fs :as fs])

(let [oldtask (json/parse-string (read-line) true)
      newtask (json/parse-string (read-line) true)
      uuid (:uuid oldtask)
      data-path (str (fs/path (fs/parent *file*) (fs/path "last-modified.data")))]
  (spit data-path uuid)
  (println (json/generate-string newtask)))
