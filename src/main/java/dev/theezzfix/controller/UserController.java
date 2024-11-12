package dev.theezzfix.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.theezzfix.exception.UserAlreadyExistsException;
import dev.theezzfix.model.LoginRequest;
import dev.theezzfix.model.LoginResponse;
import dev.theezzfix.model.RegisterRequest;
import dev.theezzfix.model.User;
import dev.theezzfix.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")  
@CrossOrigin(origins = "*")   
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(service.findAllUsers(), HttpStatus.OK);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
       try{
            LoginResponse response = service.login(loginRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
       }catch (Exception e){
           return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
       }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try{
            LoginResponse response = service.register(registerRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (UserAlreadyExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout (@RequestHeader("Authorization") String refreshToken){
        if(refreshToken != null && refreshToken.startsWith("Bearer")){
            refreshToken = refreshToken.substring(7);
            service.logout(refreshToken);
            return new ResponseEntity<>("Logout successfully", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Invalid token", HttpStatus.BAD_REQUEST);
        }
    }

    
}