(ns t2000.twitlib
  (:require [org.httpkit.client :as http]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clj-time.format :as f]
            [environ.core :refer [env]]
            [clojure.data.json :as json])
  (:import [java.util Locale]))



(defn parse-twitter-date 
  "Parse a date from stupid twitter format:
   Example:  Fri Apr 21 01:02:03 +0000 2017"
  [date-string]
  (let [date-format (f/formatter "EEE MMM dd HH:mm:ss Z yyyy")]
    (f/parse (.withLocale date-format Locale/US) date-string)))


(defn json->map
  "Convert JSON string to clojure map. Keys are converted to keywords"
  [s]
  (json/read-str s :key-fn keyword))


(defn parse-json-body
  "Return a clojure map from the json body.
  If error, print the response and return nil."
  [{:keys [body error] :as response}]
  (if error
    (println response)
    (json->map body)))



;;;
;;; Connect to Twitter
;;;

(defn- request-token-callback [response]
  (let [data (parse-json-body response)]
    (when (= (:token_type data) "bearer")
      (:access_token data))))


(defn- request-token []
  (let [url "https://api.twitter.com/oauth2/token"
        key (env :twitlib-consumer-key)
        secret (env :twitlib-consumer-secret)
        form-params {"grant_type" "client_credentials"}]
    @(http/post url {:basic-auth [key secret]
                     :form-params form-params}
                request-token-callback)))


(def token
  "When deref-ed the first time, we retrieve auth token 
  from twitter. Token is cached, so when deref-ed next
  time we use the cached token."
  (delay (request-token)))


(defn http-get
  "Send http GET including twitter authentication.
  Return json converted into clojure data structure."
  [url]
  @(http/get url {:oauth-token @token} parse-json-body))


