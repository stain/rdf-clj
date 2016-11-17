(ns commons-rdf-clj.utils
  (:import
    [java.util UUID]
  )
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
