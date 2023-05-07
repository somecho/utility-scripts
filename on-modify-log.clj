#!/usr/bin/env bb
(require '[cheshire.core :as json])

(let [oldtask (json/parse-string (read-line) true)
      newtask (json/parse-string (read-line) true)
      uuid (:uuid oldtask)]
  (spit "../last-modified.data" uuid) 
  (println (json/generate-string newtask)))
