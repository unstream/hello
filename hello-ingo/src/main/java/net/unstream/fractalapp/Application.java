package net.unstream.fractalapp;

import java.util.HashSet;
import java.util.Set;

import net.unstream.fractal.db.FractalRepository;
import net.unstream.fractal.db.converters.JodaDateTimeToLongConverter;
import net.unstream.fractal.db.converters.LongToJodaDateTimeConverter;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = FractalRepository.class)
@ComponentScan({"net.unstream.fractalapp", "net.unstream", "net.unstream.fractal.db"})
@EnableCaching
@EnableAsync
public class Application {



	@Configuration
	//@EnableSpringDataWebSupport
	static class ConversionConfiguration {
		
//		@Bean
//		public SpringDataWebConfiguration getSpringDataWebConfiguration() {
//			ApplicationContext df;
//			SpringDataWebConfiguration config = new SpringDataWebConfiguration(df) {
//				@Override
//				public PageableHandlerMethodArgumentResolver pageableResolver() {
//					PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver = new PageableHandlerMethodArgumentResolver(sortResolver());
//					pageableHandlerMethodArgumentResolver.setFallbackPageable(PageRequest.of(0,  9));
//					return pageableHandlerMethodArgumentResolver;
//				}
//			};
//			return config;
//		}

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
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}