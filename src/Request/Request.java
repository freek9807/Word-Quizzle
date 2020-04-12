package Request;

import java.io.Serializable;
/**
 *  Template di una richiesta
 *
 * @author Federico Pennino
 */
public interface Request extends Serializable {
    /**
     * Restituisce l'utente a cui si riferisce l'operazione
     * @return restituisce l'utente a cui si riferisce la classe
     */
    String getUser();
    /**
     * Imposta l'utente a cui si riferisce l'operazione
     * @param user imposta l'utente a cui si riferisce la classe
     */
    void setUser(String user);
}
