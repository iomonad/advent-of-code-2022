(ns aoc22.core
  (:require [aoc22.utils :refer [file->seq safe-parseint sum]]
            [clojure.core.match :as m]
            [clojure.string :as str]))

;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
;;; Day1
;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

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

(comment
  (time (day1 "day1"))
  (time (day1-bis "day1")))

;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
;;; Day1
;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

;;; ROCK    A | X
;;; LEAF    B | Y
;;; CISSOR  C | Z

(defn day2
  [file]
  (->> (file->seq file #(str/split % #" "))
       (map (partial map keyword))
       (reduce (fn [acc [elf choice]]
                 (+ acc
                    (m/match [elf choice]
                             [:A  :X] (+ 3 1)
                             [:A  :Y] (+ 6 2)
                             [:A  :Z] (+ 0 3)
                             [:B  :X] (+ 0 1)
                             [:B  :Y] (+ 3 2)
                             [:B  :Z] (+ 6 3)
                             [:C  :X] (+ 6 1)
                             [:C  :Y] (+ 0 2)
                             [:C  :Z] (+ 3 3)))
                 ) 0)))

(comment
  (time (day2 "day2"))
  )
