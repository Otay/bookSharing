package com.udemy.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.udemy.demo.jwt.JwtFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	@Autowired
	RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Bean
	public SecurityFilterChain web(HttpSecurity http)throws Exception {
		http
		.csrf().disable()
		.exceptionHandling()
		.authenticationEntryPoint(restAuthenticationEntryPoint)
		.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
			.authorizeRequests()
								 .antMatchers("/").permitAll()
					             .antMatchers("/static/**/").permitAll()
					             .antMatchers("/users").permitAll()
								.antMatchers("/authenticate").permitAll()
								.antMatchers("/isconnected").permitAll()
								.antMatchers("/v3/api-docs/**").permitAll()
					            .antMatchers("/swagger-resources/**").permitAll()
					            .antMatchers("/swagger-ui/**").permitAll()
					            .antMatchers("/swagger-ui.html").permitAll()
					            .antMatchers("/webjars/**").permitAll()
			.anyRequest().authenticated();
		
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passWordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
