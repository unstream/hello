FROM adoptopenjdk/openjdk11:alpine-jre
MAINTAINER Ingo Weichsel
RUN mkdir /data
ADD hello-ingo/target/hello-ingo-1.0-SNAPSHOT.jar /data/
EXPOSE 8080
ENTRYPOINT [ "java" ]
CMD [ "-Xms400m", "-Xmx400m",  "-jar", "/data/hello-ingo-1.0-SNAPSHOT.jar" ]
