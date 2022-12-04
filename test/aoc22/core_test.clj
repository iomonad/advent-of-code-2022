(ns aoc22.core-test
    (:require [clojure.test :refer :all]
              [aoc22.core :refer :all]))

(deftest aoc22-test
  (testing "day1"
    (is (= (day1 "day1") 68775)
        (= (day1-bis "day1") 202585)))
  (testing "day2"
    (is (= (day2 "day2") 14163)
        (= (day2-bis "day2") 12091)))
  (testing "day3"
    (is (= (day3 "day3") 7845)
        (= (day3-bis "day3") 2790)))
  (testing "day4"
    (is (= (day4 "day4") 500)
        (= (day4-bis "day4") 815))))
