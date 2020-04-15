package Server;

import Models.ClientConnectionInfo;
import Models.SignedUpUsersListModel;
import Models.UserModel;
import Server.Runnable.ChallengeMsgWaiterRunnable;
import Server.Runnable.LoggedInUserRunnable;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
/**
 * Questa classe gestisce le connessioni TCP al server
 *
 * @author Federico Pennino
 */
public class TCPServer {
    /**
     * Server Socket per le connessioni TCP
     */
    ServerSocket serverSocket;
    /**
     * Il pool dei thread in esecuzione
     */
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    /**
     * Contiene gli utenti che sono connessi e le porte UDP su cui aspettano le richieste
     */
    ConcurrentHashMap<String, ClientConnectionInfo> online = new ConcurrentHashMap<>();
    /**
     * Il costruttore
     * @param usersListModel la lista degli utenti registrati
     * @throws IOException Se ci sono problemi di connessione con il server
     * @throws ClassNotFoundException Se non riesce a recuperare l'oggetto dal socket
     */
    public TCPServer(SignedUpUsersListModel usersListModel) throws IOException, ClassNotFoundException {
        // Apro il canale del ServerSocket
        serverSocket = new ServerSocket(5454);
        // Creo un nuovo Datagram per la connessione UDP
        DatagramSocket serverSocketUDP = new DatagramSocket();
        // Ottengo la porta UDP su cui scrivere
        int port = serverSocketUDP.getLocalPort();
        // Costruisco il Runnable per la richiesta di sfida in UDP
        ChallengeMsgWaiterRunnable challengeRunnable = new ChallengeMsgWaiterRunnable(serverSocketUDP,online);
        executor.execute(challengeRunnable);
        // La invio al client
        while (true) {
            // Aspetto una connessione
            Socket sock = serverSocket.accept();
            // Apro gli stream di Input e Output verso il socket
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            // Ottengo le informazioni di login dal socket
            UserModel clientUser = (UserModel) ois.readObject();
            // Costruisco l'oggetto che descrive le informazioni sulla connessione col client
            ClientConnectionInfo clientInfo = new ClientConnectionInfo();
            // Guardo se le informazioni sono valide
            if(usersListModel.isValid(clientUser) && !isUserActive(clientUser,clientInfo)){
                System.out.println("Connessione accettata da : "+ clientUser.getUser());
               // Restituisco una risposta
                dos.writeBoolean(true);
                dos.writeInt(port);
                int clientUDPPort = ois.readInt();
                int socketAnswers = ois.readInt();
                clientInfo.setDataOutputStream(dos);
                clientInfo.setObjectInputStream(ois);
                clientInfo.setSocket(sock);
                clientInfo.setUDPPort(clientUDPPort);
                clientInfo.setUDPPortAnswers(socketAnswers);
                // Creo il runnable che gestisce la connessione dell'utente
                LoggedInUserRunnable client = new LoggedInUserRunnable(clientInfo);
                client.setClientUser(clientUser);
                client.setOnlineUsers(online);
                client.setSuul(usersListModel);
                // Avvio l'utente connesso
                executor.execute(client);
            } else {
                // Rifiuto la connessione
                System.out.println("Connessione rifiutata da : " + clientUser.getUser());
                dos.writeBoolean(false);
            }
        }
    }
    /**
     * Guardo se è attivo un utente con le credenziali inserite
     * @param clientUser l'utente di cui si desidera avere informazioni
     * @param clientInfo le informazioni non persistenti dell'utente
     * @return se è attivo o meno
     */
    private boolean isUserActive(UserModel clientUser, ClientConnectionInfo clientInfo){
        // Aggiungo l'utente alla lista dei connessi se non presente
        return online.putIfAbsent(clientUser.getUser(), clientInfo) != null;
    }
}
