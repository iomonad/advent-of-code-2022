(ns aoc22.core
  (:require [clojure.java.io :as io]
            [aoc22.utils :refer [file->seq safe-parseint
                                 sum]]
            [clojure.pprint :refer [pprint]]))

;;; Day1

(defn day1
  [file]
  (->> (file->seq file safe-parseint)
       (partition-by nil?)
       (remove (partial every? nil?))
       (map sum)
       (apply max)))

(defn day1-bis
  [file]
  (->> (file->seq file safe-parseint)
       (partition-by nil?)
       (remove (partial every? nil?))
       (map sum)
       (sort >)
       (take 3)
       sum))

(comment (day1 "day1")
         (day1-bis "day1"))

;;; Day2
