package com.appsdeveloperblog.app.ws.service.external;

public class EmailConstants {
    private static String verificationSubject = "You need to verify your account, with sended token : $token ";

    public static String getVerificationSubject(String key){

        if (key!=null){
            return verificationSubject.replace("$token",key);
        }

        return verificationSubject;

    }
}
