package com.inn.ecommerce.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EcommerceUtils {

    private EcommerceUtils(){

    }
    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus){



        //return new ResponseEntity<String>("{\"messag\":\""+responseMessage+"\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<String>("{\"message\":\""+responseMessage+"\"}", httpStatus);

    }


}
