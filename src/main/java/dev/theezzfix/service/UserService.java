package dev.theezzfix.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.theezzfix.exception.UserAlreadyExistsException;
import dev.theezzfix.model.LoginRequest;
import dev.theezzfix.model.LoginResponse;
import dev.theezzfix.model.RegisterRequest;
import dev.theezzfix.model.UpdateUserRequest;
import dev.theezzfix.model.User;
import dev.theezzfix.repository.UserRepository;
import dev.theezzfix.service.FileStorageService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, String> refreshTokenStore = new HashMap<>();

    private final FileStorageService fileStorageService;

    public UserService(UserRepository userRepository, FileStorageService fileStorageService) {
        this.repository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    public User findUserById(ObjectId id) {
        return repository.findById(id).orElse(null);
    }

    public List<User> findAllUsers() {
        return repository.findAll();
    }

   public LoginResponse login(LoginRequest loginRequest) throws Exception {
        User user = repository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new Exception("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new Exception("Invalid password");
        }
     
        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenStore.put(user.getUsername(), refreshToken);
        
        return new LoginResponse(accessToken, refreshToken, user);
    }

    public LoginResponse register(RegisterRequest registerRequest) throws UserAlreadyExistsException{
        Optional<User> existingUser = repository.findByUsername(registerRequest.getUsername());
        if(existingUser.isPresent()){
            throw new UserAlreadyExistsException("User already exists");
        }

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setRole("user");
        newUser.setProfileImageId(null);
        
        User savedUser = repository.save(newUser);

        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenStore.put(savedUser.getUsername(), refreshToken);

        return new LoginResponse(accessToken, refreshToken, savedUser);
    }

    public User updateUser(ObjectId id, UpdateUserRequest updateRequest) throws Exception {
        User user = repository.findById(id)
            .orElseThrow(() -> new Exception("User not found"));

        if (updateRequest.getUsername() != null && 
            !updateRequest.getUsername().equals(user.getUsername())) {
            
            Optional<User> existingUser = repository.findByUsername(updateRequest.getUsername());
            if (existingUser.isPresent()) {
                throw new UserAlreadyExistsException("Username already taken");
            }
            user.setUsername(updateRequest.getUsername());
        }

        if (updateRequest.getEmail() != null && 
            !updateRequest.getEmail().equals(user.getEmail())) {
            user.setEmail(updateRequest.getEmail());
        }

        return repository.save(user);
    }

    public void logout(String refreshToken){
        refreshTokenStore.values().removeIf(token->token.equals(refreshToken));
    };

    public LoginResponse refreshAccessToken (String refreshToken) throws Exception{
        if(refreshTokenStore.containsValue(refreshToken)){
            String newAccessToken = UUID.randomUUID().toString();
            return new LoginResponse(newAccessToken, refreshToken, null);
        }else{
            throw new Exception("Invalid refresh token");
        }
    };

    public User updateProfileImage(String userId, MultipartFile file) throws IOException {
        ObjectId objectId = new ObjectId(userId);
        User user = repository.findById(objectId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete old profile image if exists
        if (user.getProfileImageId() != null) {
            fileStorageService.deleteFile(user.getProfileImageId());
        }

        // Store new profile image
        String fileId = fileStorageService.storeFile(file);
        user.setProfileImageId(fileId);
        
        return repository.save(user);
    }

    public User deleteProfileImage(String userId) {
        ObjectId objectId = new ObjectId(userId);
        User user = repository.findById(objectId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfileImageId() != null) {
            fileStorageService.deleteFile(user.getProfileImageId());
            user.setProfileImageId(null);
            return repository.save(user);
        }
        
        return user;
    }
}
