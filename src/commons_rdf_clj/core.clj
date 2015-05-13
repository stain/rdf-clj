(ns commons-rdf-clj.core
  (:import
    (org.apache.commons.rdf.api Graph IRI)
    (org.apache.commons.rdf.simple SimpleRDFTermFactory))
  )


(def ^:dynamic *factory* (new SimpleRDFTermFactory))

(defmacro with-factory [factory & body]
  `(binding [*factory* ~factory]
    ~@body))

(defn create-graph []
  (.createGraph *factory*))

(defn create-iri [iri-string]
  (.createIRI *factory* iri-string))

(defn as-iri [iri]
  (if (instance? IRI iri) iri
    (create-iri (str iri))))

(defn create-literal
  ([literal]
    (.createLiteral *factory* literal))
  ([literal lang-or-type]
    (.createLiteral *factory* literal lang-or-type)))

(defn create-triple [subject predicate object]
  (.createTriple *factory* subject predicate object))

(defn add-triple [graph triple]
  (.add graph triple))

(defn graph-size [^Graph graph]
  (.size graph))

;(defn )
