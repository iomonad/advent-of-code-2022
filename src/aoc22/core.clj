(ns aoc22.core
  (:require [aoc22.utils :refer [file->seq safe-parseint sum]]
            [clojure.core.match :as m]
            [clojure.string :as str]
            [clojure.set :as set]
            [clojure.java.io :as io]
            [criterium.core :refer [bench with-progress-reporting
                                    quick-bench]]))

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

(def rotate-chars
  (partial map (fn [c]
                 (if (Character/isUpperCase c)
                   (+ 0x1B (- (int c) 0x41))
                   (- (int c) 0x60)))))

(defn day3
  [file]
  (->> (file->seq file)
       (mapv (fn [x]
              (mapv (partial map identity)
                   [(subs x 0 (/ (count x) 2))
                    (subs x (/ (count x) 2) (count x))])))
       (map (fn [[l r]] (apply str (set/intersection (set l) (set r)))))
       (mapcat rotate-chars)
       sum))

(defn day3-bis
  [file]
  (->> (file->seq file)
       (map (partial map identity))
       (partition-all 3)
       (map (fn [[a b c]] (apply str (set/intersection (set a) (set b) (set c)))))
       (mapcat rotate-chars)
       sum))

(comment
  (day3 "day3")
  (day3-bis "day3"))

;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
;;; Day4
;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

(defn day4
  [file]
  (->> (file->seq file)
       (map #(partition 2 (map read-string (rest (re-find #"(\d+)-(\d+),(\d+)-(\d+)" %)))))
       (reduce (fn [acc [[a b] [y z]]]
                 (m/match [(<= a y) (>= b z) (>= a y) (<= b z)]
                          [true    true    _       _] (inc acc)
                          [_       _      true    true] (inc acc)
                          :else acc)) 0)))

(defn day4-bis
  [file]
  (->> (file->seq file)
       (map #(partition 2 (map read-string (rest (re-find #"(\d+)-(\d+),(\d+)-(\d+)" %)))))
       (filter (fn [[[a b] [y z]]] (if (<= a y) (>= b y) (<= a z))))
       count))

(comment
  (day4 "day4")
  (day4-bis "day4"))

;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
;;; Day5
;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

(defn day5
  [file cycle-fn]
  (let [body (file->seq file)
        stack (->> (take 9 body)
                   (map (partial keep-indexed #(when (odd? %1) %2)))
                   (map (partial keep-indexed #(when (even? %1) %2)))
                   (apply map list)
                   (map (partial remove #(Character/isWhitespace %)))
                   (reduce (fn [acc s]
                             (assoc acc (read-string (str (last s)))
                                    (drop-last s)))
                           (sorted-map)))]
    (->> (drop 10 body)
         (map (fn [eip]
                (map read-string
                     (rest (re-find #"move (\d+) from (\d+) to (\d+)" eip)))))
         ((fn [instructions]
            (loop [s stack
                   i (apply list instructions)]
              (if (empty? i)
                (apply str (map first (vals s)))
                (let [[c f t] (peek i)
                      tm (cycle-fn (take c (s f)))
                      aus (-> (update s t (partial concat tm))
                              (update f (partial drop c)))]
                  (recur aus (pop i))))))))))

(comment
  (time (day5 "day5" reverse))
  (time (day5 "day5" identity)))

;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
;;; Day6
;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

(defn day6
  [file n]
  (->> (slurp (io/resource file))
       (partition n 1)
       (map set)
       (map count)
       (map-indexed vector)
       (sort-by second >)
       first
       sum))

(comment
  (quick-bench (day6 "day6" 4))
  (quick-bench (day6 "day6" 14)))
