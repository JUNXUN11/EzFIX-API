package dev.theezzfix.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.theezzfix.exception.UserAlreadyExistsException;
import dev.theezzfix.model.LoginRequest;
import dev.theezzfix.model.LoginResponse;
import dev.theezzfix.model.RegisterRequest;
import dev.theezzfix.model.User;
import dev.theezzfix.repository.UserRepository;

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
        
        User savedUser = repository.save(newUser);

        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenStore.put(savedUser.getUsername(), refreshToken);

        return new LoginResponse(accessToken, refreshToken, savedUser);
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
}
