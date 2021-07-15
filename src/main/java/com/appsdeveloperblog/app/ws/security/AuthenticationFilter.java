package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.SpringApplicationContext;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter  extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        UserLoginRequestModel userLoginRequestModel = null;
        try {
            userLoginRequestModel = new ObjectMapper()
                    .readValue(request.getInputStream(), UserLoginRequestModel.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginRequestModel.getEmail(),
                            userLoginRequestModel.getPassword(),
                            new ArrayList<>()
                    ));

        } catch (IOException e) {
            throw new RuntimeException();
        }


    }

    /*
    1- We could mark this class bean and directly autowire userservice
    2- We could mark springapplicationcontext as a component other than bean
    3- We could directly say Userservice u = new UserServiceImpl();
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = ((MyCustomUserDetails)authResult.getPrincipal()).getUsername();

        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis()+SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();

        UserService userService = (UserService)SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto = userService.getUser(username); //İn login username is filled with email. so username == ...@gmail.com

        response.addHeader(SecurityConstants.HEADER_STRING,SecurityConstants.TOKEN_PREFIX+token);
        response.addHeader("UserID", userDto.getUserId());
    }

}
