Some notes on the Whydah Spring Security integration
====================================================

The current integration is attempted to be as closely aligned to the typical Spring Security for Spring Boot applications as possible.

Some links to setting up Spring Security for Spring Boot

 * https://www.baeldung.com/spring-security-basic-authentication
 * https://www.baeldung.com/spring-security-authentication-provider
 * https://www.baeldung.com/spring-security-multiple-entry-points
 
 In short, we have added a WebSecurityWhydahConfiguration which in its simplest form will look something like this


## Example configuration config 
 ````
 package api.auth;
 
 import net.whydah.identity.spring.WhydahAuthenticationProvider;
 import net.whydah.identity.spring.WhydahEntryPoint;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.ComponentScan;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.core.annotation.Order;
 import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
 import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
 import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
 import org.springframework.web.client.RestTemplate;
 
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
     private WhydahAuthenticationProvider authProvider;
 
     @Bean
     public RestTemplate restTemplate() {
         return new RestTemplate();
     }
 
 
     @Override
     protected void configure(HttpSecurity httpSecurity) throws Exception {
         httpSecurity.csrf().disable();
         httpSecurity.antMatcher("/user/**")
                 .authorizeRequests().anyRequest().hasAnyRole()
                 .and().httpBasic().authenticationEntryPoint(whydahEntryPoint);
     }
 
     @Override
     protected void configure(AuthenticationManagerBuilder auth) throws Exception {
         auth.authenticationProvider(authProvider);
     }
 }
 ````


## Example configuration config if you want to add Basic username/password fallback
 ````
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
 ````
    

## BeanConfiguration
 ````
 @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }  
 ````

  
## Example maven config   
 ````
 	<properties>
 		<java.version>1.8</java.version>
 		<whydah-typelib-version>2.5.3</whydah-typelib-version>
 	</properties>
 	
 	...
 	
  	<repositories>
  		<!-- Needed for parent  -->
  		<repository>
  			<id>cantara-releases</id>
  			<name>Cantara Release Repository</name>
  			<url>https://mvnrepo.cantara.no/content/repositories/releases/</url>
  		</repository>
  		<repository>
  			<id>cantara-snapshots</id>
  			<name>Cantara Snapshot Repository</name>
  			<url>https://mvnrepo.cantara.no/content/repositories/snapshots/</url>
  		</repository>
  
  	</repositories>
...
    <dependencies>
		<dependency>
			<groupId>net.whydah.sso</groupId>
			<artifactId>Whydah-TypeLib</artifactId>
			<version>${whydah-typelib-version}</version>
		</dependency>

	</dependencies>
 ````
