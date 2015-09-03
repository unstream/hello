# Hello Mandelbrot
This project implements a web application enabling users to create and share images of the Mandelbrot and the Julia set. 

This project was not written because the Mandelbrot Set is cool (although it is!), but rather to toy around with some technologies and frameworks.

You can download a docker image of this project at https://hub.docker.com/r/unstream/mandelbrot/.

A running instance where you can browse some fractals is deployed at http://hello-ingo.elasticbeanstalk.com/. 
But beware, it is a T1 Micro instance using an embedded database, which is not much faster than my Raspberry PI.

### Building the project
mvn install builds all subprojects and creates a hello-ingo-1.0-SNAPSHOT.war in the hello-ingo module.

### Run the project
Either execute the war directly calling java -Dneo4jData=&lt;db-folder&gt; -jar hello-ingo-1.0-SNAPSHOT.war
or deploy the war to a Servlet Container, I tested with Wildfly and Tomcat.

### Modules
The module hello-ingo contains a Spring Boot Application serving a Spring MVC/Thymeleaf web application.

The module mandelbrot implements the persistence services for users and fractals using Spring Data and neo4j. 
It also implements the Mandelbrot image creation services.


