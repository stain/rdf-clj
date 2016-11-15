(ns commons-rdf-clj.types
  (:import
    (clojure.lang Seqable)
    (org.apache.commons.rdf.api Graph IRI Triple RDF RDFTerm Literal BlankNode)
    (java.util UUID)
    (org.apache.commons.rdf.simple SimpleRDF))
    )


(def ^:dynamic *factory* (new SimpleRDF))

(defmacro with-factory [factory & body]
  `(binding [*factory* ~factory]
    ~@body))

(defmacro if-instance [type value & body]
  `(if (instance? ~type ~value) ~value
    ~@body))

(defn rdf-namespace [base & names]
  (apply hash-map (mapcat #(list %1 {:uri (str base (name %1))})
          names)))

(def ns-rdf (rdf-namespace "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  :langString :HTML :PlainLiteral :XMLLiteral))

(def ns-xsd (rdf-namespace "http://www.w3.org/2001/XMLSchema#"
  :string   :normalizedString

  :anyURI

  :base64Binary :hexBinary
  :byte :unsignedByte

  :boolean

  :decimal
  :double :float
  :long :unsignedLong
  :short :unsignedShort

  ; date/time/duration
  :date :dateTime :dayTimeDuration :duration
  :gDay :gMonth :gMonthDay :gYear :gYearMonth
  :time

  :int :unsignedInt
  :integer
  :negativeInteger :nonPositiveInteger
  :nonNegativeInteger :positiveInteger

  ; XML stuff?
  :Name :NCName :NMTOKEN :token :language
))

(defprotocol PGraph
  (add-triple
    [g t]
    [g subj pred obj])
  (graph-seq [g])
  (triple-count [g])
)

(extend-type Graph
  PGraph
    (add-triple [g tripl]
      (.add g tripl) ;; TODO: Type cohersion
      g)
    (add-triple [g subj pred obj]
      (.add g subj pred obj) ;; TODO: Type cohersion
      g)
    (triple-count [g]
      (.size g))
)


(extend-type clojure.lang.Seqable
  PGraph
    (add-triple [g tripl]
      (conj g tripl))
    (add-triple [g s p o]
        (conj g {:subject s :predicate p :object o}))
    (triple-count [g] (count g))
)

(defprotocol PTriple
  (subject [t])
  (predicate [t])
  (object [t])
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
    (object [m] (:object m)))

;
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

(extend-type RDF
  Factory
      (graph
        ([f] (.createGraph f))
        ([f g] (if-instance Graph g
          (reduce add-triple (graph f) g))))
    (iri [f iri] (if-instance IRI iri (.createIRI f (str iri))))
    (literal
      ([f lit]
        (if-instance Literal lit) (.createLiteral f (str lit)))
      ([f lit type-or-lang]
        (.createLiteral f (str lit)
          (if-instance String type-or-lang
            (iri f type-or-lang)))))
    (blanknode
      ([f]
        (.createBlankNode f))
      ([f name]
        (.createBlankNode f (str name))))
    (triple
      ([f t]
        (if-instance Triple t (.createTriple f (subject t) (predicate t) (object t))))
      ([f subj pred obj] (.createTriple f subj pred obj))) ;; TODO: Type conversion
)

(defprotocol Term
  (iri? [term])
  (literal? [term])
  (blanknode? [term])

  (ntripes-str [term])

  (iri-str [term])

  (literal-str [term])
  (literal-lang [term])
  (literal-type [term])

  (blanknode-id [term])
)


(extend-type RDFTerm Term
  ; default implementations are all false
  ; rather than using instanceof, as each
  ; of them extend-type separately
  (iri? [term] false)
  (literal? [term] false)
  (blanknode? [term] false)
  (ntriples-str [term] (.ntriplesString term)))


(extend-type IRI Term
  (iri? [iri] true)
  (iri-str [iri] (.getIRIString iri)))

(extend-type Literal Term
  (literal? [literal] true)
  (literal-str [literal] (.getLexicalForm literal))
  (literal-lang [literal] (.orElse (.getLanguageTag literal) nil))
  (literal-type [literal] (.getDatatype literal)))

(extend-type BlankNode Term
  (blanknode? [bnode] true)
  (blanknode-id [bnode] (.uniqueIdentifier bnode)))


(extend-type Seqable Term
  (iri? [term] (contains? term :iri))
  (literal? [term] (contains? term :literal))
  (blanknode? [term] (contains? term :blanknode))

  (ntriples-str [term] (cond (iri? term) (str "<" (:iri term) ">")
                             (blanknode? term) (str "_:" (:blanknode term))
                             (literal? term)
                                  ;; TODO: Escape properly
                                  ;; TODO: language and datatype
                                (str \" (:literal term)  \")))

  (iri-str [iri] (str (:iri iri)))
  (blanknode-id [bnode] (str (:blanknode bnode)))

  (literal-str [lit] (str (:literal lit)))
  (literal-lang [lit] (:language lit)) ;; TODO: Ensure nil or str
  (literal-type [lit] (iri {} (get lit :datatype
    (or ; fall-back when :datatype is not specified
      (and (:language lit) {:iri (ns-rdf :langString)})
      {:iri (ns-xsd :string)}))))
)


(defn- uuid
  ([] (java.util.UUID/randomUUID))
  ([id]
    (if (instance? java.util.UUID id) id ; as-is
      ; otherwise, parse as string
      (java.util.UUID/fromString (str id)))))

(extend-type Seqable Factory
  (graph
    ([f] (set f))
    ([f g] (set g)))

  (iri [f iri] (assoc f :iri (str iri)))

  (literal
    ([f lit] (assoc f :literal (str lit)))
    ([f lit type-or-lang]
      (assoc (literal f lit)
        (if (iri? type-or-lang)
          {:datatype (iri-str type-or-lang)}
          {:language (name type-or-lang)}))))

  (blanknode
    ([f] (assoc f :blanknode (uuid)))
    ([f id] (assoc f :blanknode (name id))))
)
