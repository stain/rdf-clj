(ns commons-rdf-clj.types
  (:import
    (org.apache.commons.rdf.api Graph IRI RDFTermFactory)
    (java.util UUID)
    (org.apache.commons.rdf.simple SimpleRDFTermFactory))
    )



(defprotocol Factory
  (graph
    [f]
    [f g])
  (iri [f iri])
  (literal
    [f lit]
    [f lit type-or-lang])
  (blanknode
    [f]
    [f name])
  (triple
    [f t]
    [f subj pred obj])
)

(defprotocol PGraph
  (add-triple
    [g t]
    [g subj pred obj])
  (graph-seq [g])
)

(defprotocol PTriple
  (subject [t])
  (predicate [t])
  (object [t])
)


(defmacro if-instance [type value & body]
  `(if (instance? ~type ~value) ~value
    ~@body))

(extend-type Graph
  PGraph
    (add-triple [g tripl]
      (.add g (triple tripl))
      g)
    (add-triple [g subj pred obj]
      (.add g (triple subj pred obj)
      g))
)


(extend-type clojure.lang.Seqable
  PGraph
  (add-triple [g tripl])
    (conj g (triple tripl))
)

(extend-type Triple
  PTriple
    (subject [t] (.getSubject t))
    (predicate [t] (.getPredicate t))
    (object [t] (.getObject t)))

(extend-type clojure.lang.IPersistentMap
  PTriple
    (subject [m] (:subject m))
    (predicate [m] (:predicate m))
    (object [m]) (:object m))



(extend-type RDFTermFactory
  Factory
    (graph [f] (.createGraph f))
    (graph [f g] (if-instance Graph g
      (reduce add-triple (graph f) t)))
    (iri [f iri] (if-instance IRI iri (.createIRI f (str iri))))
    (literal [lit] (if-instance Literal lit) (.createLiteral f (str lexical)))
    (literal [lit type-or-lang] (.createLiteral f
        (str lexical)
        (if-instance String type-or-lang
          (iri type-or-lang))))
    (blanknode [] (.createBlankNode f))
    (blanknode [name] (.createBlankNode f (str name)))
    (triple [t] (if-instance Triple t (.createTriple f (subject t) (predicate t) (object t))))
    (triple [subj pred obj] (.createTriple f subj pred obj)) ;; TODO: Type conversion
)
