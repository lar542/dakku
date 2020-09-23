package com.popol.dakku.config.leaf;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import com.popol.dakku.modules.commons.consts.VarConsts;
import com.popol.dakku.modules.commons.interceptor.MenuLoadInterceptor;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.popol.dakku.modules")
public class ServletContext implements WebMvcConfigurer {

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Bean
	public SpringResourceTemplateResolver thymeleafTemplateResolver() {
		SpringResourceTemplateResolver resourceTemplateResolver = new SpringResourceTemplateResolver();
		resourceTemplateResolver.setPrefix("/WEB-INF/views/");
		resourceTemplateResolver.setSuffix(".html");
		resourceTemplateResolver.setCharacterEncoding("UTF-8");
		resourceTemplateResolver.setCacheable(false);
		return resourceTemplateResolver;
	}
	
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(thymeleafTemplateResolver());
		templateEngine.setAdditionalDialects(additionalDialects());
		return templateEngine;
	}
	
	private final Set<IDialect> additionalDialects() {
		Set<IDialect> dialects = new HashSet<IDialect>();
		dialects.add(new LayoutDialect());
		dialects.add(new SpringSecurityDialect());
		return dialects;
	}
	
	@Bean
	public ThymeleafViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		thymeleafViewResolver.setTemplateEngine(templateEngine());
		thymeleafViewResolver.setCharacterEncoding("UTF-8");
		thymeleafViewResolver.setOrder(1);
		return thymeleafViewResolver;
	}
	
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(300000000);
		return multipartResolver;
	}
	
	@Bean
	public MenuLoadInterceptor menuLoadInterceptor() {
		return new MenuLoadInterceptor();
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry
			.addInterceptor(menuLoadInterceptor())
			.addPathPatterns("/", "/post/**", "/auth/post/**", "/auth/inven/**");
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/resources/**")
			.addResourceLocations("/resources/");
	}
	
	@Configuration
	@EnableWebMvc
	@Profile("local")
	public static class ResourcesLocalConfig implements WebMvcConfigurer {
		
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry
				.addResourceHandler("/upload/summernoteImg/**")
				.addResourceLocations("file:///E:/dakkuFile/local/");
		}
		
		@Bean
		public VarConsts varConsts() {
			return new VarConsts("/upload/summernoteImg/", "E:\\dakkuFile\\local\\");
		}
	}
	
	@Configuration
	@EnableWebMvc
	@Profile("prod")
	public static class ResourcesProdConfig implements WebMvcConfigurer {
		
	}
}
