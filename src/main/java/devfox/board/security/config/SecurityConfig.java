package devfox.board.security.config;

import devfox.board.users.entity.UserRole;
import devfox.board.security.handler.CustomLogoutHandler;
import devfox.board.security.jwt.JWTFilter;
import devfox.board.security.jwt.JwtUtil;
import devfox.board.security.jwt.JwtService;
import devfox.board.security.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
public class   SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    @Qualifier("LoginSuccessHandler")
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    @Qualifier("SocialSuccessHandler")
    private final AuthenticationSuccessHandler socialSuccessHandler;


    private final JwtService jwtService;

    private final JwtUtil jwtUtil;

    public SecurityConfig(
            AuthenticationConfiguration authenticationConfiguration,
            @Qualifier("loginSuccessHandler")
                    AuthenticationSuccessHandler authenticationSuccessHandler,
            @Qualifier("socialSuccessHandler")
            AuthenticationSuccessHandler socialSuccessHandler,
            JwtService jwtService, JwtUtil jwtUtil) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.socialSuccessHandler = socialSuccessHandler;
        this.jwtService = jwtService;
        this.jwtUtil = jwtUtil;


    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager
            (AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((auth) -> auth.disable());

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http
                .httpBasic((auth) -> auth.disable());

        http
                .formLogin((auth) -> auth.disable());

        http
                .logout(logout ->
                        logout.addLogoutHandler
                                (new CustomLogoutHandler(jwtService, jwtUtil)));


        http
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(socialSuccessHandler));



        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/jwt/exchange", "/jwt/refresh").permitAll()
                        .requestMatchers( "/user/exist", "/user","/login","/join").permitAll()
                        .requestMatchers("/user/**").permitAll()
//                        .requestMatchers("/board/**","/board").authenticated()
                        .anyRequest().authenticated()
                );



        http
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        })

                        .accessDeniedHandler(((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN)
                        ))
                );

        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        http
                .addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), authenticationSuccessHandler),
                        UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        return http.build();
    }

    // CORS Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // 권한 계층
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(UserRole.ADMIN.name()).implies(UserRole.USER.name())
                .build();
    }

}
