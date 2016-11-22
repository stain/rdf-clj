(ns rdf
  (:import
    (org.apache.commons.rdf.simple SimpleRDF))
  (:require
    [rdf.protocols :as p]
    [rdf.ns]
    [rdf.seq]
    [rdf.commonsrdf]))


(def ^:dynamic *rdf* (new SimpleRDF))

(defmacro with-rdf [r & body]
  `(binding [*rdf* ~r]
    ~@body))


; expose p/Graph protocol using *rdf*

(defn graph
    ([] (p/graph *rdf*))
    ([g] (p/graph *rdf* g)))

(defn iri
  [iri] (p/iri *rdf* iri))

(defn literal
  ([lit] (p/literal *rdf* lit))
  ([lit type-or-lang] (p/literal *rdf* lit type-or-lang)))

(defn blanknode
  ([] (p/blanknode *rdf*))
  ([label] (p/blanknode *rdf* label)))

(defn triple
  ([t] (p/triple *rdf* t))
  ([subj pred obj] (p/triple *rdf* subj pred obj)))

; expose p/Term protocol
(defn iri? [term] (p/iri? term))
(defn literal? [term] (p/literal? term))
(defn blanknode? [term] (p/blanknode? term))
(defn ntriples-str [term] (p/ntriples-str term))
(defn iri-str [term] (p/iri-str term))
(defn literal-str [term] (p/literal-str term))
(defn literal-lang [term] (p/literal-lang term))
(defn literal-type [term] (p/literal-type term))
(defn blanknode-id [term] (p/blanknode-id term))

; expose p/Triple protocol
(defn subject [t] (p/subject t))
(defn predicate [t] (p/predicate t))
(defn object [t] (p/object t))

; expose p/Graph protocol
(defn add-triple
  ([g t] (p/add-triple g t))
  ([g subj pred obj] (p/add-triple g subj pred obj)))
(defn triple-count [g] (p/triple-count g))
