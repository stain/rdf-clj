  (ns rdf
  (:import
    (org.apache.commons.rdf.api RDF)
    (org.apache.commons.rdf.simple SimpleRDF))
  (:require
    [rdf.utils :as u]
    [rdf.protocols :as p]
    [rdf.ns]
    [rdf.seq]
    [rdf.commonsrdf :as c]))


(defn rdf-impl
  "Select an RDF implementation for given keyword. The implementation
  can be used with *rdf*, with-rdf, as well as the first argument for
  the rdf.protocols/RDF protocol methods.

  The implementations include at least:

    :clojure - Clojure-centric representation using Clojure maps/hash
    :simple - Commons RDF Simple implementation
    :any - Any Commons RDF implementations discovered on class path

  These implementations are also available if the corresponding
  Commons RDF integration modules (and their dependencies) are
  on the classpath:

    :jena - Commons RDF mapping of Apache Jena
    :rdf4j - Commnos RDF mapping of Eclipse RDF4J
    :jsonldjava - Commons RDF mapping of JSONLD-Java

  Called with no arguments this function selects the
  default implementation, currently :clojure"
  ([] (rdf-impl :clojure))
  ([r]
    (cond
      (= :clojure r) (hash-map)
      (= :any r) (c/rdf-impl)
      (keyword? r) (c/rdf-impl r)
      :else r)))


(def ^:dynamic *rdf*
  "Dynamic var of which RDF implementation to use for creating RDF concepts.

  The implementation must be an instance that is supported by the
  rdf.protocols/RDF protocol.

  Use with-rdf to temporarily change the RDF implementation."
  (rdf-impl))

(defmacro with-rdf
  "Modify *rdf* to set which RDF implementation to use.
  The implementation can be either a a keyword (e.g. :simple) or
  an RDF instance, (e.g. rdf-impl :clojure).
  "
  [r & body]
  `(binding [*rdf* (rdf-impl ~r)]
    ~@body))

; expose p/Graph protocol using *rdf*

(defn graph
  "Create a graph of RDF triples.

  If arument g is provided, it's triples will be added
  to the graph.

  The type of underlying storage for the graph is determined by the
  *rdf* var, which can be modified with with-rdf."
    ([] (p/graph *rdf*))
    ([g] (p/graph *rdf* g)))

(defn iri
  "Create a IRI."
  [iri] (p/iri *rdf* iri))

(defn literal
  "Create a literal from a lexical value.

  If only one argument is provided, and the value is a
  (literal?), then it would be returned as-is or mapped to
  the current *rdf* factory. Otherwise, the argument is
  assumed to be a string representing the lexical value of a
  literal with a datatype equivalent to (rdf.ns/xsd :string).

  If the second argument is provided, it should be either
  the datatype iri (e.g. (rdf.ns/xsd :int)),
  or a non-nil language tag, represented as as a string
  or keyword .

  If a third argument is provided, it is the language tag
  (or nil), while the second argument must be a datatype iri.
  "
  ([lit]
      (p/literal *rdf* lit))
  ([lit type-or-lang]
    (p/literal *rdf* lit type-or-lang))
  ([lit type lang]
    (p/literal *rdf* lit type lang)))

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
(defn blanknode-ref [term] (p/blanknode-ref term))

; expose p/Triple protocol
(defn subject [t] (p/subject t))
(defn predicate [t] (p/predicate t))
(defn object [t] (p/object t))

; expose p/Graph protocol
(defn add-triple
  ([g t] (p/add-triple g t))
  ([g subj pred obj] (p/add-triple g subj pred obj)))
(defn triple-count [g] (p/triple-count g))
