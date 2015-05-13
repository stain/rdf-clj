(ns commons-rdf-clj.graph
  (:import
    (org.apache.commons.rdf.api Graph IRI)
    (java.util UUID)
    (org.apache.commons.rdf.simple SimpleRDFTermFactory))
    )

(defn is-iri? [obj]
  (nil? (:iri obj)))

(defn is-literal? [obj]
  (nil? (:literal obj)))

(defn is-bnode? [obj]
    (nil? (:bnode obj)))

;(defn is-triple? [obj]
;   ((map nil? (map))

(defn create-iri [iri-str]
  { :iri iri-str})


(defn- uuid []
  (str (UUID/randomUUID)))

(def ^:dynamic *salt* (uuid))

;; todo: (with-salt ?)

(defn create-bnode
  ([]      { :bnode  (uuid) })
  ([name]  { :bnode (str *salt* "#" name)}))


(def XSD_STRING (create-iri "http://www.w3.org/2001/XMLSchema#string"))
(def RDFS_LANGSTRING (create-iri "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"))

(defn create-literal
  ([literal]
    {:literal literal :datatypei XSD_STRING})
  ([literal lang-or-type]
    (if (is-iri? lang-or-type)
      {:literal literal :datatype lang-or-type}
      {:literal literal :datatype RDFS_LANGSTRING :lang lang-or-type})))

(defn create-triple [s p o]
  { :subject s :predicate p :object o})

(defn create-graph []
  (hash-set))

;(defn )
