package Remote.Exception;

public class PasswordNotValidException extends Exception {
    public PasswordNotValidException(){
        super("La password inserita è vuota o non valida");
    }
}
