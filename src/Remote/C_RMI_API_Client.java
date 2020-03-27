package Remote;

import Remote.Exception.PasswordNotValidException;
import Remote.Exception.UserAlreadyExistsException;
import Models.SignedUpUsersListModel;
import Models.UserModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class C_RMI_API_Client extends UnicastRemoteObject implements I_RMI_API_Client {

    SignedUpUsersListModel users;

    public C_RMI_API_Client(SignedUpUsersListModel users) throws RemoteException {
        super();
        this.users = users;
    }

    @Override
    public synchronized boolean registration(String username, String password) throws RemoteException,PasswordNotValidException, UserAlreadyExistsException {
        if(username == null || password == null)
            throw new IllegalArgumentException("Utente non valido");

        if(password.equals(""))
            throw new PasswordNotValidException();

        SignedUpUsersListModel.addResult add = users.add(new UserModel().setUser(username).setPassword(password));


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
