package com.tandem.profile_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileRequest {

    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @Size(min = 1, max = 100, message = "Surname must be between 1 and 100 characters")
    private String surname;

    @Size(max = 20, message = "Phone number is too long")
    private String phoneNumber;

    @jakarta.validation.constraints.Email(message = "Email should be valid")
    private String email;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Size(max = 100, message = "City name is too long")
    private String city;

    private String placeOfWork;

    private String jobTitle;

    private String personalInterests;
}