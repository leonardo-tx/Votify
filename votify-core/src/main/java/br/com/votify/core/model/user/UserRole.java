package br.com.votify.core.model.user;

import lombok.Getter;

@Getter
public enum UserRole {
    COMMON(PermissionFlags.NONE),
    MODERATOR(PermissionFlags.MODERATOR),
    ADMIN(PermissionFlags.ALL);

    private final int permissionFlags;

    UserRole(int permissionFlags) {
        this.permissionFlags = permissionFlags;
    }
}
