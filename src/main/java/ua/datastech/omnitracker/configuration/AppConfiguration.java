package ua.datastech.omnitracker.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.client.RestTemplate;
import ua.datastech.omnitracker.service.security.AppBasicAuthenticationEntryPoint;

import java.util.Collections;

@Configuration
public class AppConfiguration {

    @Bean
    public BasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
        return new AppBasicAuthenticationEntryPoint();
    ***REMOVED***

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();
        restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        return restTemplate;
    ***REMOVED***

***REMOVED***
