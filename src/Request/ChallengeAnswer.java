package Request;

/**
 * Formato di una risposta a una richiesta di sfida
 *
 * @author Federico Pennino
 */
public class ChallengeAnswer implements Request {

    private String name;
    /**
     * Valore della risposta
     */
    private int response;

    /**
     * Il costruttore
     * @param name nome dell'utente a cui si riferisce la richiesta
     * @param response valore della riposta
     */
    public ChallengeAnswer(String name, int response) {
        this.name = name;
        this.response = response;
    }

    /**
     * Restituisce il valore della risposta
     * @return valore della risposta
     */
    public int getResponse() {
        return response;
    }

    @Override
    public String getUser() {
        return name;
    }

    @Override
    public void setUser(String user) { }
}
