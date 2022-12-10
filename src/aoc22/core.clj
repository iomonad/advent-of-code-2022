(ns aoc22.core
  (:require [aoc22.utils :refer [file->seq safe-parseint sum
                                 recursive-sel]]
            [clojure.core.match :as m]
            [clojure.string :as str]
            [clojure.set :as set]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint pprint-indent]]
            [clojure.core.matrix :as mm]
            [com.rpl.specter :as sp]
            [criterium.core :refer [bench with-progress-reporting
                                    quick-bench]]
            [instaparse.core :as insta]
            [clojure.walk :as walk]))

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

;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
;;; Day7
;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

#_(defn day7
    [file]
    (->> (file->seq file)
         ((fn [instructions]
            (loop [[eip & tail]  instructions
                   tree          []
                   stack         {}]
              (let [cursor (cond
                             (= eip "$ cd ..")               :popd
                             (str/starts-with? eip "$ cd")   :mdir
                             (str/starts-with? eip "$ ls")   :nop
                             (Character/isDigit (first eip)) :insert
                             :else                           :nop)]
                (m/match [(and eip (nil? tail)) cursor]
                         [true _] stack
                         [_    :nop]    (recur tail tree stack)
                         [_    :ls]     (recur tail tree stack)
                         [_    :mdir]   (recur tail (concat tree [(str/replace eip "$ cd" "")]) stack)
                         [_    :popd]   (recur tail (drop-last tree) stack)
                         [_    :insert] (recur tail tree
                                               (update-in stack (concat tree [:wid]) concat
                                                          ((comp vector read-string first) (str/split eip #"  ")))))))))
         (map (partial sum))
         (filter (partial >= 100000))
         sum))

(def day7-grammar
  "S = cd | ls | dir | file

    cd = <'$ cd '> path
    ls = <'$ ls'>
    dir = <'dir '> path
    file = filesize <' '> path

    <filesize> = #'\\d+'
    <path> = #'[a-zA-Z0-9./]+'")

(defn day7
  [file]
  (letfn [(idx-depth [workdir]
            (if (= 1 (count workdir)) (identity workdir)
                (cons workdir (idx-depth (pop workdir)))))]
    (let [parser (insta/parser day7-grammar)]
      (->> (file->seq file)
           (map parser)
           (map second)
           (reduce (fn [{:keys [results workdir] :as acc} instruction]
                     (m/match instruction
                              [:cd   ".."  ] (update acc :workdir pop)
                              [:cd   dir   ] (update acc :workdir conj dir)
                              [:file size _] (assoc acc :results
                                                    (reduce (fn [acc c]
                                                              (update acc c (fnil + 0) (read-string size))) results
                                                            (idx-depth workdir)))
                              ;; NOP on other cases
                              :else acc))
                   {:workdir ["/"]
                    :results {}})
           :results
           (map val)
           (filter (partial >= 100000))
           sum))))


(defn day7-bis
  [file]
  (letfn [(idx-depth [workdir]
            (if (= 1 (count workdir)) (identity workdir)
                (cons workdir (idx-depth (pop workdir)))))]
    (let [parser (insta/parser day7-grammar)
          {:keys [results]}
          (->> (file->seq file)
               (map parser)
               (map second)
               (reduce (fn [{:keys [results workdir] :as acc} instruction]
                         (m/match instruction
                                  [:cd   ".."  ] (update acc :workdir pop)
                                  [:cd   dir   ] (update acc :workdir conj dir)
                                  [:file size _] (assoc acc :results
                                                        (reduce (fn [acc c]
                                                                  (update acc c (fnil + 0) (read-string size))) results
                                                                (idx-depth workdir)))
                                  ;; NOP on other cases
                                  :else acc))
                       {:workdir ["/"]
                        :results {}}))
          max-at-root (- 30000000 (- 70000000 (get results "/")))]
      (->> results
           (map val)
           (filter #(>= % max-at-root))
           sort
           first))))

(comment
  (pprint (day7 "day7"))
  (pprint (day7-bis "day7")))

;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
;;; Day8
;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

(defn day8
  [file]
  (->> (file->seq file)
       (mapv (partial mapv (comp read-string str identity)))
       ))

(comment
  (pprint (day8 "test"))
  )


;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
;;; Day10
;;; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


(defn day10
  [file]
  (let [decomposition
        (->> (file->seq file #(str/split % #" "))
             (reduce (fn [acc [i n]]
                       (condp = i
                         "addx" (conj acc 0 (read-string n))
                         (conj acc 0))) [])
             (reductions + 1))]
    (->> (range 20 221 40)
         (map (fn [idx] (* idx (nth decomposition (dec idx)))))
         sum)))


;;; https://gist.github.com/xero/59c8a62ff1fe564264f9
(defn day10-bis
  [file]
  (let [decomposition
        (->> (file->seq file #(str/split % #" "))
             (reduce (fn [acc [i n]]
                       (condp = i
                         "addx" (conj acc 0 (read-string n))
                         (conj acc 0))) [])
             (reductions + 1))]
    (->> (map-indexed
          (fn [i x]
            (cond
              (<= (dec x) (mod i 40) (+ 1 x)) "░"
              :else " "))
          decomposition)
         (partition 40)
         (map (partial apply str))
         (clojure.pprint/pprint))))

(comment
  (day10 "day10")
  (day10-bis "day10") ;; => RGLRBZAU
  )
