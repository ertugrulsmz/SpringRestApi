package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String EMAIL_VERIFICATION_URL = "/users/email-verification";
    public static final String H2_CONSOLE = "/h2-console/**";


    /*
    This is non changing class so we have created intermediate class called appProperties
    Springapplication context is needed since this is not a bean class just a config type
     */
    public static String getTokenSecret(){
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");
        return appProperties.getTokenSecret();
    }
}
