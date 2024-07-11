package org.nikita.spingproject.filestorage.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.nikita.spingproject.filestorage.utils.PasswordMatches;
import org.nikita.spingproject.filestorage.utils.ValidEmail;

@Getter
@Setter
@Accessors(chain = true)
@PasswordMatches
public class UserDto {
    @NotNull
    @NotEmpty(message = "Empty field")
    @ValidEmail
    private String email;
    @NotNull
    @NotEmpty(message = "Empty field")
    @Size(min=3, max = 16, message = "Password length must be from 3 to 16")
    private String password;
    private String matchingPassword;
}
