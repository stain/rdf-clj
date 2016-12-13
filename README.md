# rdf-clj

[![Build Status](https://travis-ci.org/stain/commons-rdf-clj.svg?branch=master)](https://travis-ci.org/stain/commons-rdf-clj)

This is an early attempt to create Clojure RDF library
with bindings for
[Apache Commons RDF](http://commonsrdf.incubator.apache.org/)
and native Clojure data structures.

Note that this project is currently **experimental** and may change
at any time (or not at all). Please feel free to contribute by
raising issues or pull requests!

Similar to Commons RDF, this library focus on creating
and inspecting objects corresponding to RDF
concepts like _triple_, _graph_ and _IRI_, _Literal_ and _Blank node_;
as well as and interoperability between the RDF implementations.

This library does not currently expose more specific
features of the underlying RDF frameworks (e.g. SPARQL queries in
Apache Jena or a remote repository connection in Eclipse RDF4J),
but the way _rdf-clj_ has been implemented with _Clojure protocols_
should mean that it is relatively easy to "mix and match", if needed.

## RDF

The main functionality of _rdf-clj_
is exposed from the top-level [rdf](src/rdf.clj) namespace, so many
users of this library may only need to require `rdf`:

```clojure
(ns example1
  (:require [rdf :refer :all]))
```

The below shows how to use `graph`, `triple` and `add-triple`:

```clojure

(def g (graph))
example1=> (type g)
clojure.lang.PersistentHashSet

(def t (triple (blanknode) (iri "http://example.com/greeting") (literal "Hello world", "en")))

example1=> t
{:subject {:blanknode #uuid "5cb6182d-2791-47f6-9937-89c088490c99"}, :predicate {:iri "http://example.com/greeting"}, :object {:literal "Hello world", :language "en"}}

(def g (add-triple g t))
g
#{{:subject {:blanknode #uuid "5cb6182d-2791-47f6-9937-89c088490c99"}, :predicate {:iri "http://example.com/greeting"}, :object {:literal "Hello world", :language "en"}}}

example1=> (triple-count g)
1

example1=> (subject (first g))
{:blanknode #uuid "5cb6182d-2791-47f6-9937-89c088490c99"}

example1=> (blanknode? (subject (first g)))
true

example1=> (literal-lang (object (first g)))
"en"
```

Analogue to `conj`, graph modification methods like
`add-triple` return the mutated graph, as the graph
(by default) is backed by an immutable set. Note that Commons RDF
implementations and a `transient` Clojure colleciton
will do direct modifications.



## Protocols

The `rdf` functionality is implemented as a series of
([rdf.protocols](src/rdf/protocols.clj))
with implementations for Commons RDF ([rdf.commonsrdf]]src/rdf/commonsrdf.clj)
and regular Clojure seqs ([rdf.seq](src/rdf/seq.clj)).


The starting point is the `rdf.protocols/RDF` protocol, which
exposes factory methods like `graph`, `iri` and `literal`. The first
argument of these methods is the implementation to use, e.g.
an instance of `org.apache.commons.rdf.api.RDF` or `{}` to use
Clojure dictionaries.




## Usage

Install using [Leiningen](http://leiningen.org/)

    lein install

To run the tests:

    lein test


## License

Copyright © 2015-2016 Stian Soiland-Reyes
Copyright © 2015-2016 University of Manchester

Distributed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0). See the file
[LICENSE](LICENSE) for details.
