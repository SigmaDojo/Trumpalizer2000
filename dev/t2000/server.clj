(ns t2000.server
  (:require [org.httpkit.server :refer [run-server]]
            [t2000.handler :refer [app]]))


(defonce server (atom nil))

(defn start-server []
  (reset! server (run-server #'app {:port 3000 :join? false})))

(defn stop-server []
  (if-let [it @server]
    (.stop it))
  (reset! server nil))

