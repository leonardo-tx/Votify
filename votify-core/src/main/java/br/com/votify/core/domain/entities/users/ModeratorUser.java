package br.com.votify.core.domain.entities.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("MODERATOR")
@NoArgsConstructor
@SuperBuilder
public final class ModeratorUser extends User {
    @Override
    public int getPermissions() {
        return PermissionFlags.MODERATOR;
    }
}
