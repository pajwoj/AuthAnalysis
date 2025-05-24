package pl.pajwoj.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class UserDTO {
    @NotBlank(message = "Email cannot be blank")
    String email;

    @NotBlank(message = "Password cannot be blank")
    String password;
}