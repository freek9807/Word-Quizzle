package Request;
/**
 * Richiesta di amicizia da inviare al server
 *
 * @author Federico Pennino
 */
public class AddFriend implements Request {
    // Il nome dell'utente da aggiungere
    private String name;

    public void setUser(String name) {
        this.name = name;
    }

    public String getUser() {
        return name;
    }
}
