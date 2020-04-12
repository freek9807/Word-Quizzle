package Request;
/**
 * Richiesta di classifica degli amici
 *
 * @author Federico Pennino
 */
public class UserFriendsRank implements Request {
    // Il nome dell'utente che vuole la lista dei suoi amici
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
