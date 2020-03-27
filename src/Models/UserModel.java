package Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class UserModel implements Serializable {

    public String user;
    public String password;
    private ArrayList<String> friends;

    public UserModel(){
        super();
        friends = new ArrayList<String>();
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public UserModel setUser(String user) {
        this.user = user;
        return this;
    }

    public UserModel setPassword(String password) {
        this.password = password;
        return this;
    }

    public ArrayList<String> addFriend(String friend){
        if(friends.contains(friend))
            return null;

        friends.add(friend);

        return friends;
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

    public boolean isEqual(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return  userModel.user.equals(this.user) && userModel.password.equals(this.password);

    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
