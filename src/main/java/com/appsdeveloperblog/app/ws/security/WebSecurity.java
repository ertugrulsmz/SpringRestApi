package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    // User Service will extend userdetails service
    private final UserService userDetailsService;
    private  final BCryptPasswordEncoder bcryptPasswordEncoder;
    private final UserRepository userRepository;

    public WebSecurity(UserService userService,BCryptPasswordEncoder bcryptPasswordEncoder
                        ,UserRepository userRepository){
        this.userDetailsService = userService;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
        this.userRepository = userRepository;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST,SecurityConstants.SIGN_UP_URL)
                .permitAll()
                .antMatchers(HttpMethod.GET,SecurityConstants.EMAIL_VERIFICATION_URL)
                .permitAll()
                .antMatchers(SecurityConstants.H2_CONSOLE)
                .permitAll()
                .antMatchers(HttpMethod.DELETE,"/users/**").hasRole("ADMIN")
                .anyRequest().authenticated().and()
                .addFilter(getAuthenticationFilter()) // my defined function below.
                .addFilter(new AuthorizationFilter(authenticationManager(),userRepository))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    //To not to create session which caches previously entered credentials (bearer header)
        http.headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bcryptPasswordEncoder);
    }

    /*
    * Defined by me, normally in configure we would create new filter.
    * now we create new filter here with adjustment. we could move this function above
     */
    public AuthenticationFilter getAuthenticationFilter() throws Exception {
        // class that we extended
        AuthenticationFilter authenticationObj = new AuthenticationFilter(authenticationManager());
        authenticationObj.setFilterProcessesUrl("/users/login");
        return authenticationObj;

    }
}
