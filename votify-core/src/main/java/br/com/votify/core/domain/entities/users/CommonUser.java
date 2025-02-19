package br.com.votify.core.domain.entities.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("COMMON")
public final class CommonUser extends User {
    public CommonUser() {
    }

    public CommonUser(Long id, String userName, String name, String email, String password) {
        super(id, userName, name, email, password);
    }
}
