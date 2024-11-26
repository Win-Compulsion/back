package com.lkkp.runwith.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean(name = "appSecurityFilterChain")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**").permitAll() // Actuator 엔드포인트 접근 허용
                        .requestMatchers("/member/savedata").permitAll()
                        .anyRequest().permitAll() // 나머지 요청 허용
                )
                .formLogin(form -> form.loginPage("/login"));

        return http.build();
    }
}
