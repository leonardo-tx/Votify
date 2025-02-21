package br.com.votify.core.domain.entities.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_USER")
public class User {
    public static final int ID_MIN_LENGTH = 3;
    public static final int ID_MAX_LENGTH = 40;
    public static final int EMAIL_MIN_LENGTH = 5;
    public static final int EMAIL_MAX_LENGTH = 254;
    public static final int FIRST_NAME_MIN_LENGTH = 1;
    public static final int FIRST_NAME_MAX_LENGTH = 50;
    public static final int LAST_NAME_MIN_LENGTH = 1;
    public static final int LAST_NAME_MAX_LENGTH = 50;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_BYTES = 72;

    @Id
    @Column(nullable = false, length = ID_MAX_LENGTH)
    private String id;

    @Column(nullable = false, length = FIRST_NAME_MAX_LENGTH)
    private String firstName;

    @Column(length = LAST_NAME_MAX_LENGTH)
    private String lastName;

    @Column(unique = true, nullable = false, length = EMAIL_MAX_LENGTH)
    private String email;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private UserTypeEnum role;

    @Column(nullable = false)
    private String password;

    public void updateFrom(User other) {
        if (other.id != null) {
            id = other.id;
        }
        if (other.firstName != null) {
            firstName = other.firstName;
        }
        if (other.lastName != null) {
            lastName = other.lastName;
        }
        if (other.email != null) {
            email = other.email;
        }
        if (other.role != null) {
            role = other.role;
        }
        if (other.password != null) {
            password = other.password;
        }
    }
}
