package Request;

import java.io.Serializable;

public class AddFriend implements Serializable {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
