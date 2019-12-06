package com.example.demo.security;

import com.example.demo.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
        .antMatcher("/*").anonymous().and()
        .antMatcher("/api/**").authorizeRequests().and().httpBasic();

        // Les points accessibles à tout le monde
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/v1/user").anonymous()
                .antMatchers(HttpMethod.GET, "/api/v1/event").anonymous()
                .antMatchers(HttpMethod.POST, "/api/v1/user").anonymous()
                // Disallow everything else..
                ;//.anyRequest().authenticated();
        super.configure(http);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.inMemoryAuthentication().withUser("admin").password("admin");
        auth.userDetailsService(userDetailsServiceImpl);
        super.configure(auth);
    }
}
