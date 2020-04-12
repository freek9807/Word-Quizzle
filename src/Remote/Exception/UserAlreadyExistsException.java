package Remote.Exception;
/**
 *  Eccezione lanciata se l'utente esiste di già
 */
public class UserAlreadyExistsException extends Exception{
    /**
     * Il costruttore
     */
    public UserAlreadyExistsException(){
        super("L'utente è già presente tra quelli registrati! ");
    }
}
