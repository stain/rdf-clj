(ns commons-rdf-clj.core
  (:import
    (org.apache.commons.rdf.api Graph)
    (org.apache.commons.rdf.simple SimpleRDFTermFactory))
  )


(def ^:dynamic *factory* (SimpleRDFTermFactory.))

(defmacro with-factory [factory & body]
  `(binding [*factory* ~factory]
    ~@body))

(defn create-graph []
  (.createGraph *factory*))


(defn graph-size [^Graph g]
  (.size g))
