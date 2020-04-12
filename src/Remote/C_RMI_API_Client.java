package Remote;

import Remote.Exception.PasswordNotValidException;
import Remote.Exception.UserAlreadyExistsException;
import Models.SignedUpUsersListModel;
import Models.UserModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
/**
 * Classe che implementa l'iterfaccia remota da usare per la registrazione
 *
 * @author Federico Pennino
 */
public class C_RMI_API_Client extends UnicastRemoteObject implements I_RMI_API_Client {
    /**
     * L'astrazione del file con gli utenti registrati
     */
    SignedUpUsersListModel users;
    /**
     * Costruttore
     * @param users l'utente che vuole registrarsi
     * @throws RemoteException se la connessione RMI restituisce un errore
     */
    public C_RMI_API_Client(SignedUpUsersListModel users) throws RemoteException {
        super();
        this.users = users;
    }
    /**
     * Il metodo che implementa la registrazione RMI
     * @param username lo username dell'utente
     * @param password la password dell'utente
     * @return se l'operazione ha avuto successo
     * @throws PasswordNotValidException Se la password non è valida
     * @throws UserAlreadyExistsException Se lo username esiste già
     */
    @Override
    public synchronized boolean registration(String username, String password) throws PasswordNotValidException, UserAlreadyExistsException {
        // Se non sono validi i dati passati
        if( username == null || password == null)
            throw new IllegalArgumentException("Utente non valido");
        // Se la password è vuota
        if(password.equals(""))
            throw new PasswordNotValidException();
        // Altrimenti provo ad aggiungere l'utente a quelli registrati
        SignedUpUsersListModel.addResult add = users.add(new UserModel().setUser(username).setPassword(password));
        // controllo il valore restituito
        switch (add){
            case FULL:
                System.out.println("Numero masssimo di utenti raggiunto. Impossibile registrare : " + username);
                return false;
            case EXISTS:
                System.out.println("Utente " + username + " già registrato. Impossibile procedere ");
                throw new UserAlreadyExistsException();
            case OKAY:
                System.out.println("Utente "+ username + " registrato.");
                return true;

        }
        return false;
    }
}
