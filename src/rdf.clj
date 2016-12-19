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
    {:post [(satisfies? p/RDF %)]}
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

  If argument g is provided, it's triples will be added
  to the graph.

  The type of underlying storage for the graph is determined by the
  *rdf* var, which can be modified with with-rdf."
    ([] (p/graph *rdf*))
    ([g] (p/graph *rdf* g)))

(defn iri
  "Create a IRI."
  [iri]
  {:pre [(not (nil? iri))]
   :post [(p/iri? %)]}
  (p/iri *rdf* iri))

(defn literal
  "Create or return an RDF literal.

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
    {:pre [(not (nil? lit))]
     :post [(p/literal? %)]}
    (p/literal *rdf* lit))
  ([lit type-or-lang]
    {:pre [(not (nil? lit))
           (or (p/iri? type-or-lang)
               (string? type-or-lang)
               (keyword? type-or-lang))]
     :post [(p/literal? %)]}
    (p/literal *rdf* lit type-or-lang))
  ([lit type lang]
    {:pre [(not (nil? lit))
           (p/iri? type)
           (or (string? lang) (keyword? lang))]
     :post [(p/literal? %)]}
    (p/literal *rdf* lit type lang)))

(defn blanknode
  "Create a blank node.  If the argument label is provided,
  it may be used to locally identify the blank node."
  ([]
    {:post [(p/blanknode? %)]}
    (p/blanknode *rdf*))
  ([label]
    {:post [(p/blanknode? %)]}
    (p/blanknode *rdf* label)))


(defn term?
  "Return true if t is an RDF term (IRI, blank node or string)"
  [t]
    (boolean (or (p/iri? t) (p/blanknode? t) (p/literal? t))))

(defn triple? [t]
  "Return true if t is an RDF triple.
  The triple must have a valid subject, predicate and object."
  (boolean (and
    (p/subject t) (p/predicate t) (p/object t))))

(defn graph? [g]
  "Return true if g can be accessed as an RDF graph"
  (satisfies? p/Graph g))

(defn triple
  "Create an RDF triple, either from an existing triple t,
  or for a new triple given as subj pred obj.

  Example:
      (triple (blanknode) (iri \"http://schema.org/name\") (literal \"Example\"))
  "
  ([t]
    {:pre [(triple? t)]
     :post [(triple? %)]}
    (p/triple *rdf* t))
  ([subj pred obj]
    {:pre [(or (p/iri? subj) (p/blanknode? subj))
           (p/iri? pred)
           (term? obj)]
    :post [(triple? %)]}
    (p/triple *rdf* subj pred obj)))

; expose p/Triple protocol
(defn subject [t]
  {:post [(or (p/iri? %) (p/blanknode? %))]}
  (p/subject t))
(defn predicate [t]
  {:post [(p/iri? %)]}
  (p/predicate t))
(defn object [t]
  {:post [(term? %)]}
  (p/object t))

; expose p/Term protocol
(defn iri?
  "Return true if term is an IRI"
  [term]
  (boolean (p/iri? term)))

(defn literal?
  "Return true if term is a literal"
  [term]
  (boolean (p/literal? term)))

(defn blanknode?
  "Return true if term is a blank node"
  [term]
  (boolean (p/blanknode? term)))

(defn ntriples-str
  "Return the N-Triples representation of the RDF term or triple t

  Examples:
      (ntriples-str (literal \"Hello\" :en)) ; => \"Hello\"@en
      (ntriples-str t) ; => _:b0 <http://example.com/> \"Hello\" .
  "
  [t]
  {:post [(string? %)]}
  (if (term? t)
    (p/ntriples-str t)
    (let [s (p/ntriples-str (subject t))
          p (p/ntriples-str (predicate t))
          o (p/ntriples-str (object t))]
          (str s " " p " " o " ."))))

(defn iri-str
  "Return the IRI string of i"
  [i]
  {:post [(string? %)]}
  (p/iri-str i))

(defn literal-str
  "Return the lexical string of RDF literal"
  [lit]
  {:post [(string? %)]}
   (p/literal-str lit))

(defn literal-lang
  "Return the language tag of RDF literal,
  or nil if the literal has no language."
  [lit]
  {:post [(or (nil? %) (string? %))]}
  (p/literal-lang lit))

(defn literal-type
  "Return the language type IRI of RDF literal"
  [lit]
  {:post [(p/iri? %)]}
  (p/literal-type lit))

(defn blanknode-ref
  "Return a reference string for blank node"
  [lit]
  {:post [(string? %)]}
  (p/blanknode-ref lit))


; expose p/Graph protocol
(defn add-triple
  "Return a graph with the triple added"
  ([g t]
    {:pre [(triple? t)]
     :post [(satisfies? p/Graph %)]}
    (p/add-triple g t))
  ([g subj pred obj]
    {:pre [(or (iri? subj) (blanknode? subj))
           (iri? pred)
           (satisfies? p/Term obj)]
     :post [(satisfies? p/Graph %)]}
     (p/add-triple g subj pred obj)))

(defn triple-count
  "Return count of triples in graph"
  [g]
  {:post [(<= 0 %)]}
  (p/triple-count g))
