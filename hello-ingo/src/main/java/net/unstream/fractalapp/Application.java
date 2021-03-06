package net.unstream.fractalapp;

import java.util.HashSet;
import java.util.Set;

import net.unstream.fractal.db.converters.JodaDateTimeToLongConverter;
import net.unstream.fractal.db.converters.LongToJodaDateTimeConverter;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.lifecycle.AuditingEventListener;
import org.springframework.data.neo4j.support.mapping.Neo4jMappingContext;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan({"net.unstream.fractalapp", "net.unstream"})
@Import(RepositoryRestMvcConfiguration.class)
@EnableAutoConfiguration
@EnableAsync
public class Application {

	@Configuration
	@EnableNeo4jRepositories(basePackages = "net.unstream")
	@EnableTransactionManagement

	static class ApplicationConfig extends Neo4jConfiguration {
		public ApplicationConfig() {
			setBasePackage("net.unstream");
		}
		
		@Bean
		GraphDatabaseService graphDatabaseService() {
//			return new SpringRestGraphDatabase("http://hellofractal.sb04.stations.graphenedb.com:24789/db/data/", 
//					"hellofractal", "CnPxncpPKb9jnoXjf4My");
			String dbDataDirectory = System.getProperty("neo4jData");
			System.out.println("Storing neo4j data in: " + dbDataDirectory);
			GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase(dbDataDirectory);
			
			return database;
		}
		

		@Bean
		public AuditingEventListener auditingEventListener() throws Exception {
			AuditingEventListener listener = new AuditingEventListener(new ObjectFactory<IsNewAwareAuditingHandler>() {
				@Override
				public IsNewAwareAuditingHandler getObject() throws BeansException {
					return new IsNewAwareAuditingHandler(new Neo4jMappingContext());
				}
			});
			return listener;
		}
	}

	@Configuration
	//@EnableSpringDataWebSupport
	static class ConversionConfiguration {
		
		@Bean 
		public SpringDataWebConfiguration getSpringDataWebConfiguration() {
			SpringDataWebConfiguration config = new SpringDataWebConfiguration() {
				@Override
				public PageableHandlerMethodArgumentResolver pageableResolver() {
					PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver = new PageableHandlerMethodArgumentResolver(sortResolver());
					pageableHandlerMethodArgumentResolver.setFallbackPageable(new PageRequest(0,  9));
					return pageableHandlerMethodArgumentResolver;
				}
			};
			return config;			
		}

		@Bean(name="conversionService")
	    public ConversionService getConversionService() {
	        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
	        bean.setConverters(getConverters());
	        bean.afterPropertiesSet();
	        ConversionService object = bean.getObject();
	        return object;
	    }

	    private Set<Converter> getConverters() {
			Set<Converter> converters = new HashSet<Converter>();
			converters.add(new JodaDateTimeToLongConverter());
			converters.add(new LongToJodaDateTimeConverter());
	        return converters;
	    }
	}
	
	@Configuration
	@EnableCaching
	static class CacheConfiguration {
		@Bean
		public CacheManager cacheManager() {
			return new EhCacheCacheManager(ehCacheCacheManager().getObject());
		}

		@Bean
		public EhCacheManagerFactoryBean ehCacheCacheManager() {
			EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
			cmfb.setConfigLocation(new ClassPathResource("ehcache.xml"));
			cmfb.setShared(true);
			return cmfb;
		}
	}
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}