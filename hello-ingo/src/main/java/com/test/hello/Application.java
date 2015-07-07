package com.test.hello;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan({"com.test.hello", "net.unstream"})
@EnableAsync
public class Application {

	
	@Configuration
	@EnableNeo4jRepositories(basePackages = "net.unstream.mandelbrot")
	static class ApplicationConfig extends Neo4jConfiguration {
		
		public ApplicationConfig() {
			setBasePackage("net.unstream.mandelbrot");
		}
		
		@Bean
		GraphDatabaseService graphDatabaseService() {
//			return new SpringRestGraphDatabase("http://hellofractal.sb04.stations.graphenedb.com:24789/db/data/", 
//					"hellofractal", "CnPxncpPKb9jnoXjf4My");
			String dbDataDirectory = System.getProperty("neo4jData");
			System.out.println("Storing neo4j data in: " + dbDataDirectory);
			return new GraphDatabaseFactory().newEmbeddedDatabase(dbDataDirectory);
		}
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}