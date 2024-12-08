package dev.theezzfix.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import dev.theezzfix.exception.UserAlreadyExistsException;
import dev.theezzfix.model.LoginRequest;
import dev.theezzfix.model.LoginResponse;
import dev.theezzfix.model.RegisterRequest;
import dev.theezzfix.model.UpdateUserRequest;
import dev.theezzfix.model.User;
import dev.theezzfix.service.UserService;
import dev.theezzfix.service.FileStorageService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/users")  
@CrossOrigin(origins = "*")   
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private FileStorageService fileStorageService;

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

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest updateRequest) {
        try {
            ObjectId objectId = new ObjectId(id);
            User updatedUser = service.updateUser(objectId, updateRequest);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) { 
            return new ResponseEntity<>("Invalid user ID format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
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

    @PostMapping("/{userId}/profile-image")
    public ResponseEntity<User> uploadProfileImage(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) throws IOException {
        User updatedUser = service.updateProfileImage(userId, file);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{userId}/profile-image")
    public ResponseEntity<?> getProfileImage(@PathVariable String userId) {
        try {
            ObjectId objectId = new ObjectId(userId);
            User user = service.findUserById(objectId);
            if (user == null || user.getProfileImageId() == null) {
                return ResponseEntity.notFound().build();
            }

            Optional<GridFsResource> resourceOptional = fileStorageService.getFile(user.getProfileImageId());
            if (resourceOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            GridFsResource resource = resourceOptional.get();
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{userId}/profile-image")
    public ResponseEntity<?> updateProfileImagePatch(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) {
        try {
            User updatedUser = service.updateProfileImage(userId, file);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/profile-image")
    public ResponseEntity<?> deleteProfileImage(@PathVariable String userId) {
        try {
            User updatedUser = service.deleteProfileImage(userId);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}