package net.whydah.identity.spring.config;

import net.whydah.identity.spring.WhydahAuthenticationProvider;
import net.whydah.identity.spring.WhydahEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@Order(1)
@ComponentScan("net.whydah")
public class WebSecurityWhydahConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private WhydahEntryPoint whydahEntryPoint;

    @Autowired
    private WhydahAuthenticationProvider authWProvider;


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity.antMatcher("/user/**")
                .authorizeRequests().anyRequest().hasAnyRole()
                .and()
                .httpBasic().authenticationEntryPoint(whydahEntryPoint)
                .and()
                .exceptionHandling()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureUrl("/?login_error")
                .successHandler(authenticationSuccessHandler);
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authWProvider);
    }

}
