package Request;

import java.io.Serializable;

public class ListFriends implements Serializable {
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
