package fi.robbie.loan.loan_calculator;

import fi.robbie.loan.loan_calculator.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailServiceImpl userDetailsService;

    //Configures the security filter chain for HTTP requests.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        return http
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                // Publicly accessible endpoints
                .requestMatchers(mvcMatcherBuilder.pattern("/css/**"), mvcMatcherBuilder.pattern("/login"), mvcMatcherBuilder.pattern("/register"), mvcMatcherBuilder.pattern("/logout")).permitAll()
                // Loan-related endpoints require authentication
                .requestMatchers(mvcMatcherBuilder.pattern("/loan-list"), mvcMatcherBuilder.pattern("/loan/**")).authenticated()
                // All other requests require authentication
                .anyRequest().authenticated())
            // Configure login page and default success URL
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/loan-list", true).permitAll())
            // Allow logout for all users
            .logout(logout -> logout.permitAll())
            .build();
    }

    // Configures global authentication with a custom user details service and password encoder.
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}