package Remote.Exception;
/**
 * Eccezione lanciata se la password inserita dall'utente non è valida
 *
 * @author Federico Pennino
 */
public class PasswordNotValidException extends Exception {
    /**
     * Il costruttore
     */
    public PasswordNotValidException(){
        super("La password inserita è vuota o non valida");
    }
}
