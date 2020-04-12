package Remote;

import Remote.Exception.PasswordNotValidException;
import Remote.Exception.UserAlreadyExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 *  Interfaccia RMI relativa alla connessione RMI
 */
public interface I_RMI_API_Client extends Remote {
    /**
     * L'interfaccia che descrive l'oggetto da chiamare in RMI
     * @param username lo username dell'utente
     * @param password la password dell'utente
     * @return se l'operazione ha successo o meno
     * @throws RemoteException se ci sono problemi con l'RMI
     * @throws PasswordNotValidException se la password non è valida
     * @throws UserAlreadyExistsException se l'utente esiste già
     */
    boolean registration(String username,String password) throws RemoteException,PasswordNotValidException, UserAlreadyExistsException;
}
