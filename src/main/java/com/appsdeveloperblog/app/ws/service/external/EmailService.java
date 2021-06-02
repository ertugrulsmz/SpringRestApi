package com.appsdeveloperblog.app.ws.service.external;



public interface EmailService {

    public void sendSimpleMessage(String to, String subject, String text);
}
