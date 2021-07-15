package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class AuthorizationFilter extends BasicAuthenticationFilter {
    private final UserRepository userRepository;

    public AuthorizationFilter(AuthenticationManager authenticationManager
                                ,UserRepository userRepository)
    {
        super(authenticationManager);
        this.userRepository = userRepository;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(SecurityConstants.HEADER_STRING);

        if(header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)){
            chain.doFilter(request,response);
            return;
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        chain.doFilter(request,response);
    }

    /*
    Checking if given token is correct by jwts
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);

        if(token != null){
            token = token.replace(SecurityConstants.TOKEN_PREFIX,"");

            String user = Jwts.parser()
                    .setSigningKey(SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token).getBody().getSubject();

            if(user != null){
                UserEntity userEntity = userRepository.findUserByEmail(user);
                if(userEntity == null) return null;

                MyCustomUserDetails myCustomUserDetails = new MyCustomUserDetails(userEntity);
                return new UsernamePasswordAuthenticationToken(myCustomUserDetails,null,myCustomUserDetails.getAuthorities());
                //return new UserNamePassword(user,...,..) --> this also works
            }

            return null;
        }
        return null;
    }


}
