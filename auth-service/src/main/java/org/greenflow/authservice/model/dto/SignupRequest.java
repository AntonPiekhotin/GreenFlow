package org.greenflow.authservice.model.dto;

import com.fasterxml.jackson.databind.annotation.EnumNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import util.ValidRole;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignupRequest {

    @NotBlank
    @Size(max = 50)
    @Email
    String email;

    @NotBlank
    @Size(min = 6, max = 40)
    String password;

    @NotBlank
    @ValidRole
    String role;
}