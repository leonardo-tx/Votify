package br.com.votify.core.domain.entities.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("COMMON")
@NoArgsConstructor
@SuperBuilder
public final class CommonUser extends User {
}
