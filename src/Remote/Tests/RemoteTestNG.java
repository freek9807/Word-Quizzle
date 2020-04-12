package Remote.Tests;

import Remote.C_RMI_API_Client;
import Remote.Exception.PasswordNotValidException;
import Remote.Exception.UserAlreadyExistsException;
import Models.SignedUpUsersListModel;
import Models.UserModel;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;
import static org.testng.Assert.*;
/**
 * Questa classse testa la registrazione lato Server
 * Facendo uno stress test da 100 utenti
 *
 * @author Federico Pennino
 */
public class RemoteTestNG {
    /**
     * Variabili d'istanza
     */
    AtomicInteger atomicInteger = new AtomicInteger(0);
    SignedUpUsersListModel val = new SignedUpUsersListModel();
    C_RMI_API_Client server = new C_RMI_API_Client(val);
    /**
     * Il costruttore
     * @throws RemoteException se l'RMI ha un problema
     */
    public RemoteTestNG() throws RemoteException {}
    /**
     * Ripristino ai valori di partenza
     */
    @BeforeClass
    public void doSomething() {
        //val.restore();
    }
    /**
     * Controllo se gli oggetti vengono costruiti adeguatamente
     */
    @Test
    public void UserModelTest() {
        UserModel um = new UserModel().setUser("f.pennino").setPassword("prova1");
        assertEquals(um.toString(),"UserModel{" +
                "user='" + "f.pennino" + '\'' +
                ", password='" + "prova1" + '\'' +
                '}');
    }
    /**
     * Eseguo lo stress test con 15 Thread e 100 chiamate
     * @throws UserAlreadyExistsException Se l'utente esiste già
     * @throws PasswordNotValidException Se la password non è valida
     */
    @Test(threadPoolSize = 15, invocationCount = 100,  timeOut = 10000)
    public void SignedUpUsersListModelTest() throws UserAlreadyExistsException, PasswordNotValidException {
        int a = atomicInteger.addAndGet(1);
        assertTrue(server.registration("f.p"+a,"prova1"));
        SignedUpUsersListModel.addResult add = val.add(new UserModel().setUser("f.pennino"+a).setPassword("prova1"));
        assertEquals(add, SignedUpUsersListModel.addResult.OKAY);
        add = val.add(new UserModel().setUser("f.pennino"+a).setPassword("prova1"));
        assertEquals(add, SignedUpUsersListModel.addResult.EXISTS);
    }
}