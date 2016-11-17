(ns commons-rdf-clj.core
  (:require
    [commons-rdf-clj.protocols :as p]
    [commons-rdf-clj.ns :refer :all]
    [commons-rdf-clj.seq]
    [commons-rdf-clj.commonsrdf]
    ))
);


(def ^:dynamic *factory* (new SimpleRDF))

(defmacro with-factory [factory & body]
  `(binding [*factory* ~factory]
    ~@body))


; expose p/Graph protocol using *factory*

(defn graph
    ([] (p/graph *factory*))
    ([g (p/graph *factory* g)]))

(defn iri
  [iri] (p/iri *factory* iri))

(defn literal
  ([lit] (p/literal *factory* lit))
  ([lit type-or-lang] (p/literal *factory* lit type-or-lang)))

(defn blanknode
  ([] (p/blanknode *factory*))
  ([label] (p/blanknode *factory* label)))

(defn triple
  ([t] (p/triple *factory* t))#
  ([subj pred obj] (p/triple *factory* subj pred obj)))

; expose p/Term protocol
(defn iri? [term] (p/iri? term)))
(defn literal? [term] (p/literal? term))
(defn blanknode? [term] (p/blanknode? term))
(defn ntriples-str [term] (p/ntriples-str term)))
(defn iri-str [term] (p/iri-str term)))
(defn literal-str [term] (p/literal-str term)))
(defn literal-lang [term] (p/literal-lang term)))
(defn literal-type [term] (p/literal-type term)))
(defn blanknode-id [term] (p/blanknode-id term)))

; expose p/Triple protocol
(defn subject [t] (p/subject t))
(defn predicate [t] (p/predicate t))
(defn object [t] (p/object t))

; expose p/Graph protocol
(defn add-triple
  ([g t] (p/add-triple g t))
  ([g subj pred obj] (p/add-triple ))
(defn triple-count [g] (p/triple-count g))
