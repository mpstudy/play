package toy.entities;

import javax.persistence.*;

/**
 * Data returned from the database
 */
@Entity
@Table(name = "users")
public class UserData {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    @Column(unique=true)
    public String email;
    public String password;

    public UserData() {
    }

    public UserData(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
