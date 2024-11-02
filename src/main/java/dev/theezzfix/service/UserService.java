package dev.theezzfix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.theezzfix.model.LoginRequest;
import dev.theezzfix.model.LoginResponse;
import dev.theezzfix.model.User;
import dev.theezzfix.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public List<User> findAllUsers() {
        return repository.findAll();
    }

   public LoginResponse login(LoginRequest loginRequest) throws Exception {
        User user = repository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new Exception("User not found"));

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new Exception("Invalid password");
        }
     
        String token = UUID.randomUUID().toString();
        
        return new LoginResponse(token, user);
    }
}