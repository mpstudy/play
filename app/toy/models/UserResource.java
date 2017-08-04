package toy.models;

import toy.entities.UserData;

public class UserResource {

    protected String email;
    protected String password;

    public UserResource() {

    }

    public UserResource(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserResource(UserData userData) {
        this.email = userData.email;
        this.password = userData.password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
