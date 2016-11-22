(ns rdf.ns
  (:require
    [rdf.protocols :as p]))

; A standalone version of seq's (iri)
; to avoid circular imports
(defn- iri [base local]
  {:iri (str base local)})

(defn rdf-namespace [base & names]
  (if (empty? names) #(iri base (name %1)) ; maps anything
    ; else, map only listed names.
    (apply hash-map (mapcat #(list (keyword %1) (iri base %1))
          (map name names)))))

(defmacro ns-rdf [n base & names]
  `(def ~n (rdf-namespace ~base ~@names)))


(ns-rdf rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  ; indicating class
  :type
  ; type of all properties
  :Property
  ; special Literal types
  :HTML :langString :XMLLiteral
  ; DEPRECATED in RDF 1.1
  :PlainLiteral
  ; reification
  :Statement :subject :predicate :object
  ; collections
  :Bag :Seq :Alt :List
  :value :nil :first :rest
  )

(ns-rdf xsd "http://www.w3.org/2001/XMLSchema#"
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
)

(ns-rdf rdfs "http://www.w3.org/2000/01/rdf-schema#"
  :label :comment
  :seeAlso :isDefinedBy
  :Resource :Class
  :subClassOf :subPropertyOf
  :domain :range
  :Literal :Container :ContainerMembershipProperty
  :member :Datatype
)

; Some common schemas, prefixes from http://prefix.cc/
; These are not restricted and accept any keyword
(ns-rdf dc "http://purl.org/dc/elements/1.1/")
(ns-rdf dct "http://purl.org/dc/terms/")
(ns-rdf schema "http://schema.org/")
(ns-rdf foaf "http://xmlns.com/foaf/0.1/")
(ns-rdf owl "http://www.w3.org/2002/07/owl#")
