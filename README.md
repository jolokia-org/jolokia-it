# Jolokia Integration Tests and API Documentation

This project holds integration tests for Jolokia against a multitude
of Java platforms. These platform or application servers are selected
with Maven profiles and use
[docker-maven-plugin](https://github.com/rhuss/docker-maven-plugin)
for setting up the stage.

Jolokia uses
[Spring REST Docs](http://projects.spring.io/spring-restdocs/) for
creating the API documentation by instrumenting
[REST assured](https://github.com/jayway/rest-assured) based
Test. This ensures that the documentation always matches the agents. 

## Integration Tests

The integration tests are prepared for running for different server
configuration which must be selected by a profile. The supported
profiles are

* `tomcat` Jolokia WAR agent with Tomcat. Supported versions are 8.0
  (default), 7.0, 6.0 and 5.5 
* `jetty` Jolokia WAR agent with Jetty. Supported versions are 9.0
  (default), 8.0 and 7.0
  
The server version can be selected with the property `server.version`.

E.g. for running the integration tests with Tomcat 7.0 use 

```bash
mvn -Ptomcat -Dserver.version=7.0 clean install
```

Internally the integrtion tests use the
[tiles-maven-plugin](https://github.com/repaint-io/maven-tiles) for
composing the docker-maven-plugin configuration for various setups. To
create and install the tiles, use

```bash
mvn -Ptile clean install
```

This must be done once before running the integration tests or
creating the documentations. The tiles themselves can be found below
the directory `tile`. 

## API Documentation

The Jolokia API documentation is based on
[AsciiDoc](http://www.methods.co.nz/asciidoc/) and the
[asciidoctor-maven-plugin](http://asciidoctor.org/docs/asciidoctor-maven-plugin/),
together with
[Spring REST Doc](http://projects.spring.io/spring-restdocs/) for
extracting the examples from live integration tests. The documentation
can be found in the subdirectory `doc` and can be created with 

```bash
mvn -Pdoc clean install
```

## Versioning

The versioning follows the Jolokia versions. So, for each new version
of Jolokia an equal tag will be created. There are two main branches:
`master` is for the current main line of Jolokia development (2.x in
that case) and `1.x` is for Jolokia 1 releases.
