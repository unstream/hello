FROM errordeveloper/oracle-jre
MAINTAINER Ingo Weichsel
RUN mkdir /data
ADD hello-ingo-1.0-SNAPSHOT.war /data/
ADD neo4j.db /data/neo4j.db/
EXPOSE 8080
ENTRYPOINT [ "java" ]
CMD [ "-Dneo4jData=/data/neo4j.db",  "-jar", "/data/hello-ingo-1.0-SNAPSHOT.war" ]
