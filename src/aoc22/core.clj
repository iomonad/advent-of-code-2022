(ns aoc22.core
  (:require [aoc22.utils :refer [file->seq safe-parseint sum]]
            [clojure.core.match :as m]
            [clojure.string :as str]
            [clojure.set :as set]))

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
;;; Day2
;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

(defn day2
  "Can be implemented with bitmasks and factorized with idx,
  but don't have time to have an intelligent approach"
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
                             [:C  :Z] (+ 3 3))))
               0)))

(defn day2-bis
  [file]
  (let [win {:A :Y :B :Z :C :X}
        loose {:A :Z :B :X :C :Y}
        ref {:X 1 :Y 2 :Z 3 :A 1 :B 2 :C 3}]
    (->> (file->seq file #(str/split % #" "))
         (map (partial map keyword))
         (reduce (fn transpose [acc [elf choice]]
                   (+ acc
                      (m/match [elf choice]
                               [_   :X] ((comp ref loose) elf)
                               [_   :Y] (+ 3 (ref elf))
                               [_   :Z] (+ 6 ((comp ref win) elf))))) 0))))

(comment
  (time (day2 "day2"))
  (time (day2-bis "day2")))

;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
;;; Day3
;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

(defn day3
  [file]
  (->> (file->seq file)
       (mapv (fn [x]
              (mapv (partial map identity)
                   [(subs x 0 (/ (count x) 2))
                    (subs x (/ (count x) 2) (count x))])))
       (map (fn [[l r]] (apply str (set/intersection (set l) (set r)))))
       (mapcat (partial map (fn [c]
                              (if (Character/isUpperCase c)
                                (+ 0x1B (- (int c) 0x41))
                                (- (int c) 0x60)))))
       sum))


(comment
  (day3 "day3")
  )

