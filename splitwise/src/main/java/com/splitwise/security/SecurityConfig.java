package com.splitwise.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    private AuthenticationSuccessHandler oAuth2SuccessHandler;


    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(getPasswordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public JWTAuthProvider jwtAuthProvider(){
        return new JWTAuthProvider(jwtUtil,userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,AuthenticationManager authenticationManager,JWTUtil jwtUtil) throws Exception {

        System.out.println("check spring security filter chain");
        JWTAuthFilter jwtAuthFilter = new JWTAuthFilter(authenticationManager,jwtUtil);
        JWTValidationFilter jwtValidationFilter = new JWTValidationFilter(authenticationManager,jwtUtil);
        OAuthValidationFilter oAuthValidationFilter1 = new OAuthValidationFilter(jwtUtil);
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/User/signup","/auth/**", "/oauth2/**","/error").permitAll()
                .anyRequest().authenticated())
                .csrf(csrf->csrf.disable())
                .oauth2Login(oAuth -> oAuth.loginPage("/oauth2/authorization/google").successHandler(oAuth2SuccessHandler))
//                .oauth2ResourceServer(oauth2->oauth2.jwt(Customizer.withDefaults()))

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtValidationFilter,JWTAuthFilter.class)
                .addFilterBefore(oAuthValidationFilter1,UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    String acceptHeader = request.getHeader("Accept");
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        // If client tried with JWT but invalid/missing
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Invalid or missing JWT token\"}");
                    } else {
                        // Browser flow → redirect to login
                        if (acceptHeader != null && acceptHeader.contains("application/json")) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        } else {
                            response.sendRedirect("/login");
                        }
                    }
                })
        );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(){

        return new ProviderManager(List.of(daoAuthenticationProvider(),jwtAuthProvider()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec("mysecretkeymysecretkey".getBytes(), "HmacSHA256")
        ).build();
    }
}
