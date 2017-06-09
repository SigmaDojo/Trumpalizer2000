(ns t2000.core
  (:require [t2000.twitlib :as twit]
            [clj-time.format :as f]
            [clojure.data.json :as json]))



(def search-url
  "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=realDonaldTrump&count=200")


(def cached-tweets
  (delay (twit/http-get search-url)))


(defn tweets->date
  "Extract create-date from each tweet"
  [tweets]
  (map
   (fn [tweet] (twit/parse-twitter-date (:created_at tweet)))
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

