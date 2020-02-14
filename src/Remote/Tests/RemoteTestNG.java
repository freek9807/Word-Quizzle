package Remote.Tests;

import Remote.Models.SignedUpUsersListModel;
import Remote.Models.UserModel;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;
import static org.testng.Assert.*;

public class RemoteTestNG {

    AtomicInteger atomicInteger = new AtomicInteger(0);
    SignedUpUsersListModel val = new SignedUpUsersListModel();

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
    public void SignedUpUsersListModelTest(){
        int a = atomicInteger.addAndGet(1);
        SignedUpUsersListModel.addResult add = val.add(new UserModel().setUser("f.pennino"+a).setPassword("prova1"));
        assertEquals(add, SignedUpUsersListModel.addResult.OKAY);
        add = val.add(new UserModel().setUser("f.pennino"+a).setPassword("prova1"));
        assertEquals(add, SignedUpUsersListModel.addResult.EXISTS);
        assertTrue(val.store());
    }
}