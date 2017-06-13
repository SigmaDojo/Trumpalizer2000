(ns t2000.core
  (:require [t2000.twitlib :as twit]
            [clj-time.format :as f]
            [clojure.data.json :as json]))

(def ^:dynamic *URL*
  "https://api.twitter.com/1.1/statuses/user_timeline.json")


(defn- query-params
  "Return query params for twitter HTTP query.
  Include max_id if max-id is greater than zero."
  [options batch-size max-id]
  (let [base-options {"count" batch-size}]
    {:query-params (merge base-options options (when (pos? max-id) {"max_id" max-id}))}))

(defn find-lowest-id [tweets]
  (if-not (empty? tweets)
    (transduce (map :id) min Long/MAX_VALUE tweets)
    0))

(defn get-next-batch! [store batch-size url options]
  (let [max-id (dec (find-lowest-id @store))
        options (query-params options batch-size max-id)
        next-batch (twit/http-get url options)
        cleaned (if (contains? next-batch :statuses) (:statuses next-batch) next-batch)]
    (swap! store into cleaned)))

(defn get-batch! [num-batches batch-size url options]
  (let [store (atom [])]
    (dotimes [n num-batches]
      (get-next-batch! store batch-size url options))
    @store))



;; TODO: should fix this weird combo of delay and atom...
;; Fetch 5*200 tweets
(def cached-tweets
  (delay (get-batch! 5 200)))


(defn tweets->date
  "Extract create-date from each tweet"
  [tweets]
  (map #(twit/parse-twitter-date (:created_at %)) tweets))


(defn extract-day [date]
  (f/unparse (f/formatter "e") date))

(defn extract-hour [date]
  (f/unparse (f/formatter "HH") date))

(defn group-them [method]
  (if (= method "byDay")
    extract-day
    extract-hour))

(defn group-tweets
  "Takes a list of tweets, groups them based on the hour
  they were sent, and count the frequency of each group.

  Returns a map from hour-of-the-day to number-of-tweets-sent."
  [method tweets]
  (into (sorted-map)
        (frequencies (map (group-them method) (tweets->date tweets)))))


(defn tweet-freq->coords [method tweets]
  (mapv (fn [[k v]] {:x (Integer/parseInt k) :y v})
        (group-tweets method tweets)))





(defn get-tweets [url options]
  (get-batch! 5 200 url options))


(def memo-get-tweets (memoize get-tweets))

(defn get-timeline [user method]
  (let [tweets  (memo-get-tweets "https://api.twitter.com/1.1/statuses/user_timeline.json"
                                 {"screen_name" user})]
    (json/write-str
     {:data (tweet-freq->coords method tweets)
      :user  (get-in tweets [0 :user])})))


(defn get-search [query method]
  (let [tweets  (memo-get-tweets "https://api.twitter.com/1.1/search/tweets.json"
                                 {"q" query})]  ;; TODO: URL encode query??? or is it already fixed?
    (json/write-str
     {:data (tweet-freq->coords method tweets)
      :user  (get-in tweets [0 :user])})))


;; TODO: delete this
(defn get-data []
  (get-timeline "realDonaldTrump" "byHour"))
