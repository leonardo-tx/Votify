package br.com.votify.dto.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatePasswordRequestDTO {
    private String oldPassword;
    private String newPassword;
} 