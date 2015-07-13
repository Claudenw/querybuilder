querybuilder
============

*NOTICE*
========

QueryBuilder is now part of the <a href="https://jena.apache.org">Apache Jena</a> project and can be found within the jena-extracs package:

* <a href="https://github.com/apache/jena/tree/master/jena-extras/jena-querybuilder">git</a> 
* <a href="https://repository.apache.org/content/repositories/releases/org/apache/jena/jena-querybuilder">releases</a>
* <a href="https://repository.apache.org/content/repositories/snapshots/org/apache/jena/jena-querybuilder">snapshots</a>
* <a href="https://jena.apache.org/documentation/extras/querybuilder/index.html">documentation</a>

Maven Info
     <groupId>org.apache.jena</groupId>
     <artifactId>jena-querybuilder</artifactId>


All development has shifted to the Apache Jena project.  Please report issues and submit improvements there.

-----------------------------------

OLD DOCUMENTATION BELOW
=======================

Query Builder for Jena.  Implementations of Ask, Construct and Select builders that allow developers to create queries without resorting to StringBuilders or similar solutions.

Each of the builders has a series of methods to define the query.  Each method returns the builder for easy chaing.  The  example:

```
SelectBuilder sb = new SelectBuilder()
  .addVar( "*" )
  .addWhere( "?s", "?o", "?p" );
  
Query q = sb.build();

```

produces `SELECT * WHERE { ?s ?o ?p . }`

Template Usage
==============

In addition to making it easier to build valid queries the QueryBuilder has a clone method.  Using this a developer can create as "Template" query and add to it as necessary.

for example using the above query as the "template" the this code:

```
SelectBuilder sb2 = sb.clone();
sb2.addPrefix( "foaf", "http://xmlns.com/foaf/0.1/" ).addWhere( ?s, RDF.type, foaf:Person) 
```

produces `PREFIX foaf: http://xmlns.com/foaf/0.1/ SELECT * WHERE { ?s ?o ?p . ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> foaf:person . }`

Prepared Statement Usage
========================

The query builders have the ability to replace variables with other values.  This can be 

```
SelectBuilder sb = new SelectBuilder()
  .addVar( "*" )
  .addWhere( "?s", "?o", "?p" );
  
sb.setVar( Var.alloc( "?p" ), NodeFactory.createURI( "http://xmlns.com/foaf/0.1/Person" )
Query q = sb.build();

```

produces `SELECT * WHERE { ?s ?o <http://xmlns.com/foaf/0.1/Person> . }`

