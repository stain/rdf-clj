(ns rdf.rdf4j
  (:import
    (clojure.lang Seqable)
    (java.util ServiceLoader)
    (org.apache.commons.rdf.api Graph IRI Triple RDF RDFTerm Literal BlankNode)
    (org.apache.commons.rdf.simple SimpleRDF)
    (org.apache.commons.rdf.rdf4j RDF4J)
    (org.eclipse.rdf4j.model Statement)
    )
  (:require
    [rdf.protocols :as p]
    [rdf.commonsrdf :as c]
    ))

(def ^:dynamic *rdf4j* (RDF4J.))

(defn ->triple [statement]
  (.asTriple *rdf4j* statement))

(extend-type Statement
  p/Triple
    (subject [t] (.getSubject (->triple t)))
    (predicate [t] (.getPredicate (->triple t)))
    (object [t] (.getObject (->triple t))))
