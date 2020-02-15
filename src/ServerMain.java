import Remote.C_RMI_API_Client;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static Registry registry;
    public static void main(String[] args) {
        System.out.println("Server Avviato");
        try{
            C_RMI_API_Client server = new C_RMI_API_Client();
            registry = LocateRegistry.createRegistry(5099);
            registry.rebind("SignUp",server);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }
}
