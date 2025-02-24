package br.com.votify.core.domain.entities.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MODERATOR")
public final class ModeratorUser extends User {
    public ModeratorUser() {
    }

    public ModeratorUser(Long id, String userName, String name, String email, String password) {
        super(id, userName, name, email, password);
    }
}
