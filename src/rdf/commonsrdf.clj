(ns rdf.commonsrdf
  (:import
    (clojure.lang Seqable)
    (java.util ServiceLoader)
    (org.apache.commons.rdf.api Graph IRI Triple RDF RDFTerm Literal BlankNode)
    (org.apache.commons.rdf.simple SimpleRDF))
  (:require
    [rdf.utils :refer [if-instance]]
    [rdf.protocols :as p]))

(defn- key-for-rdf-impl [rdf]
  (keyword (last (clojure.string/split
    ; Last part of implementation class package name, e.g.
    ; org.apache.commons.rdf.simple.SimpleRDF -> :simple
    (.getName (.getPackage (class rdf)))
     #"\." ))))

(defn- simpleRDF [] (SimpleRDF.))

(defn- termPattern [term]
  (if (nil? term) nil ; return as-is
    (if-instance term RDFTerm ; return as-is
      (cond ; coerce to a Commons RDF instance
        (p/iri? term) (p/iri (simpleRDF) term)
        (p/blanknode? term) (p/blanknode (simpleRDF) term)
        (p/literal? term) (p/literal (simpleRDF) term)))))


(defn- rdf-impls-seq []
  (iterator-seq (.iterator
    (java.util.ServiceLoader/load RDF))))

(defn rdf-impls []
  (apply hash-map (mapcat #(list (key-for-rdf-impl %1) %1)
    (rdf-impls-seq))))

(defn rdf-impl
  ([] (first (rdf-impls-seq)))
  ([k]
    (if (= :simple k) (simpleRDF) ; no need for ServiceLoader and mapping
      (get (rdf-impls) (keyword k)))))


(extend-type Graph
  p/Graph
    (add-triple
      ([g tripl]
        (.add g (p/triple (simpleRDF) tripl)
        g)) ; Return same instance
      ([g subj pred obj]
        (.add g (p/triple (simpleRDF) subj pred obj)
        g)))
    (remove-triple
      ([g tripl]
        (.remove g (p/triple (simpleRDF) tripl))
        g)
      ([g subj pred obj]
        (.remove g (termPattern subj) (termPattern pred) (termPattern obj))
        g))
    (contains-triple?
      ([g tripl]
        (.contains g (p/triple (simpleRDF) tripl)))
      ([g subj pred obj]
        (.contains g (termPattern subj) (termPattern pred) (termPattern obj))))
    (triple-count [g]
      (.size g))
)


(extend-type Triple
  p/Triple
    (subject [t] (.getSubject t))
    (predicate [t] (.getPredicate t))
    (object [t] (.getObject t)))


(extend-type RDF
  p/RDF
    (graph
        ([f] (.createGraph f))
        ([f g] (if-instance Graph g
          (reduce p/add-triple (p/graph f) g))))
    (iri [f iri] (if-instance IRI iri (.createIRI f (p/iri-str iri))))
    (literal
      ([f lit]
        (if-instance Literal lit) (.createLiteral f (p/literal-str lit)))
      ([f lit type-or-lang]
        (.createLiteral f (str lit)
          (if (p/iri? type-or-lang)
            (p/iri f type-or-lang)) ; must be type
            (name type-or-lang))); must be non-nil lang
      ([f lit type lang]
        (if (nil? lang)
          (.createLiteral f (str lit) (p/iri f type))
          (.createLiteral f (str lit) (p/iri f type) (name lang)))))

    (blanknode
      ([f]
        (.createBlankNode f))
      ([f name]
        (.createBlankNode f (str name))))
    (triple
      ([f t]
        (if-instance Triple t (.createTriple f (p/subject t) (p/predicate t) (p/object t))))
      ([f subj pred obj] (.createTriple f subj pred obj))) ;; TODO: Type conversion
)


(extend-type RDFTerm p/Term
  ; Odd case - a non-IRI non-Literal non-BlankNode RDFTerm. Not much
  ; we can support then..
  (iri? [term] false)
  (literal? [term] false)
  (blanknode? [term] false)
  (ntriples-str [term] (.ntriplesString term)))

(extend-type IRI p/Term
  (literal? [term] false)
  (blanknode? [term] false)
  (iri? [iri] true)
  (iri-str [iri] (.getIRIString iri))
  (ntriples-str [term] (.ntriplesString term)))

(extend-type Literal p/Term
  (blanknode? [term] false)
  (iri? [iri] false)
  (literal? [literal] true)
  (literal-str [literal] (.getLexicalForm literal))
  (literal-lang [literal] (.orElse (.getLanguageTag literal) nil))
  (literal-type [literal] (.getDatatype literal))
  (ntriples-str [term] (.ntriplesString term)))


(extend-type BlankNode p/Term
  (iri? [iri] false)
  (literal? [literal] false)
  (blanknode? [bnode] true)
  (blanknode-ref [bnode] (.uniqueReference bnode))
  (ntriples-str [term] (.ntriplesString term)))
