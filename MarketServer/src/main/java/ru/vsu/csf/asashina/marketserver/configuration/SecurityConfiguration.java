package ru.vsu.csf.asashina.marketserver.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.vsu.csf.asashina.marketserver.filter.AuthenticationFilter;
import ru.vsu.csf.asashina.marketserver.model.enums.RoleName;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration implements WebMvcConfigurer {

    private final static String ADMIN_ROLE = RoleName.ADMIN.getName();

    private final AuthenticationFilter authenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests()
                .requestMatchers("/auth/*").permitAll()
                .requestMatchers(GET, "/categories/**", "/products/**", "/v3/api-docs/**",
                        "/swagger-ui/**", "/swagger-ui.html").permitAll()

                .requestMatchers(POST, "/categories/**", "/products").hasAnyAuthority(ADMIN_ROLE)
                .requestMatchers(PUT, "/products/*").hasAnyAuthority(ADMIN_ROLE)
                .requestMatchers(DELETE, "/categories/*", "/products/**").hasAnyAuthority(ADMIN_ROLE)

                .anyRequest().authenticated();

        http.authenticationProvider(authenticationProvider)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*");
    }
}
