(ns t2000.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [t2000.twitlib :as twitlib]
            [ring.util.response :refer [response content-type]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes  
  (GET "/data" []
       (-> (twitlib/get-data)
           response
           (content-type "application/json")))
  (route/not-found "Not Found"))

(def app
  (wrap-cors
   (wrap-defaults app-routes site-defaults)
   :access-control-allow-origin [#".*"]
   :access-control-allow-methods [:get]))
