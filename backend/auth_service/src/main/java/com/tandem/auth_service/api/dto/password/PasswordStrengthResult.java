package com.tandem.auth_service.api.dto.password;

import java.util.List;
import java.util.Map;

import com.tandem.auth_service.api.dto.response.CheckPasswordStrengthResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordStrengthResult{

        int score;

        PasswordStrength strength;

        boolean valid;

        List<PasswordFeedback> feedback;

        List<PasswordFeedback> warnings;
        
        public CheckPasswordStrengthResponse mapToResponse() {

        List<String> feedback =
                this.feedback.stream()
                        .map(f -> f.message())
                        .toList();

        Map<String, Object> requirements = Map.of(
                "valid", this.valid,
                "warnings",
                this.warnings
                        .stream()
                        .map(w -> w.message())
                        .toList()
        );

        return new CheckPasswordStrengthResponse(
                this.strength,
                this.score,
                feedback,
                requirements
        );
    }
}