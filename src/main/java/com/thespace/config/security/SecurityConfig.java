package com.thespace.config.security;

import com.thespace.spaceweb.user.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig implements UserDetailsService {

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final AuthSuccessHandler authSuccessHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final UserRepository userRepository;
    private final DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "PATCH",
            "OPTIONS"
        ));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList(
            "X-CSRF-TOKEN",
            "X-XSRF-TOKEN",
            "Content-Type",
            "Authorization"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                new AntPathRequestMatcher("/user"),
                new AntPathRequestMatcher("/user/login"),
                new AntPathRequestMatcher("/user/logout"),
                new AntPathRequestMatcher("/user/login/me")
            ).csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers(
                "/**").permitAll())
            .exceptionHandling((exception) -> exception.accessDeniedHandler(accessDeniedHandler))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(formLogin -> formLogin.loginPage("/user/login")
                .usernameParameter("id")
                .passwordParameter("password")
                .successHandler(authSuccessHandler)
                .failureHandler(new CustomAuthenticationFailureHandler()))
            .rememberMe(rememberMe -> rememberMe.key("13TGT3gr@%g21$")
                .rememberMeParameter("remember")
                .tokenValiditySeconds(60 * 60 * 24 * 7)
                .tokenRepository(persistentTokenRepository())
                .useSecureCookie(false)
                .authenticationSuccessHandler(authSuccessHandler))
            .logout(logout -> logout.logoutUrl("/user/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .invalidateHttpSession(true));

        return http.build();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        log.info(id);
        return userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + id));
    }
}
