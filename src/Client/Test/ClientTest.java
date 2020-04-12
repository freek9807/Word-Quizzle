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

/**
 * Classe di test per il Client
 *
 * @author Federico Pennino
 */
public class ClientTest {
    /**
     *  Valore che aumenta ad ogni Thread, in modo da testare sempre client diversi
     */
    AtomicInteger atomicInteger = new AtomicInteger(0);
    /**
     * Test per controllare se a livello di concorrenza la registrazione del Client funziona
     */
    @Test(threadPoolSize = 3, invocationCount = 100,  timeOut = 10000)
    public void signUp(){
        try{
            // Mi connetto tramite RMI
            Registry reg = LocateRegistry.getRegistry(5099);
            I_RMI_API_Client data = (I_RMI_API_Client) reg.lookup("SignUp");
            int a = atomicInteger.addAndGet(1);
            // Provo a registrarmi
            data.registration("f.p"+a,"prova1");
            System.out.println("Esecuzione n.ro : "+a);
        }catch(RemoteException | NotBoundException | UserAlreadyExistsException | PasswordNotValidException e){
            e.printStackTrace();
        }
    }
}