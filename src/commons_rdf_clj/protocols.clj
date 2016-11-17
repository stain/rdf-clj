(ns commons-rdf-clj.protocols)

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
