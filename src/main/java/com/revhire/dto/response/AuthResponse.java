package com.revhire.dto.response;

import com.revhire.model.User.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private Long id;
    private String email;
    private UserRole role;
    private String firstName;
    private String lastName;
}
