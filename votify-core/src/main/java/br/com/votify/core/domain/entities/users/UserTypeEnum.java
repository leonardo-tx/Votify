package br.com.votify.core.domain.entities.users;

import lombok.Getter;

@Getter
public enum UserTypeEnum {
    ADMIN("Admin"),
    COMMON("Common");

    private final String userType;

    UserTypeEnum(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return userType;
    }
}
