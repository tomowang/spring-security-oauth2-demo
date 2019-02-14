package com.example.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
@RestController
public class ClientApplication {
	@Autowired
	private OAuth2RestTemplate resourceServerProxy;

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@RequestMapping(value = "/api/demo", method = RequestMethod.GET)
	public Map<String, Boolean> demo() throws URISyntaxException {
		String message = "demo";
		Boolean result = resourceServerProxy.postForObject("http://127.0.0.1:9090", message, Boolean.class);
		return Collections.singletonMap("message", result);
	}

	@Configuration
	@EnableOAuth2Sso
	protected static class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

		/**
		 * @param http
		 * @throws Exception
		 */
		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
				.logout()
					.deleteCookies("JSESSIONID", "XSRF-TOKEN")
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.invalidateHttpSession(true)
					.and()
				.antMatcher("/**")
					.authorizeRequests()
					.antMatchers("/**").hasRole("SUPER_ADMIN")
					.anyRequest().authenticated()
					.and()
				.csrf().disable();
			// @formatter:on
		}

		@Bean
		@ConfigurationProperties("security.oauth2.client")
		public ClientCredentialsResourceDetails getClientCredentialsResourceDetails() {
			return new ClientCredentialsResourceDetails();
		}

		@Bean
		public OAuth2RestTemplate restTemplate() {
			AccessTokenRequest atr = new DefaultAccessTokenRequest();
			return new OAuth2RestTemplate(getClientCredentialsResourceDetails(),
					new DefaultOAuth2ClientContext(atr));
		}
	}
}
