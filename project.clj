(defproject t2000 "0.1.0-SNAPSHOT"
  :description "Fetch data from twitter and present in diagram"
  :url "https://github.com/SigmaDojo/Trumpalizer2000"
  :min-lein-version "2.0.0"
  
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring-cors "0.1.10"]
                 [org.clojure/data.json "0.2.6"]
                 [environ "1.1.0"]
                 [clj-time "0.13.0"]
                 [http-kit "2.2.0"]]
  
  :plugins [[lein-ring "0.9.7"]
            [lein-environ "1.1.0"]]

  :ring {:handler t2000.handler/app}

  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.0"]]}})
