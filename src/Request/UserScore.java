package Request;
/**
 * Richiesta di punteggio per un utente
 *
 * @author Federico Pennino
 */
public class UserScore implements Request {
    // Il nome dell'utente di cui si vuole sapere il punteggio
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
