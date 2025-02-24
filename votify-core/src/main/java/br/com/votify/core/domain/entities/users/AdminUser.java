package br.com.votify.core.domain.entities.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class AdminUser extends User {
    public AdminUser() {
    }

    public AdminUser(Long id, String userName, String name, String email, String password) {
        super(id, userName, name, email, password);
    }
}
