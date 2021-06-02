package com.appsdeveloperblog.app.ws.exception;

import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(value = {UserServiceException.class})
    public ResponseEntity<Object> handlerUserServiceException(UserServiceException userServiceException, WebRequest request){

        ErrorMessage errorMessage = new ErrorMessage(new Date(),userServiceException.getMessage());

        return new ResponseEntity<Object>(errorMessage,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleGeneralException(Exception exception, WebRequest request){

        ErrorMessage errorMessage = new ErrorMessage(new Date(),exception.getMessage());

        return new ResponseEntity<Object>(errorMessage,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

*/

}
