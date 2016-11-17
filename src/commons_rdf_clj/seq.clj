(ns commons-rdf-clj.seq
  (:import
    (clojure.lang Seqable)
    (org.apache.commons.rdf.api Graph IRI Triple RDF RDFTerm Literal BlankNode)
    (java.util UUID)
    (org.apache.commons.rdf.simple SimpleRDF))
    (:require
      [commons-rdf-clj.ns :refer [rdf xsd]]
      [commons-rdf-clj.utils :refer [uuid]]
      [commons-rdf-clj.protocols :as p]
      )
)

;
(extend-type Seqable p/RDF
  (graph
    ([f] (set f))
    ([f g] (set g)))

  (iri [f iri] (assoc f :iri (p/iri-str iri)))

  (literal
    ([f lit] (assoc f :literal (str lit)))
    ([f lit type-or-lang]
      (assoc (p/literal f lit)
        (if (p/iri? type-or-lang)
          {:datatype (p/iri-str type-or-lang)}
          {:language (name type-or-lang)}))))

  (blanknode
    ([f] (assoc f :blanknode (uuid)))
    ([f id] (assoc f :blanknode (name id))))
)


(extend-type Seqable p/Term
  (iri? [term] (contains? term :iri))
  (literal? [term] (contains? term :literal))
  (blanknode? [term] (contains? term :blanknode))

  (ntriples-str [term] (cond (p/iri? term) (str "<" (:iri term) ">")
                             (p/blanknode? term) (str "_:" (:blanknode term))
                             (p/literal? term)
                                  ;; TODO: Escape properly
                                  ;; TODO: language and datatype
                                (str \" (:literal term)  \")))

  (iri-str [iri] (str (:iri iri)))
  (blanknode-id [bnode] (str (:blanknode bnode)))

  (literal-str [lit] (str (:literal lit)))
  (literal-lang [lit] (:language lit)) ;; TODO: Ensure nil or str
  (literal-type [lit] (p/iri {} (get lit :datatype
    (or ; fall-back when :datatype is not specified
      (and (:language lit) {:iri (rdf :langString)})
      {:iri :xsd/string}
      {:iri (xsd :string)}))))
)

(extend-type clojure.lang.Seqable
  p/Graph
    (add-triple [g tripl]
      (conj g tripl))
    (add-triple [g s p o]
        (conj g {:subject s :predicate p :object o}))
    (triple-count [g] (count g))
)

(extend-type clojure.lang.IPersistentMap
  p/Triple
    (subject [m] (:subject m))
    (predicate [m] (:predicate m))
    (object [m] (:object m)))
