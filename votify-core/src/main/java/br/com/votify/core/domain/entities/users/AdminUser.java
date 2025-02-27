package br.com.votify.core.domain.entities.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
public class AdminUser extends User {

    public AdminUser(Long id, String userName, String name, String email, String password) {
        super(id, userName, name, email, password);
    }

    @Override
    public int getPermissions() {
        return PermissionFlags.ALL;
    }
}
