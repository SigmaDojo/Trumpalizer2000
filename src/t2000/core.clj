(ns t2000.core
  (:require [t2000.twitlib :as twit]
            [clj-time.format :as f]
            [clojure.data.json :as json]))

(def ^:dynamic *URL*
  "https://api.twitter.com/1.1/statuses/user_timeline.json")


(def tweet-store (atom []))

(defn- query-params
  "Return query params for twitter HTTP query.
  Include max_id if max-id is greater than zero."
  [batch-size max-id]
  (let [base-options {"screen_name" "realDonaldTrump"
                      "count" batch-size}]
    {:query-params (merge base-options (when (pos? max-id) {"max_id" max-id}))}))

(defn find-lowest-id [tweets]
  (if-not (empty? tweets)
    (transduce (map :id) min Long/MAX_VALUE tweets)
    0))

(defn get-next-batch! [batch-size]
  (let [max-id (dec (find-lowest-id @tweet-store))
        options (query-params batch-size max-id)
        next-batch (twit/http-get *URL* options)]
    (swap! tweet-store into next-batch)
    (count next-batch)))

(defn get-batch! [num-batches batch-size]
  (dotimes [n num-batches]
    (get-next-batch! batch-size))
  @tweet-store)



;; TODO: should fix this weird combo of delay and atom...
;; Fetch 5*200 tweets
(def cached-tweets
  (delay (get-batch! 5 200)))


(defn tweets->date
  "Extract create-date from each tweet"
  [tweets]
  (map #(twit/parse-twitter-date (:created_at %)) tweets))


;; (def empty-freq  (zipmap (map #(format "%02d" %) (range 24)) (repeat 0)))
(def empty-freq {"00" 0  "01" 0  "02" 0  "03" 0
                 "04" 0  "05" 0  "06" 0  "07" 0
                 "08" 0  "09" 0  "10" 0  "11" 0
                 "12" 0  "13" 0  "14" 0  "15" 0
                 "16" 0  "17" 0  "18" 0  "19" 0
                 "20" 0  "21" 0  "22" 0  "23" 0})


(defn extract-hour [date]
  (f/unparse (f/formatter "HH") date))


(defn group-tweets
  "Takes a list of tweets, groups them based on the hour
  they were sent, and count the frequency of each group.

  Returns a map from hour-of-the-day to number-of-tweets-sent."
  [tweets]
  (into (sorted-map)
        (merge empty-freq
               (frequencies (map extract-hour (tweets->date tweets))))))


(defn tweet-freq->coords [tweets]
  (mapv (fn [[k v]] {:x k :y v})
        (group-tweets tweets)))


;; TODO: I don't want to send :type "barchart" - that's for the UI to decide.
;; I just want to send data.
(defn get-data []
  (json/write-str
   {:type "barchart"
    :data (tweet-freq->coords @cached-tweets)}))

