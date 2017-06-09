(ns t2000.twitlib
  (:require [org.httpkit.client :as http]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clj-time.format :as f]
            [environ.core :refer [env]]
            [clojure.data.json :as json])
  (:import [java.util Locale]))


;;;
;;; Helper functions
;;;

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

(defn request-token-callback [response]
  (let [data (parse-json-body response)]
    (when (= (:token_type data) "bearer")
      (:access_token data))))


(defn request-token []
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


;;
;;
;;


(def search-url
  "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=realDonaldTrump&count=200")


(def cached-tweets
  (delay (http-get search-url)))


(defn tweets->date
  "Extract create-date from each tweet"
  [tweets]
  (map
   (fn [tweet] (parse-twitter-date (:created_at tweet)))
   tweets))


;; TODO: generate this?
;; (def empty-freq  (zipmap (map #(format "%02d" %) (range 24)) (repeat 0)))
(def empty-freq {"00" 0
                 "01" 0
                 "02" 0
                 "03" 0
                 "04" 0
                 "05" 0
                 "06" 0
                 "07" 0
                 "08" 0
                 "09" 0
                 "10" 0
                 "11" 0
                 "12" 0
                 "13" 0
                 "14" 0
                 "15" 0
                 "16" 0
                 "17" 0
                 "18" 0
                 "19" 0
                 "20" 0
                 "21" 0
                 "22" 0
                 "23" 0})


(defn extract-hour [date]
  (f/unparse (f/formatter "HH") date))


(defn group-tweets [tweets]
  (let [hours (map extract-hour (tweets->date tweets))]
    (merge empty-freq
           (frequencies hours))))


(defn tweet-freq->coords [tweets]
  (mapv (fn [[k v]] {:x k :y v})
        (group-tweets tweets)))


(defn get-data []
  (json/write-str
   {:type "barchart"
    :data (tweet-freq->coords @cached-tweets)}))

