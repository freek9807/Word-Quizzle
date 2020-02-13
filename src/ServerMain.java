import Remote.Models.UserModel;

public class ServerMain {
    public static void main(String[] args) {
        UserModel user = new UserModel().setUser("f.pennino").setPassword("prova");
        System.out.println(user);
    }
}
