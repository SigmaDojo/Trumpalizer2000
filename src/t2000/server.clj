(ns t2000.server
  (:require  [org.httpkit.server :refer [run-server]]
             [compojure.core :refer :all]
             [compojure.route :as route]
             [t2000.core :as t2c]
             [ring.util.response :refer [response redirect content-type]]
             [ring.middleware.cors :refer [wrap-cors]]
             [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] (redirect "/index.html"))
  
  (GET "/data" []
       (-> (t2c/get-data)
           response
           (content-type "application/json")))
  
  (route/not-found "Not Found"))


(def app
  (wrap-cors
   (wrap-defaults app-routes site-defaults)
   :access-control-allow-origin [#".*"]
   :access-control-allow-methods [:get]))

;;
;;
;;

(defonce server (atom nil))

(defn start-server []
  (reset! server (run-server #'app {:port 3000 :join? false})))

(defn stop-server []
  (if-let [stop-fn @server]
    (stop-fn))
  (reset! server nil))

