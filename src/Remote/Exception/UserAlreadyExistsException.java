package Remote.Exception;

public class UserAlreadyExistsException extends Exception{
    public UserAlreadyExistsException(){
        super("L'utente è già presente tra quelli registrati! ");
    }
}
