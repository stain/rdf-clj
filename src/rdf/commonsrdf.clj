(ns rdf.commonsrdf
  (:import
    (clojure.lang Seqable)
    (org.apache.commons.rdf.api Graph IRI Triple RDF RDFTerm Literal BlankNode)
    (org.apache.commons.rdf.simple SimpleRDF))
  (:require
    [rdf.utils :refer [if-instance]]
    [rdf.protocols :as p]))


;
(extend-type Graph
  p/Graph
    (add-triple [g tripl]
      (.add g tripl) ;; TODO: Type cohersion
      g)
    (add-triple [g subj pred obj]
      (.add g subj pred obj) ;; TODO: Type cohersion
      g)
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
    (iri [f iri] (if-instance IRI iri (.createIRI f (str iri))))
    (literal
      ([f lit]
        (if-instance Literal lit) (.createLiteral f (str lit)))
      ([f lit type-or-lang]
        (.createLiteral f (str lit)
          (if-instance String type-or-lang
            (p/iri f type-or-lang)))))
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
  ; default implementations are all false
  ; rather than using instanceof, as each
  ; of them extend-type separately
  (iri? [term] false)
  (literal? [term] false)
  (blanknode? [term] false)
  (ntriples-str [term] (.ntriplesString term)))


(extend-type IRI p/Term
  (iri? [iri] true)
  (iri-str [iri] (.getIRIString iri)))

(extend-type Literal p/Term
  (literal? [literal] true)
  (literal-str [literal] (.getLexicalForm literal))
  (literal-lang [literal] (.orElse (.getLanguageTag literal) nil))
  (literal-type [literal] (.getDatatype literal)))

(extend-type BlankNode p/Term
  (blanknode? [bnode] true)
  (blanknode-id [bnode] (.uniqueIdentifier bnode)))
