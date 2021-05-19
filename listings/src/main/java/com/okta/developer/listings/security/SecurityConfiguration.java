package com.okta.developer.listings.security;

import com.okta.spring.boot.oauth.Okta;
import com.okta.spring.boot.oauth.config.OktaOAuth2Properties;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final OktaOAuth2Properties oktaOAuth2Properties;

    public SecurityConfiguration(OktaOAuth2Properties oktaOAuth2Properties) {
        this.oktaOAuth2Properties = oktaOAuth2Properties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .oauth2ResourceServer().jwt();

        Okta.configureResourceServer401ResponseBody(http);
    }
}
