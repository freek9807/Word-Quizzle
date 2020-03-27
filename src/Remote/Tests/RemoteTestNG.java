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

public class RemoteTestNG {

    AtomicInteger atomicInteger = new AtomicInteger(0);
    SignedUpUsersListModel val = new SignedUpUsersListModel();
    C_RMI_API_Client server = new C_RMI_API_Client(val);

    public RemoteTestNG() throws RemoteException {
    }

    @BeforeClass
    public void doSomething() {
        val.restore();
    }

    @Test
    public void UserModelTest() {
        UserModel um = new UserModel().setUser("f.pennino").setPassword("prova1");
        assertEquals(um.toString(),"UserModel{" +
                "user='" + "f.pennino" + '\'' +
                ", password='" + "prova1" + '\'' +
                '}');
    }

    @Test(threadPoolSize = 3, invocationCount = 9,  timeOut = 10000)
    public void SignedUpUsersListModelTest() throws RemoteException, UserAlreadyExistsException, PasswordNotValidException {
        int a = atomicInteger.addAndGet(1);
        assertTrue(server.registration("f.p"+a,"prova1"));
        SignedUpUsersListModel.addResult add = val.add(new UserModel().setUser("f.pennino"+a).setPassword("prova1"));
        assertEquals(add, SignedUpUsersListModel.addResult.OKAY);
        add = val.add(new UserModel().setUser("f.pennino"+a).setPassword("prova1"));
        assertEquals(add, SignedUpUsersListModel.addResult.EXISTS);
    }
}