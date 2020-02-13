package Remote.Tests;

import Remote.Models.SignedUpUsersListModel;
import Remote.Models.UserModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RemoteTest {

    @Test
    public void UserModelTest(){
        UserModel um = new UserModel().setUser("f.pennino").setPassword("prova1");
        assertEquals(um.toString(),"UserModel{" +
                "user='" + "f.pennino" + '\'' +
                ", password='" + "prova1" + '\'' +
                '}');
    }

    @Test
    public void SignedUpUsersListModelTest(){
        SignedUpUsersListModel val = new SignedUpUsersListModel();
        val.restore();
        SignedUpUsersListModel.addResult add = val.add(new UserModel().setUser("f.pennino").setPassword("prova1"));
        assertEquals(add, SignedUpUsersListModel.addResult.OKAY);
        add = val.add(new UserModel().setUser("f.pennino").setPassword("prova1"));
        assertEquals(add, SignedUpUsersListModel.addResult.EXISTS);
        assertTrue(val.store());
        SignedUpUsersListModel val1 = new SignedUpUsersListModel();
        System.out.println(val.size() + " "+val1.size());
        assertEquals(val.size(),val.size());
    }
}