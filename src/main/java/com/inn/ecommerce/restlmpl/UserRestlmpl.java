package com.inn.ecommerce.restlmpl;

import com.inn.ecommerce.constents.EcommerceConstants;
import com.inn.ecommerce.service.UserService;
import com.inn.ecommerce.utils.EcommerceUtils;
import com.inn.ecommerce.wrapper.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.inn.ecommerce.rest.UserRest;
import org.springframework.web.bind.annotation.*;
import com.inn.ecommerce.dto.ChangePasswordRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserRestlmpl implements UserRest {


    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try{
            return userService.signUp(requestMap);
        }
        catch(Exception ex){
            ex.printStackTrace();


        }
        return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            return userService.login(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            return userService.getAllUsers(authHeader);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //@PostMapping("/user/update")

    @Override
    public ResponseEntity<String> updateUser(Map<String, String> requestMap) {
        try {
            return userService.updateUser(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // Endpoint pour changer le mot de passe
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> requestMap, @RequestHeader("Authorization") String authHeader) {
        return userService.updatePassword(requestMap, authHeader);
    }


    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> requestMap) {
        try {
            return userService.forgotPassword(requestMap);  // Appelle la méthode du service
        } catch (Exception ex) {
            ex.printStackTrace();
            return EcommerceUtils.getResponseEntity("Something went wrong.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}






