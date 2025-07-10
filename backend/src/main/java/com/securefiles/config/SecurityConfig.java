package main.java.com.securefiles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // login, register are open
                        .anyRequest().authenticated())
                .formLogin(withDefaults()) // use Spring's default login form/session handling
                .logout(withDefaults()); // allow logout
        return http.build();
    }
}
