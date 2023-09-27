package ua.datastech.omnitracker.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final DataSource dataSource;

    private final BasicAuthenticationEntryPoint basicAuthenticationEntryPoint;

    public SecurityConfiguration(DataSource dataSource, BasicAuthenticationEntryPoint basicAuthenticationEntryPoint) {
        this.dataSource = dataSource;
        this.basicAuthenticationEntryPoint = basicAuthenticationEntryPoint;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select LOGIN, PASSWORD, STATUS from OMNI_USERS where LOGIN = ?")
                .authoritiesByUsernameQuery("SELECT LOGIN, 'ROLE_USER' FROM OMNI_USERS WHERE LOGIN = ?");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .disable()
                .headers()
                    .frameOptions()
                    .disable()
                .and()
                    .authorizeRequests()
                        .antMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .httpBasic()
                .authenticationEntryPoint(basicAuthenticationEntryPoint);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
