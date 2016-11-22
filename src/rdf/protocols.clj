(ns rdf.protocols)

(defprotocol RDF
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

(defprotocol Term
  (iri? [term])
  (literal? [term])
  (blanknode? [term])

  (ntriples-str [term])

  (iri-str [term])

  (literal-str [term])
  (literal-lang [term])
  (literal-type [term])

  (blanknode-id [term])
)


(defprotocol Triple
  (subject [t])
  (predicate [t])
  (object [t])
)


(defprotocol Graph
  (add-triple
    [g t]
    [g subj pred obj])
  (triple-count [g])
)


; nil: not iri, literal or blanknode
(extend-type nil Term
  (iri? [obj] false)
  (literal? [obj] false)
  (blanknode? [obj] false)
  ; deliberately let the rest of the functions fail
)


; Default: not iri, literal or blanknode
(extend-type java.lang.Object Term
  (iri? [obj] false)
  (literal? [obj] false)
  (blanknode? [obj] false)
  ; deliberately let the rest of the functions fail
)
