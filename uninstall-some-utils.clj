#!/usr/bin/env bb

(def scripts ["cljminimal"
              "startnewtask"
              "stoptasks"
              "uninstall-some-utils"])

(let [home (System/getProperty "user.home")
      path (str home "/.local/bin/")]
  (doseq [script scripts]
    (clojure.java.io/delete-file (str path script))))
