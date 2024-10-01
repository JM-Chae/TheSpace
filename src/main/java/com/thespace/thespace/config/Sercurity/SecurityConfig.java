package com.thespace.thespace.config.Sercurity;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig
  {
    @Bean
    public PasswordEncoder passwordEncoder()
      {
        return new BCryptPasswordEncoder();
      }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
      {
        http
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers("/**").permitAll())
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .formLogin(formLogin -> formLogin.loginPage("/user/login"))
//            .exceptionHandling((exceptionHandling) -> exceptionHandling
//                .accessDeniedHandler())
//        .oauth2Login((oauth2Login) ->oauth2Login.loginPage("/user/login"));

        ;
      return http.build();
      }

//    @Bean
//    public AccessDeniedHandler accessDeniedHandler()
//      {
//        return new ();
//      }
  }
