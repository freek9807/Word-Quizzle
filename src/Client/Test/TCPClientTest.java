package Client.Test;

import Client.TCP.TCPClient;
import Models.UserModel;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.*;
/**
 * Classe per il testing del Client TCP
 *
 * @author Federico Pennino
 */
public class TCPClientTest {
    /**
     *  Valore che aumenta ad ogni Thread, in modo da testare sempre client diversi
     */
    AtomicInteger atomicInteger = new AtomicInteger(0);
    /**
     * Test del client TCP riguardo la login
     */
    @Test(threadPoolSize = 3, invocationCount = 9,  timeOut = 10000)
    public void TCPConnection(){
        int a = atomicInteger.addAndGet(1);
        try {
            // Testo la login TCP
            TCPClient client = new TCPClient(new UserModel().setUser("f.p"+a).setPassword("prova1"));
            assertTrue(client.getResult());
            client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}