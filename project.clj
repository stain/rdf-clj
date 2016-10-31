(defproject commons-rdf-clj "0.1.0-SNAPSHOT"
  :description "Clojure wrapper for Apache Commons RDF"
  :url "http://commonsrdf.incubator.apache.org/"
  :license {:name "Apache License, version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.apache.commons/commons-rdf-api "0.3.0-incubating"]
                 [org.apache.commons/commons-rdf-simple "0.3.0-incubating"]
;                [potemkin "0.3.13"]
                ]
  :repositories { "apache-snapshot" "http://repository.apache.org/snapshots"}
              )
