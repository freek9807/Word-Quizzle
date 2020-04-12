package Main;

import Remote.C_RMI_API_Client;
import Models.SignedUpUsersListModel;
import Server.TCPServer;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
/**
 * @author Federico Pennino
 *
 * Questo è il main relativo al Server
 */
public class ServerMain {
    /**
     *  Questa classe astrae il file json contenente gli utenti registrati
     */
    private static SignedUpUsersListModel usersListModel = new SignedUpUsersListModel();
    /**
     *  Questa classe avvia il server
     *
     * @param args gli argomenti passati dalla linea di comando, in questo caso sono superflui
     */
    public static void main(String[] args) {
        System.out.println("Server Avviato");
        try{
            // Attivo la parte RMI del server
            C_RMI_API_Client server = new C_RMI_API_Client(usersListModel);
            // Interfaccia remota
            Registry registry = LocateRegistry.createRegistry(5099);
            registry.rebind("SignUp",server);
            // Attivo la parte TCP del server
            new TCPServer(usersListModel);
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
