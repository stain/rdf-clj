(ns commons-rdf-clj.n
  (:require
    [commons-rdf-clj.protocols :as p]))


(defn- ns-rdf- [namespace base & names]
  (let [n (create-ns (symbol namespace))]
    (doall (map #(intern n (symbol %1) {:iri (str base %1)})
    (map name names)))))

;
(defmacro ns-rdf [namespace base & names]
  `(ns-rdf- ~(symbol namespace) ~base
    ~@names))


;(defn rdf-namespace [base & names]
;  (apply hash-map (mapcat #(list %1 {:uri (str base (name %1))})
;          names)))

(def rdf (rdf-namespace "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  :langString :HTML :PlainLiteral :XMLLiteral))

(def xsd (rdf-namespace "http://www.w3.org/2001/XMLSchema#"
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
