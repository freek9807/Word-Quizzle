package Request;
/**
 * Formato di una richiesta di stop all'attesa di una sfida
 *
 * @author Federico Pennino
 */
public class StopWaitingForMatch implements Request {

    private String name;
    @Override
    public String getUser() {
        return name;
    }

    @Override
    public void setUser(String user) {
        this.name = user;
    }
}
