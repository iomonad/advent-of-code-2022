(ns aoc22.utils
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn safe-parseint
  [x]
  (try
    (Integer/parseInt x)
    (catch Exception _
      nil)))

(defn file->seq
  "Convert file to lines lazyseq"
  ([path] (file->seq path identity))
  ([path modifier-fn]
   (->> (slurp (io/resource path))
        (str/split-lines)
        (map modifier-fn))))

(defn sum [x]
  (apply + x))

(defn inclusive-range
  ([] (range))
  ([end] (range (inc end)))
  ([start end] (range start (inc end)))
  ([start end step] (range start (+ end step) step)))

(defn qsort [[head & tail]]
  (when head
    (lazy-cat (qsort (filter #(< % head) tail))
              [head]
              (qsort (remove #(< % head) tail)))))

(defn merge*
  [[f1 & r1 :as l1] [f2 & r2 :as l2]]
  (cond
    (nil? f1) l2
    (nil? f2) l1
    (<= f1 f2) (lazy-seq (cons f1 (merge* r1 l2)))
    (> f1 f2) (lazy-seq (cons f2 (merge* l1 r2)))))

(defn msort [s]
  (if (next s)
    (let [[left right] (split-at (/ (count s) 2) s)]
      (merge* (msort left) (msort right)))
    s))

(defn hsort [s]
  (let [heap (java.util.concurrent.PriorityBlockingQueue. s)]
    (repeatedly (count s) #(.take heap))))

(defn insert [^java.util.LinkedList s x]
  (let [i (.listIterator s (.size s))]
    (while (and (.hasPrevious i) (> (.previous i) x)))
    (.add i x)
    s))

(defn isort [s]
  (seq (reduce insert (java.util.LinkedList.) s)))

(defn treeduce [f s]
  (loop [s (partition-all 2 s)]
    (let [s (map (fn [[x y]] (f x y)) s)]
      (if (< 1 (count s))
        (recur (partition-all 2 s))
        (first s)))))

(defn tsort [s]
  (treeduce merge* (map isort (partition-all 128 s))))
