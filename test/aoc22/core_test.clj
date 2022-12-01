(ns aoc22.core-test
  (:require [clojure.test :refer :all]
            [aoc22.core :refer :all]))


(deftest aoc22-test
  (testing "day1"
    (is (= (day1 "day1") 68775)
        (= (day1-bis "day1") 202585))))
