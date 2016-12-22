(ns rdf.rdf4j
  (:import
    (clojure.lang Seqable)
    (java.util ServiceLoader)
    (org.apache.commons.rdf.api Graph IRI Triple RDF RDFTerm Literal BlankNode)
    (org.apache.commons.rdf.simple SimpleRDF)
    (org.apache.commons.rdf.rdf4j RDF4J)
    (org.eclipse.rdf4j.model Statement Value Model)
    (org.eclipse.rdf4j.repository Repository)
    )
  (:require
    [rdf.utils :refer [if-instance]]
    [rdf.protocols :as p]
    [rdf.commonsrdf :as c]
    ))

(def ^:dynamic *rdf4j* (RDF4J.))

(defn ->triple [^Statement statement]
  (.asTriple *rdf4j* statement))
;
(defn ->statement ^Statement [triple]
  (.asStatement *rdf4j* (p/triple *rdf4j* triple)))

(defn ->term [^Value value]
  (.asRDFTerm *rdf4j* value))

(defn ->value ^Value [term]
  (.asValue *rdf4j* (p/term *rdf4j* term)))



(defprotocol Graphable
  (->graph [model-or-repository]))

(extend-type Object
  Graphable
  ;  Already a graph? Done!
    (->graph [graph] (if (satisfies? p/Graph graph)
      graph  ; return as-is
      (throw (Exception. "Unsupported graph type: " (type graph))))))

; Wrap RDF4J repository as Commons RDF Graph
(extend-type Repository
  Graphable
    (->graph [repository] (.asGraph *rdf4j* repository))
  p/Graph
    (add-triple
      ([g t] (p/add-triple (->graph g) t))
      ([g subj pred obj] (p/add-triple (->graph g) subj pred obj)))
    (remove-triple
      ([g t] (p/remove-triple (->graph g) t))
      ([g subj pred obj] (p/remove-triple (->graph g) subj pred obj)))
    (contains-triple?
      ([g t] (p/contains-triple? (->graph g) t))
      ([g subj pred obj] (p/contains-triple? (->graph g) subj pred obj)))
    (triple-count [g] (p/triple-count (->graph g))))

; Wrap RDF4J model as Commons RDF Graph
(extend-type Model
  Graphable
    (->graph [model] (.asGraph *rdf4j* model))
  p/Graph
    (add-triple
      ([g t] (p/add-triple (->graph g) t))
      ([g subj pred obj] (p/add-triple (->graph g) subj pred obj)))
    (remove-triple
      ([g t] (p/remove-triple (->graph g) t))
      ([g subj pred obj] (p/remove-triple (->graph g) subj pred obj)))
    (contains-triple?
      ([g t] (p/contains-triple? (->graph g) t))
      ([g subj pred obj] (p/contains-triple? (->graph g) subj pred obj)))
    (triple-count [g] (p/triple-count (->graph g))))
