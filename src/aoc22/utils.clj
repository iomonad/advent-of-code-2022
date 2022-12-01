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
  ([path] (str/split-lines (slurp (io/resource path))))
  ([path modifier-fn]
   (->> (slurp (io/resource path))
        (str/split-lines)
        (map modifier-fn))))

(defn sum [x]
  (apply + x))
