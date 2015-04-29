(ns commons-rdf-clj.core)


(def ^:dynamic *factory* (. SimpleRDFTermFactory))

(defmacro with-factory [factory & body]
  `(binding [*factory* ~factory]
    ~@body))

(defn create-graph []
  (. factory createGraph))


(defn graph-size [g]
  (. g size))
