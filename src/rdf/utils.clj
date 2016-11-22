(ns rdf.utils
  (:import
    [java.util UUID])
  (:require [clojure.string :as string])
)

(defn uuid
  ([] (java.util.UUID/randomUUID))
  ([id]
    (if (instance? java.util.UUID id) id ; as-is
      ; otherwise, parse as string
      (java.util.UUID/fromString (str id)))))

;

(defmacro if-instance [type value & body]
  `(if (instance? ~type ~value) ~value
    ~@body))

(defn escape-literal [s]
  (string/replace s "\"" "\\\""))
