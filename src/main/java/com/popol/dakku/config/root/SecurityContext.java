package com.popol.dakku.config.root;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.popol.dakku.modules.commons.auth.oauth2.CustomOAuth2Provider;
import com.popol.dakku.modules.commons.auth.service.CustomOAuth2UserService;

@Configuration
@PropertySource("classpath:application-oauth.properties")
@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter {

	private static String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";
	private static List<String> clients = Arrays.asList("google", "naver");
	
	@Autowired
	private Environment env;
	
	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		List<ClientRegistration> registrations = clients.stream()
				.map(c -> getRegistration(c))
				.filter(registration -> registration != null)
				.collect(Collectors.toList());
		return new InMemoryClientRegistrationRepository(registrations);
	}
	
	private ClientRegistration getRegistration(String client) {
		String clientId = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-id");
		
		if(clientId == null)
			return null;
		
		String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-secret");
		
		//CommonOAuth2Provider
		if(client.equals("google")) {
			return CustomOAuth2Provider.GOOGLE.getBuilder(client)
						.clientId(clientId).clientSecret(clientSecret).build();
		} else if(client.equals("naver")) {
			return CustomOAuth2Provider.NAVER.getBuilder(client)
						.clientId(clientId).clientSecret(clientSecret).build();
		}
		return null;
	}
	

	@Bean
	public OAuth2AuthorizedClientService authorizedClientService() {
		return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
	}
	
	@Bean
	public OAuth2UserService customOAuth2UserService() {
		return new CustomOAuth2UserService();
	}
	
	/**
	 * 다음의 경로를 Spring Security 적용 안하기
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.ignoring().antMatchers("/resources/**", "/upload/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf()
				.csrfTokenRepository(csrfTokenRepository())
			.and()
				.authorizeRequests()
					.antMatchers("/manage/item/**").hasAnyAuthority("SUPER", "ITEM_CREATOR")
					.antMatchers("/auth/**").authenticated()
					.antMatchers("/login").permitAll()
			.and()
				.formLogin()
					.loginPage("/login")
			.and()
				.logout()
					.logoutUrl("/logout")
					.logoutSuccessUrl("/")
			.and()
				.oauth2Login()
					.loginPage("/login")
					.clientRegistrationRepository(clientRegistrationRepository())
					.authorizedClientService(authorizedClientService())
					.userInfoEndpoint()
						.userService(customOAuth2UserService());
	}
	
	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();
		csrfTokenRepository.setSessionAttributeName("_csrf");
		return csrfTokenRepository;
	}
}
