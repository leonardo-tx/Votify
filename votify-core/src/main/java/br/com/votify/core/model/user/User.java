package br.com.votify.core.model.user;

import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.Password;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.service.user.PasswordEncoderService;
import lombok.Getter;

@Getter
public final class User {
    private final Long id;
    private UserName userName;
    private Name name;
    private Email email;
    private String encryptedPassword;
    private final UserRole role;
    private final boolean active;

    public User(PasswordEncoderService passwordEncoderService, UserRegister userRegister) {
        if (passwordEncoderService == null) {
            throw new IllegalArgumentException("The password encoder must not be null.");
        }
        if (userRegister == null) {
            throw new IllegalArgumentException("The user register must not be null.");
        }

        this.id = null;
        this.userName = userRegister.getUserName();
        this.name = userRegister.getName();
        this.email = userRegister.getEmail();
        this.encryptedPassword = passwordEncoderService.encryptPassword(userRegister.getPassword());
        this.role = UserRole.COMMON;
        this.active = false;
    }

    private User(
            Long id,
            Email email,
            UserName userName,
            Name name,
            String encryptedPassword,
            UserRole role,
            boolean active
    ) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.name = name;
        this.encryptedPassword = encryptedPassword;
        this.role = role;
        this.active = active;
    }

    public void setPassword(PasswordEncoderService passwordEncoderService, Password password) {
        if (passwordEncoderService == null) {
            throw new IllegalArgumentException("The password encoder must not be null.");
        }
        if (password == null) {
            throw new IllegalArgumentException("Cannot set null to the password field.");
        }
        encryptedPassword = passwordEncoderService.encryptPassword(password);
    }

    public void setName(Name name) {
        if (name == null) {
            throw new IllegalArgumentException("Cannot set null to the name field.");
        }
        this.name = name;
    }

    public void setEmail(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Cannot set null to the email field.");
        }
        this.email = email;
    }

    public void setUserName(UserName userName) {
        if (userName == null) {
            throw new IllegalArgumentException("Cannot set null to the userName field.");
        }
        this.userName = userName;
    }

    public int getPermissions() {
        return role.getPermissionFlags();
    }

    public boolean hasPermission(int permissionFlags) {
        return (getPermissions() & permissionFlags) != 0;
    }

    public static User parseUnsafe(
            Long id,
            Email email,
            UserName userName,
            Name name,
            String encryptedPassword,
            UserRole userRole,
            boolean active
    ) {
        return new User(id, email, userName, name, encryptedPassword, userRole, active);
    }
}
