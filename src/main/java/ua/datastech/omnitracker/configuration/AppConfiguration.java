package ua.datastech.omnitracker.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import ua.datastech.omnitracker.service.security.AppBasicAuthenticationEntryPoint;

@Configuration
@EnableScheduling
public class AppConfiguration {

    @Bean
    public BasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
        return new AppBasicAuthenticationEntryPoint();
    ***REMOVED***

***REMOVED***
