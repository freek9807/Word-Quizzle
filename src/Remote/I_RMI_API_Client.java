package Remote;

import Remote.Exception.PasswordNotValidException;
import Remote.Exception.UserAlreadyExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface I_RMI_API_Client extends Remote {
    boolean registration(String username,String password) throws RemoteException,PasswordNotValidException, UserAlreadyExistsException;
}
