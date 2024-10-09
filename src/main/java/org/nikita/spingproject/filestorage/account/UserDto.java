package org.nikita.spingproject.filestorage.account;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nikita.spingproject.filestorage.utils.PasswordMatches;
import org.nikita.spingproject.filestorage.utils.ValidEmail;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches
public class UserDto {
    private int id;
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
