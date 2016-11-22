(ns rdf-test
  (:import (org.apache.commons.rdf.simple SimpleRDF))
  (:require [clojure.test :refer :all]
            [rdf :refer :all]))


(deftest test-create-graph
  (testing "Creating an empty graph"
    (let [g (graph)]
      (is (= 0 (triple-count g))))))

(deftest test-create-iri
  (testing "Creating IRI"
    (let [iri (iri "http://example.com/")]
      (is (= "http://example.com/" (iri-str iri))))))

(deftest test-as-iri
  (testing "as-iri from IRI"
    (let [i (iri "http://example.com/already-iri")]
      (is (identical? i (iri i)))))
  (testing "iri from String"
    (is (= "http://example.com/was-string"
             (iri-str (iri "http://example.com/was-string")))))
  (testing "as-iri from java.net.URI"
    (is (= "http://example.com/was-uri")
              (iri-str (iri (java.net.URI/create "http://example.com/was-uri")))))
  (testing "as-iri from symbol"
    (is (= "urn:uuid:9168e8bf-9399-4335-af2d-141d100fbcc1"
             (iri-str (iri 'urn:uuid:9168e8bf-9399-4335-af2d-141d100fbcc1))))))


(deftest test-create-literal
  (testing "Creating plain literal"
    (let [lit (literal "Hello")]
      (is (= "Hello" (literal-str lit)))))
  (testing "Creating typed literal"
    (let [xsd_double (iri "http://www.w3.org/2001/XMLSchema#double")
          lit (literal "13.37" xsd_double)]
          (is (= "13.37" (literal-str lit)))
          (is (= xsd_double (literal-type lit)))))
  (testing "Creating language literal"
    (let [lit (literal "Hello" "en")]
      (is (= "Hello" (literal-str lit)))
      (is (= "en" (literal-lang lit))))))

(deftest test-create-triple
  (testing "Creating triple"
    (let [subj (iri "http://example.com/ex1")
          pred (iri "http://example.com/says")
          obj (literal "Hello")
          triple (triple subj pred obj)]
          (is (= subj (subject triple)))
          (is (= pred (predicate triple)))
          (is (= obj (object triple))))))
