package br.com.votify.core.domain.entities.users;

import lombok.Getter;

@Getter
public enum PermissionFlags {
    NONE(0);

    private final int value;

    PermissionFlags(int value) {
        this.value = value;
    }
}
