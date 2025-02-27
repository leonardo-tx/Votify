package br.com.votify.core.domain.entities.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("COMMON")
@NoArgsConstructor
public final class CommonUser extends User {
    public CommonUser(Long id, String userName, String name, String email, String password) {
        super(id, userName, name, email, password);
    }
}
