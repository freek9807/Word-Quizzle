import Remote.C_RMI_API_Client;
import Models.SignedUpUsersListModel;
import TCPServer.TCPServer;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
// Questo è il main relativo al Server
public class ServerMain {

    public static SignedUpUsersListModel usersListModel = new SignedUpUsersListModel();
    public static Registry registry;

    public static void main(String[] args) {
        System.out.println("Server Avviato");
        try{
            // Attivo la parte RMI del server
            C_RMI_API_Client server = new C_RMI_API_Client(usersListModel);
            registry = LocateRegistry.createRegistry(5099);
            registry.rebind("SignUp",server);
            // Attivo la parte TCP del server
            new TCPServer(usersListModel);
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
