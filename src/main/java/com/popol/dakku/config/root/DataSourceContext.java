package com.popol.dakku.config.root;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceContext {
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		configurer.setLocation(new ClassPathResource("config.properties"));
		return configurer;
	}
	
	@Configuration
	@Profile("local")
	public static class DataSourceLocalConfig {
		
		@Value("${db.local.url}")
		private String url;
		
		@Value("${db.local.username}")
		private String username;
		
		@Value("${db.local.password}")
		private String password;
		
		@Value("${db.local.driverClassName}")
		private String driverClassName;
		
		@Bean
		public DataSource dataSource() {
			DriverManagerDataSource dataSource = new DriverManagerDataSource(url, username, password);
			dataSource.setDriverClassName(driverClassName);
			return dataSource;
		}
		
	}
	
	@Configuration
	@Profile("prod")
	public static class DataSourceProdConfig {
		
	}
}
