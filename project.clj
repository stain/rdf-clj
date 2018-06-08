(defproject org.clojars.phreed/rdf-clj "0.2.0-SNAPSHOT"
  :description "RDF for Clojure, integrates with Commons RDF, Jena, RDF4J"
  :url "https://github.com/phreed/rdf-clj"
  :license {:name "Apache License, version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.commons/commons-rdf-api "0.5.0"]
                 [org.apache.commons/commons-rdf-simple "0.5.0"]]
;                [potemkin "0.3.13"]

  :profiles {
             :dev {
                   :dependencies [[org.apache.commons/commons-rdf-jena "0.5.0"]
                                  [org.apache.commons/commons-rdf-rdf4j "0.5.0"]
                                  [org.apache.commons/commons-rdf-jsonld-java "0.5.0"]]}})

;  :repositories { "apache-snapshot" "http://repository.apache.org/snapshots"}
