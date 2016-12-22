(ns rdf.protocols)

(defprotocol RDF
  (graph
    [f]
    [f g])
  (iri [f iri])
  (literal
    [f lit]
    [f lit type-or-lang]
    [f lit type lang])
  (blanknode
    [f]
    [f name])
  (triple
    [f t]
    [f subj pred obj])
)


(defprotocol Term
  (iri? ^Boolean [term])
  (literal?  ^Boolean [term])
  (blanknode? ^Boolean  [term])

  (ntriples-str ^String [term])

  (iri-str ^String [term])

  (literal-str ^String [term])
  (literal-lang ^String [term])
  (literal-type [term])

  (blanknode-ref [term])
)

(defn term
  "Coerce t to be an RDF term (iri, literal or blanknode)
  from RDF implementation f. "
  [f t]
  (cond
    (iri? t) (iri f t)
    (blanknode? t) (blanknode f t)
    ; Below should do (str t) if t is not already
    ; a literal
    :else (literal f t)))

(defprotocol Triple
  (subject [t])
  (predicate [t])
  (object [t])
)


(defprotocol Graph
  (add-triple
    [g t]
    [g subj pred obj])
  (remove-triple
    [g t]
    [g subj pred obj])
  (contains-triple?
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
