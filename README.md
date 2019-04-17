# Hello Mandelbrot
This project implements a web application enabling users to create and share images of the Mandelbrot and the Julia set. 

This project was not written because the Mandelbrot Set is cool (although it is!), but rather to toy around with some technologies and frameworks.

You can download a docker image of this project at https://hub.docker.com/r/unstream/mandelbrot/.

### Building the project
mvn install builds all subprojects and creates a hello-ingo-1.0-SNAPSHOT.war in the hello-ingo module.

### Docker
Copy db from running application:
```
docker cp -a 3a0c69ebc057:/data/neo4j.db .
```
Start application using a local folder for the database:
```
docker run -v ~/develop/mandelbrot/neo4j.db:/data/neo4j.db -it -p8055:8080 unstream/mandelbrot
```

### Run the project
Either execute the war directly calling java -Dneo4jData=&lt;db-folder&gt; -jar hello-ingo-1.0-SNAPSHOT.war
or deploy the war to a Servlet Container, I tested with Wildfly and Tomcat. You need to specify where the embedded neo4j database stores its data. Use a external neo4j instance by changing /hello/hello-ingo/src/main/java/net/unstream/fractalapp/Application.java.

### Modules
#### hello-ingo
The module hello-ingo contains a Spring Boot Application serving a Spring MVC/Thymeleaf web application.
#### mandelbrot
The module mandelbrot implements the persistence services for users and fractals using Spring Data and neo4j. 
It also implements the Mandelbrot image creation services.
#### admin
This module implements an spring-boot-admin-server to monitor the fractal app. To do so you need to enable the management in /hello/hello-ingo/src/main/resources/application.properties.


