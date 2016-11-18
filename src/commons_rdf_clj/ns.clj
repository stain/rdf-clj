(ns rdf.ns
  (:require
    [commons-rdf-clj.protocols :as p]))

(defn- iri [base local]
  {:iri (str base local)})

(defn rdf-namespace [base & names]
  (apply hash-map (mapcat #(list %1 (uri base %1)))
          (map name names)))


(defn- ns-rdf- [namespace base & names]
  (let [n (create-ns (symbol namespace))]
    (doall (map #(intern n (symbol %1) (iri base %1))
    (map name names)))))

;
(defmacro ns-rdf [n base & names]
  `(ns-rdf- (symbol '~n) ~base ~@names)
  `(def ~n (rdf-namespace ~base ~@names))
)


(ns-rdf rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  :langString :HTML :PlainLiteral :XMLLiteral))

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
))
