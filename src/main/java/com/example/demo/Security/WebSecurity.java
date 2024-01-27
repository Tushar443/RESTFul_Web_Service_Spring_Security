package com.example.demo.Security;

import com.example.demo.ws.Service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurity {

	private final UserService userDetailsService2;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userDetailsService2 = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception{
		System.out.println("----------WebSecurityDemo == configure() Method Call----------");
		// Configure AuthenticationManagerBuilder
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(userDetailsService2).passwordEncoder(bCryptPasswordEncoder);

		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

		//Customize Login URL path
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
		authenticationFilter.setFilterProcessesUrl("/users/login");
		http.cors(cors-> cors.disable());

	  return http.csrf(csrf -> csrf.disable())
			  .authorizeHttpRequests(
					configure ->
						configure
								.requestMatchers(new AntPathRequestMatcher(SecurityConstants.SIGN_UP_URL,HttpMethod.POST.name()))
								.permitAll().anyRequest().authenticated()
				)
			  .authenticationManager(authenticationManager)
			  .addFilter(authenticationFilter)
			  .addFilter(new AuthorizationFilter(authenticationManager))
			  .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			  .build();
	}
}
