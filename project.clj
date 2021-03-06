(defproject t2000 "0.1.0-SNAPSHOT"
  :description "Fetch data from twitter and present in diagram"
  :url "https://github.com/SigmaDojo/Trumpalizer2000"
  :min-lein-version "2.0.0"
  
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/data.json "0.2.6"]
                 [compojure "1.6.0"]
                 [ring/ring-defaults "0.3.0"]
                 [ring/ring-codec "1.0.1"]             
                 [ring-cors "0.1.10"]
                 [environ "1.1.0"]
                 [clj-time "0.13.0"]
                 [http-kit "2.2.0"]]
  
  :plugins [[lein-ring "0.12.0"]
            [lein-pprint "1.1.2"]
            [lein-environ "1.1.0"]]

  :main t2000.server
  
  :ring {:handler t2000.server/app}

  :profiles {:dev [:dev-defaults :dev-overrides]
             :dev-defaults {:dependencies [[javax.servlet/servlet-api "2.5"]]}})
