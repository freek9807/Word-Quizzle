package Client.Test;

import Remote.Exception.PasswordNotValidException;
import Remote.Exception.UserAlreadyExistsException;
import Remote.I_RMI_API_Client;
import org.testng.annotations.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.*;

public class ClientTest {

    AtomicInteger atomicInteger = new AtomicInteger(0);

    @Test(threadPoolSize = 3, invocationCount = 9,  timeOut = 10000)
    public void signup(){
        try{
            Registry reg = LocateRegistry.getRegistry(5099);
            I_RMI_API_Client data = (I_RMI_API_Client) reg.lookup("SignUp");
            int a = atomicInteger.addAndGet(1);
            System.out.println("Connesso "+a);
            data.registration("f.p"+a,"prova1");
        }catch(RemoteException | NotBoundException | UserAlreadyExistsException | PasswordNotValidException e){
            e.printStackTrace();
        }
    }
}