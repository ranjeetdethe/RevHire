package com.revhire.dao;

import com.revhire.model.User;
import java.util.Optional;

public interface UserDAO {
    User createUser(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(int id);

    // In a real enterprise app, we might check email existence separately
    boolean emailExists(String email);

    boolean updatePassword(String email, String newPassword);

}
