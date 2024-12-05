package dev.theezzfix.model;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String username;
    private String email;
    private String currentPassword;
    private String newPassword;
}
