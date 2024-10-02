package com.thespace.thespace.config.Sercurity;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig implements UserDetailsService
  {


    @Bean
    public PasswordEncoder passwordEncoder()
      {
        return new BCryptPasswordEncoder();
      }

    @Bean
    public WebMvcConfigurer corsConfigurer()
      {
        return new WebMvcConfigurer()
          {
            public void addCorsMappings(final CorsRegistry registry)
              {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:5173")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
              }
          };
      }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
      {
        http
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers("/**").permitAll())
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(formLogin -> formLogin.loginPage("/user/login").usernameParameter("id").passwordParameter("password").defaultSuccessUrl("/").failureUrl("/login?error=true"))
            .logout(logout -> logout.logoutUrl("/user/logout").logoutSuccessUrl("/").invalidateHttpSession(true))
//            .exceptionHandling((exceptionHandling) -> exceptionHandling
//                .accessDeniedHandler())
//        .oauth2Login((oauth2Login) ->oauth2Login.loginPage("/user/login"));

        ;
      return http.build();
      }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
      return authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
      {
        return null;
      }

//    @Bean
//    public AccessDeniedHandler accessDeniedHandler()
//      {
//        return new ();
//      }
  }
