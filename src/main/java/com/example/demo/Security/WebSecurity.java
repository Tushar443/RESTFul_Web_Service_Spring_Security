package com.example.demo.Security;

import com.example.demo.Repository.UserRepo;
import com.example.demo.ws.Service.UserServiceIfc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true,prePostEnabled=true)
public class WebSecurity {

    private final UserServiceIfc userDetailsService2;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepo userRepo;

    public WebSecurity(UserServiceIfc userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder, UserRepo userRepo) {
        this.userDetailsService2 = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepo = userRepo;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        System.out.println("----------WebSecurityDemo == configure() Method Call----------");
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService2).passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        //Customize Login URL path
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
        authenticationFilter.setFilterProcessesUrl("/users/login");
        http.cors(cors -> cors.disable());

        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        configure ->
                                configure
                                        .requestMatchers(new AntPathRequestMatcher(SecurityConstants.SIGN_UP_URL, HttpMethod.POST.name()))
                                        .permitAll()
                                        .requestMatchers(new AntPathRequestMatcher(SecurityConstants.EMAIL_VERIFICATION_URL, HttpMethod.GET.name()))
                                        .permitAll()
                                        .requestMatchers(new AntPathRequestMatcher(SecurityConstants.PASSWORD_RESET_REQUEST_URL, HttpMethod.POST.name()))
                                        .permitAll()
                                        .requestMatchers(new AntPathRequestMatcher(SecurityConstants.PASSWORD_RESET_URL, HttpMethod.POST.name()))
                                        .permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/v2/api-docs"))//"/configuration/**","/swagger/**","/webjars/**"))
                                        .permitAll()
//								.requestMatchers(new AntPathRequestMatcher(SecurityConstants.ADMIN_ACCESS_URL,HttpMethod.DELETE.name()))
//								.hasRole("ADMIN")
//								.hasAuthority("DELETE_AUTHORITY")
//								.hasAnyAuthority("DELETE_AUTHORITY","WRITE_AUTHORITY")
//								.hasRole("ADMIN","USER")

                                        .anyRequest().authenticated()
                )
                .authenticationManager(authenticationManager)
                .addFilter(authenticationFilter)
                .addFilter(new AuthorizationFilter(authenticationManager, userRepo))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
