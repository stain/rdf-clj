(ns commons-rdf-clj.core-test
  (:import (org.apache.commons.rdf.simple SimpleRDFTermFactory))
  (:require [clojure.test :refer :all]
            [commons-rdf-clj.core :refer :all]))


(deftest test-create-graph
  (testing "Creating an empty graph"
    (let [g (create-graph)]
      (is (= 0 (graph-size g))))))

(deftest test-create-iri
  (testing "Creating IRI"
    (let [iri (create-iri "http://example.com/")]
      (is (= "http://example.com/" (.getIRIString iri))))))

(deftest test-create-literal
  (testing "Creating plain literal"
    (let [lit (create-literal "Hello")]
      (is (= "Hello" (.getLexicalForm lit)))))
  (testing "Creating typed literal"
    (let [xsd_double (create-iri "http://www.w3.org/2001/XMLSchema#double")
          lit (create-literal "13.37" xsd_double)]
          (is (= "13.37" (.getLexicalForm lit)))
          (is (= xsd_double (.getDatatype lit)))))
  (testing "Creating language literal"
    (let [lit (create-literal "Hello" "en")]
      (is (= "Hello" (.getLexicalForm lit)))
      (is (= "en" (.get (.getLanguageTag lit)))))))
