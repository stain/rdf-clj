(ns commons-rdf-clj.core-test
  (:import (org.apache.commons.rdf.simple SimpleRDFTermFactory))
  (:require [clojure.test :refer :all]
            [commons-rdf-clj.core :refer :all]))


(deftest test-create-graph
  (testing "Creating a graph"
    (let [g (create-graph)]
      (is (= 0 (graph-size g))))))
