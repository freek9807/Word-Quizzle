package Remote;

import Remote.Exception.PasswordNotValidException;
import Remote.Exception.UserAlreadyExistsException;
import Remote.Models.SignedUpUsersListModel;
import Remote.Models.UserModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class C_RMI_API_Client extends UnicastRemoteObject implements I_RMI_API_Client {

    protected C_RMI_API_Client() throws RemoteException { }

    @Override
    public boolean registration(String username, String password) throws RemoteException,PasswordNotValidException, UserAlreadyExistsException {
        if(username == null || password == null)
            throw new IllegalArgumentException("Utente non valido");

        if(password.equals(""))
            throw new PasswordNotValidException();

        SignedUpUsersListModel users = new SignedUpUsersListModel();

        SignedUpUsersListModel.addResult add = users.add(new UserModel().setUser(username).setPassword(password));

        switch (add){
            case FULL:
                return false;
            case EXISTS:
                throw new UserAlreadyExistsException();
            case OKAY:
                return true;

        }

        return false;
    }
}
