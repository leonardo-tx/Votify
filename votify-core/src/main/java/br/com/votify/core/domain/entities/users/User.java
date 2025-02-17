package br.com.votify.core.domain.entities.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "TB_USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String document;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserTypeEnum role;

    public User() {
    }

    public User(Integer id, String firstName, String lastName, String document, String email, UserTypeEnum role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.email = email;
        this.role = role;
    }
}
