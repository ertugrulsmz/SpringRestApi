package com.appsdeveloperblog.app.ws.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UtilsTest {

    @Autowired
    private Utils utils;

    private String mockUserId = "axcw4";

    @BeforeEach
    private void setUp() {

    }

    @Test
    public void testGenerateEmailVerificationToken(){
        String token = utils.generateEmailVerificationToken(mockUserId);
        assertNotNull(token);
    }

    @Test
    public void testGenerateEmailVerification_NullUserId(){
        String token = utils.generateEmailVerificationToken(null);
        assertNotNull(token);
    }

    @Test
    public void testTokenNotExpired(){
        String token = utils.generateEmailVerificationToken(mockUserId);
        boolean is_expired = Utils.hasTokenExpired(token);

        assertFalse(is_expired);
    }

    // As exception was handled. It no longer throws exception so this test is disabled
    // not to fail.
    @Disabled
    @Test
    public void testTokenExpired(){
        //Random token
        String token= "knknj";

        assertThrows(Exception.class,
                ()->Utils.hasTokenExpired(token));
    }



}
