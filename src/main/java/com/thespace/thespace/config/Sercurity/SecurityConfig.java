package com.thespace.thespace.config.Sercurity;

import com.thespace.thespace.config.Sercurity.handler.AuthSuccessHandler;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig implements UserDetailsService
  {

    private final AuthSuccessHandler authSuccessHandler;
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder()
      {
        return new BCryptPasswordEncoder();
      }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173")); // 허용할 출처
      configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // 허용할 메서드
      configuration.setAllowCredentials(true); // 인증 정보 전송 허용
      configuration.setAllowedHeaders(Collections.singletonList("*")); // 허용할 헤더

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration); // 모든 경로에 CORS 설정 적용
      return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
      {
        http
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers("/**").permitAll())
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(formLogin -> formLogin
                .loginPage("/user/login")
                .usernameParameter("id")
                .passwordParameter("password")
                .successHandler(authSuccessHandler))
            .rememberMe(rememberMe -> rememberMe
                .key("13TGT3gr@%g21$")
                .rememberMeParameter("remember")
                .tokenValiditySeconds(60*60*24*7)
                .authenticationSuccessHandler(authSuccessHandler))
            .logout(logout -> logout.logoutUrl("/user/logout").logoutSuccessUrl("/").invalidateHttpSession(true));
//        .oauth2Login((oauth2Login) ->oauth2Login.loginPage("/user/login"));

        return http.build();
      }

    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException
      {
        log.info(id);
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User Not Found"+id));
        return user;
      }

//    @Bean
//    public AccessDeniedHandler accessDeniedHandler()
//      {
//        return new ();
//      }
  }
