(defproject aoc22 "0.1.0-SNAPSHOT"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[clj-http                       "3.12.3"]
                 [clojure.java-time              "1.1.0"]
                 [com.rpl/specter                "1.1.4"]
                 [org.clojure/clojure            "1.10.3"]
                 [org.clojure/core.cache         "1.0.225"]
                 [org.clojure/core.match         "0.2.1"]
                 [org.clojure/core.match         "1.0.0"]
                 [org.clojure/data.xml           "0.0.8"]
                 [org.clojure/data.zip           "0.1.1"]
                 [org.clojure/tools.logging      "1.2.4"]
                 [org.clojure/tools.macro        "0.1.5"]
                 [spootnik/unilog                "0.7.30"]]
  :profiles {:dev {:dependencies [[integrant/repl                     "0.3.2"]
                                  [org.clojure/tools.namespace        "1.3.0"]]
                   :source-paths ["dev"]
                   :resource-paths ["resources" "dev-resources"]}
             :uberjar {:aot :all}}
  :repl-options {:init-ns aoc22.core}
  :repl-options {:init-ns user
                 :prompt #(str "\u001B[35m[\u001B[34m" % "\u001B[35m]\u001B[33m▶\u001B[m ")})
