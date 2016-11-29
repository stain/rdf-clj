(ns rdf.seq
  (:import
    (clojure.lang Seqable Associative)
    (org.apache.commons.rdf.api Graph IRI Triple RDF RDFTerm Literal BlankNode)
    (java.util UUID)
    (java.net URI)
    (org.apache.commons.rdf.simple SimpleRDF))
    (:require
      [rdf.ns :refer [rdf xsd]]
      [rdf.utils :refer [uuid escape-literal]]
      [rdf.protocols :as p]
    )
)

(defn- termPattern [term]
  (if (nil? term) nil ; return as-is
    (cond ; coerce to a Clojure map
      (p/iri? term) (p/iri {} term)
      (p/blanknode? term) (p/blanknode {} term)
      (p/literal? term) (p/literal {} term))))


(extend-type String p/Term
  (iri? [obj] false)
  (blanknode? [obj] false)

  (iri-str [s] s) ; workaround even though (iri? is false)
  (literal? [s] true)
  (literal-str [s] s)
  (literal-lang [s] nil)
  (literal-type [s] (xsd :string))
  (ntriples-str [s] (str \" (escape-literal s) \"))) ; TODO: Escape " etc in str

(extend-type URI p/Term
  (literal? [obj] false)
  (blanknode? [obj] false)
  (iri? [uri] true)
  (iri-str [uri] (str uri))
  (ntriples-str [uri] (str "<" uri ">")))

;
(extend-type clojure.lang.Keyword p/Term
  (literal? [obj] false)
  (blanknode? [obj] false)

  (iri? [kw] (not (nil? (p/iri-str kw))))
  (iri-str [kw] (get-in (ns-publics 'rdf.ns)
    [(keyword (name kw)) :iri]))
  (ntriples-str [kw] (str "<" (p/iri-str kw) ">")))


(extend-type UUID p/Term
  (iri? [obj] true)
  (blanknode? [uuid] true) ; if you want to?
  (literal? [obj] false)
  (blanknode-ref [uuid] (str uuid))
  (iri-str [uuid] (str "urn:uuid:" uuid))
  (ntriples-str [uuid] (str "_:" uuid)))

(extend-type Associative p/Term
  (iri? [term] (contains? term :iri))
  (literal? [term] (contains? term :literal))
  (blanknode? [term] (contains? term :blanknode))

  (ntriples-str [term] (cond (p/iri? term) (str "<" (:iri term) ">")
                             (p/blanknode? term) (str "_:" (:blanknode term))
                             (p/literal? term)
                                  ;; TODO: language and datatype
                                (str \" (escape-literal (:literal term))  \")))

  (iri-str [iri] (str (:iri iri)))
  (blanknode-ref [bnode] (str (:blanknode bnode)))

  (literal-str [lit] (str (:literal lit)))
  (literal-lang [lit] (:language lit)) ;; TODO: Ensure nil or str
  (literal-type [lit] (p/iri {} (get lit :datatype
    (or ; fall-back when :datatype is not specified
      (and (:language lit) {:iri (rdf :langString)})
      (xsd :string)))))
)


(extend-type Seqable
  p/Graph
  (add-triple
    ([g tripl] (conj g (p/triple {} tripl)))
    ([g s p o]
      (conj g {:subject (termPattern s) :predicate (termPattern p) :object (termPattern o)})))
  (remove-triple
    ([g tripl]
      (disj g (p/triple {} tripl)))
    ([g subj pred obj]
       ;; TODO: Handle null pattern
      (disj g (p/triple {} subj pred obj))))
  (contains-triple?
    ([g tripl]
      (.contains g (p/triple {} tripl)))
    ([g subj pred obj]
      (.contains g (termPattern subj) (termPattern pred) (termPattern obj))))
  (triple-count [g] (count g))
)

(extend-type Associative
  p/Triple
    (subject [m] (:subject m))
    (predicate [m] (:predicate m))
    (object [m] (:object m)))

;
(extend-type Seqable
  p/Triple
    (subject [t] (nth t 0))
    (predicate [t] (nth t 1))
    (object [t] (nth t 2)))


;
(extend-type Associative p/RDF
  (graph
    ([f] (set f))
    ([f g] (set g)))
  (triple
    ([f t] (merge f {:subject (p/subject t) :predicate (p/predicate t) :object (p/object t)}))
    ([f subj pred obj] (merge f { :subject subj :predicate pred :object obj }))) ;; TODO: Map to our own types?

  (iri [f iri] (assoc f :iri (p/iri-str iri)))

  (literal
    ([f lit] (assoc f :literal (str lit)))
    ([f lit type-or-lang]
      (merge f {:literal (str lit)}
        (if (p/iri? type-or-lang)
          {:datatype (p/iri-str type-or-lang)}
          {:language (name type-or-lang)}))))

  (blanknode
    ([f] (assoc f :blanknode (uuid)))
    ([f id] (assoc f :blanknode (name id))))
)
