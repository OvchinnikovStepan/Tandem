package com.tandem.auth_service.repository;

import static com.tandem.jooq.tables.Users.USERS;
import com.tandem.auth_service.model.User;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final DSLContext dsl;

    public UserRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return dsl.selectFrom(USERS)
            .where(USERS.ID.eq(id))
            .fetchOptional()
            .map(record -> mapToUser(record));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return dsl.selectFrom(USERS)
            .where(USERS.EMAIL.eq(email))
            .fetchOptional()
            .map(record -> mapToUser(record));
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return dsl.selectFrom(USERS)
            .where(USERS.PHONE_NUMBER.eq(phoneNumber))
            .fetchOptional()
            .map(record -> mapToUser(record));
    }

    @Override
    public User save(User user) {

        var record = dsl.insertInto(USERS)
                .set(USERS.ID, user.getId())
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.PHONE_NUMBER, user.getPhoneNumber())
                .set(USERS.PASSWORD_HASH, user.getPasswordHash())
                .set(USERS.EMAIL_VERIFIED, user.isEmailVerified())
                .set(USERS.PHONE_VERIFIED, user.isPhoneVerified())
                .set(USERS.CREATED_AT, user.getCreatedAt())
                .set(USERS.LAST_LOGIN_AT, user.getLastLoginAt())
                .returning(USERS.ID)
                .fetchOne();

        user.setId(record.getId());

        return user;
    }
    
    @Override
    public User update(User user) {

        dsl.update(USERS)
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.PHONE_NUMBER, user.getPhoneNumber())
                .set(USERS.PASSWORD_HASH, user.getPasswordHash())
                .set(USERS.EMAIL_VERIFIED, user.isEmailVerified())
                .set(USERS.PHONE_VERIFIED, user.isPhoneVerified())
                .set(USERS.CREATED_AT, user.getCreatedAt())
                .set(USERS.LAST_LOGIN_AT, user.getLastLoginAt())
                .where(USERS.ID.eq(user.getId()))
                .execute();

        return user;
    }
    private User mapToUser(org.jooq.Record record) {
        return User.builder()
            .id(record.get(USERS.ID))
            .email(record.get(USERS.EMAIL))
            .phoneNumber(record.get(USERS.PHONE_NUMBER))
            .passwordHash(record.get(USERS.PASSWORD_HASH))
            .emailVerified(record.get(USERS.EMAIL_VERIFIED))
            .phoneVerified(record.get(USERS.PHONE_VERIFIED))
            .createdAt(record.get(USERS.CREATED_AT))
            .lastLoginAt(record.get(USERS.LAST_LOGIN_AT))
            .build();
    }
}
