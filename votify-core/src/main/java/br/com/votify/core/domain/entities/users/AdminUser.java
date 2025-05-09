package br.com.votify.core.domain.entities.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
@SuperBuilder
public class AdminUser extends User {
    @Override
    public int getPermissions() {
        return PermissionFlags.ALL;
    }
}
