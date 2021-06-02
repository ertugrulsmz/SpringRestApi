package com.appsdeveloperblog.app.ws.shared;

import com.appsdeveloperblog.app.ws.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

import java.util.Date;
import java.util.Random;

@Component
public class Utils {

    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static boolean hasTokenExpired(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.getTokenSecret()) // key we embedded to encryption and we will decode by that
                .parseClaimsJws(token).getBody();

        Date tokenExpiration = claims.getExpiration();
        Date today = new Date();

        return tokenExpiration.before(today); // is expiration before today.
    }

    public String generateUserId(int length){
        return generateRandomString(length);
    }
    public String generateAddressId(int length){return generateRandomString(length);}

    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for(int i=0;i<length;i++){
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }

    public String generateEmailVerificationToken(String generatedUserId) {

        String token = Jwts.builder()
                .setSubject(generatedUserId) //this is just subject and in verification we don't care subject(id of user)
                                                                                                // for now ...
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()) // secret needs to be provided
                                                                        // during decoding to verify token
                .compact();
        // this is somehow encrypted token. in email verification it will be decoded with tokensecret we have provided
        // we will extract date and see if it is outdated
        return token;
    }
}
