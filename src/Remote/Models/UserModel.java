package Remote.Models;

import java.io.Serializable;
import java.util.Objects;

public class UserModel implements Serializable {

    private String user;
    private String password;

    public UserModel(){
        super();
    }

    public UserModel setUser(String user) {
        this.user = user;
        return this;
    }

    public UserModel setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return user.equals(userModel.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
