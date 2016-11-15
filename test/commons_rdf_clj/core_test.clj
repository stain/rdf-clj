(ns commons-rdf-clj.core-test
  (:import (org.apache.commons.rdf.simple SimpleRDF))
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

(deftest test-as-iri
  (testing "as-iri from IRI"
    (let [iri (create-iri "http://example.com/already-iri")]
      (is (identical? iri (as-iri iri)))))
  (testing "as-iri from String"
    (is (= "http://example.com/was-string"
             (.getIRIString (as-iri "http://example.com/was-string")))))
  (testing "as-iri from java.net.URI"
    (is (= "http://example.com/was-uri")
              (.getIRIString (as-iri (java.net.URI/create "http://example.com/was-uri")))))
  (testing "as-iri from symbol"
    (is (= "urn:uuid:9168e8bf-9399-4335-af2d-141d100fbcc1"
             (.getIRIString (as-iri 'urn:uuid:9168e8bf-9399-4335-af2d-141d100fbcc1))))))


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
      ; Need to (.get the Optional)
      (is (= "en" (.get (.getLanguageTag lit)))))))

(deftest test-create-triple
  (testing "Creating triple"
    (let [subj (create-iri "http://example.com/ex1")
          pred (create-iri "http://example.com/says")
          obj (create-literal "Hello")
          triple (create-triple subj pred obj)]
          (is (= subj (.getSubject triple)))
          (is (= pred (.getPredicate triple)))
          (is (= obj (.getObject triple))))))
