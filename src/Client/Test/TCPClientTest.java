package Client.Test;

import Client.TCP.TCPClient;
import Models.UserModel;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.*;

public class TCPClientTest {

    AtomicInteger atomicInteger = new AtomicInteger(0);

    @Test(threadPoolSize = 3, invocationCount = 9,  timeOut = 10000)
    public void TCPConnection(){
        try {
            int a = atomicInteger.addAndGet(1);
            TCPClient client = new TCPClient(new UserModel().setUser("f.p"+a).setPassword("prova1"));
            assertTrue(client.getResult());
            client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}