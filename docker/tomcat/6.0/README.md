## Apache Tomcat 6.x

A simple docker build for installing a vanilla Tomcat 6 below
*/opt/tomcat*. It comes out of the box and is intended for use for
integration testing.

Features:

* Tomcat Version: **6.0.39**
* Java Version: **Oracle 1.7.0_51-b13** (base image: *dockerfile/java*)
* Port: **8080**
* User **admin** (Password: **admin**) has been added to access the admin
  applications */host-manager* and */manager*)
* Documentation and examples has been removed
* Command: `/opt/tomcat/bin/catalina.sh run`
