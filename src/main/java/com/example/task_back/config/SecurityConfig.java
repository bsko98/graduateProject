
package com.example.task_back.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/loginPage","/signUp","/error","/login","/getComments/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST,"/joinGroup").permitAll()
                        .requestMatchers("/admin/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/index.html").permitAll()
                        .anyRequest().authenticated());
        http
                .formLogin((form)->form.loginPage("/loginPage")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/myPrayerList",true)
                        .permitAll());
        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .sessionManagement((session)->session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true));
        http
                .sessionManagement((session)->session
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession));
        http
                .logout((logout)->logout
                        .logoutUrl("/logout") // 로그아웃 URL (기본값: /logout)
                        .logoutSuccessUrl("/loginPage") // 로그아웃 성공 후 리다이렉트 경로
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                        .permitAll());
        return http.build();
    }


}

