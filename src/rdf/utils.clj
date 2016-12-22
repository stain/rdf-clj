(ns rdf.utils
  (:import
    [java.util UUID])
  (:require [clojure.string :as string])
)

(defmacro if-instance [type value & body]
  `(if (instance? ~type ~value) ~value
    ~@body))


(defn uuid
  (^UUID [] (java.util.UUID/randomUUID))
  (^UUID [id]
    (if-instance UUID id
      ; otherwise, parse as string
      (java.util.UUID/fromString (str id)))))

(defn escape-literal [s]
  (string/replace s "\"" "\\\""))
