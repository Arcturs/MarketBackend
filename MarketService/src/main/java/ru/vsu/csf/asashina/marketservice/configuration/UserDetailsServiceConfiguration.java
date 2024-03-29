package ru.vsu.csf.asashina.marketservice.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.vsu.csf.asashina.marketservice.service.UserService;

@Configuration
@AllArgsConstructor
public class UserDetailsServiceConfiguration {

    private final UserService userService;

    @Bean
    public UserDetailsService userDetailsService() {
        return userService::getUserByEmail;
    }
}
