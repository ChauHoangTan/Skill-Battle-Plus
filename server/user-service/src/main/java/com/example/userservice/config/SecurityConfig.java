package com.example.userservice.config;

import com.example.userservice.filter.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // Cho phép tất cả domain (hoặc set domain cụ thể)
        configuration.addAllowedMethod("*"); // Cho phép tất cả method: GET, POST, PUT, DELETE,...
        configuration.addAllowedHeader("*"); // Cho phép tất cả headers
        configuration.setAllowCredentials(true); // Nếu cần gửi cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        author -> author
                                .requestMatchers(HttpMethod.POST, "/users/api/profile").permitAll()
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/users/v3/**","/v3/**",
                                        "/users/webjars/**",
                                        "/users/swagger-ui/**",
                                        "/users/swagger-ui.html").permitAll()
                                .requestMatchers("/users/api/**").hasRole("USER")
                                .anyRequest().authenticated()
                )
//                .cors()
                .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
