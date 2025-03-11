package br.com.votify.core.domain.entities.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "TB_USER")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "USER_TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class User {
    public static final int USER_NAME_MIN_LENGTH = 3;
    public static final int USER_NAME_MAX_LENGTH = 40;
    public static final int EMAIL_MIN_LENGTH = 5;
    public static final int EMAIL_MAX_LENGTH = 254;
    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 50;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_BYTES = 72;

    protected User() {
    }

    protected User(Long id, String userName, String name, String email, String password) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = USER_NAME_MAX_LENGTH)
    private String userName;

    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @Column(unique = true, nullable = false, length = EMAIL_MAX_LENGTH)
    private String email;

    @Column(nullable = false)
    private String password;

    public int getPermissions() {
        return PermissionFlags.NONE;
    }

    public boolean hasPermission(int permissionFlags) {
        return (getPermissions() & permissionFlags) != 0;
    }
}
