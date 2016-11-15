
(ns commons-rdf-clj.types-test
  (:import (org.apache.commons.rdf.simple SimpleRDF))
  (:require [clojure.test :refer :all]
            [commons-rdf-clj.types :refer :all]))


(deftest test-create-graph
  (testing "Creating an empty graph"
    (let [g (graph (new SimpleRDF))]
      (is (= 0 (triple-count g))))))

(deftest test-create-iri
  (testing "Creating IRI"
    (let [iri (iri (new SimpleRDF) "http://example.com/")]
      (is (= "http://example.com/" (iri-str iri))))))
