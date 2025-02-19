package br.com.votify.core.domain.entities.users;

import lombok.Getter;

@Getter
public enum UserTypeEnum {
    COMMON(0),
    MODERATOR(1),
    ADMIN(2);


    private final Integer userType;

    UserTypeEnum(Integer userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return name();
    }
}
