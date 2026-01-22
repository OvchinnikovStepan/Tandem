package com.tandem.auth_service.repository;

import com.tandem.auth_service.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    User save(User user);
    User update(User user);
}
