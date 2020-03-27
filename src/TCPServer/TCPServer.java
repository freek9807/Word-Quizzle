package TCPServer;

import Models.SignedUpUsersListModel;
import Models.UserModel;
import TCPServer.Runnable.LoggedInUserRunnable;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
// Questa classe gestisce le connessioni TCP al server
public class TCPServer {
    // Variabili d'istanza della classe :
    // il Server Socket
    ServerSocketChannel serverSocket;
    // Il set in cui vengono salvati i Thread attivi
    Set<Thread> activeConnection = new HashSet<>();
    // Il costruttore
    public TCPServer(SignedUpUsersListModel usersListModel) throws IOException, ClassNotFoundException {
        // Apro il canale del ServerSocket
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", 5454));
        
        while (true) {
            // Aspetto una connessione
            SocketChannel socket = serverSocket.accept();
            Socket sock = socket.socket();
            // Apro gli stream di Input e Output verso il socket
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            // Ottengo le informazioni di login dal socket
            UserModel clientUser = (UserModel) ois.readObject();
            // Guardo se le informazioni sono valide
            if(!isUserActive(clientUser) && usersListModel.isValid(clientUser)){
                System.out.println("Connessione accettata da : "+ clientUser.user);
                // Restituisco una risposta
                dos.writeBoolean(true);
                // Avvio il Thread che gestisce la connessione dell'utente
                LoggedInUserRunnable client = new LoggedInUserRunnable(sock, ois, dos,clientUser,usersListModel);
                Thread current = new Thread(client,clientUser.user);
                activeConnection.add(current);
                current.start();
            } else {
                // Rifiuto la connessione
                System.out.println("Connessione rifiutata da : " + clientUser.user);
                dos.writeBoolean(false);
            }
        }
    }
    // Guardo se è attivo un utente con le credenziali inserite
    private boolean isUserActive(UserModel clientUser){
        boolean stop = false;
        Iterator<Thread> it = activeConnection.iterator();
        // Guardo se tra i Thread attivi uno ha il nome dell'utente
        // che sta provando ad accedere
        // Se il Thread non è più attivo lo elimino
        while(it.hasNext()){
            Thread th = it.next();
            if(th.getName().equals(clientUser.user) && th.isAlive()) {
                stop = true;
            }  else if(!th.isAlive()){
                it.remove();
            }
        }
        // Restituisco se l'utente è attivo
        return stop;
    }


}
