package Server.Runnable;
import Models.ClientConnectionInfo;
import Models.SignedUpUsersListModel;
import Models.UserModel;
import Request.*;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Questa classe gestisce il Runnable della connessione Server-Client
 */
public class LoggedInUserRunnable implements Runnable {
    /**
     * Le variabili d'istanza
     */
    private ClientConnectionInfo info;
    /**
     * Le informazioni dell'utente che fa riferimento a questo Runnable
     */
    private UserModel clientUser;
    private SignedUpUsersListModel suul;
    private ConcurrentHashMap<String,ClientConnectionInfo> online;
    /**
     * Costruttore
     * @param info le informazioni relative alle varie connessioni
     */
    public LoggedInUserRunnable(ClientConnectionInfo info) {
        this.info = info;
    }
    /**
     * Imposta l'utente a cui corrisponde il Runnable
     * @param clientUser l'utente a cui corrisponde la connessione
     */
    public void setClientUser(UserModel clientUser) {
        this.clientUser = clientUser;
    }
    /**
     * La lista degli utenti registrati
     * @param suul La lista degli utenti registrati
     */
    public void setSuul(SignedUpUsersListModel suul) {
        this.suul = suul;
    }
    /**
     * La lista degli utenti disponibili
     * @param online La lista degli utenti disponibili
     */
    public void setOnlineUsers(ConcurrentHashMap<String, ClientConnectionInfo> online) {
        this.online = online;
    }
    /**
     * Il cuore del Runnable
     */
    @Override
    public void run() {
        try {
            while(true){
                // Aspetto che mi arrivi un nuovo Object dal client
                Object obj = info.getObjectInputStream().readObject();
                // Se l'utente mi invia una richiesta di amicizia
                if(obj instanceof AddFriend){
                    IfAddFriendRequest((AddFriend) obj);
                }
                // Se l'utente invia una richiesta per listare i suoi amici
                if(obj instanceof ListFriends){
                    IfListFriendsRequest((ListFriends) obj);
                }
                // Se l'utente invia una richiesta per la classifica degli amici
                if(obj instanceof UserFriendsRank){
                    IfUserFriendsRankRequest((UserFriendsRank) obj);
                }
                // Se l'utente riese il punteggio di un altro utente
                if(obj instanceof UserScore){
                    IfUserScoreRequest((UserScore) obj);
                }
                // Se l'utente ha risposto a una sfida
                if(obj instanceof ChallengeAnswer){
                    IfChallengeAnswer((ChallengeAnswer) obj);
                }
                // Se un utente ha smesso di aspettare risposta
                if(obj instanceof StopWaitingForMatch){
                    IfStopWaiting((StopWaitingForMatch) obj);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Se qualcosa va storto oppure l'utente si disconnette
            // provo a fermare la connessione al client
            try {
                closeConnection();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * Se è la richiesta di un nuovo amico
     * @param friend la richiesta dell'utente da aggiungere
     * @throws IOException se c'è stato un errore di connessione
     */
    public void IfAddFriendRequest(AddFriend friend) throws IOException{
        // Eseguo la richiesta sul file che descrive
        boolean res = suul.addFriendEdge(clientUser,new UserModel().setUser(friend.getUser()));
        // Stampo un messaggio
        System.out.println((res ? "Arco di amicizia aggiunto tra : " : "Non è stato possibile aggiungere un arco tra : ") + clientUser.getUser() +" - "+ friend.getUser());
        // Invio una risposta
        info.getDataOutputStream().writeBoolean(res);
    }
    /**
     * Se richiedo la lista di amici
     * @param ls la richiesta dell'utente di cui voglio la lista
     * @throws IOException se c'è stato un errore di connessione
     */
    public void IfListFriendsRequest(ListFriends ls) throws IOException{
        // Ottengo la lista di amici
        String friends = suul.retrieveFriends(ls);
        // Casto la risposta
        byte[] output = friends.getBytes(StandardCharsets.UTF_8);
        // Invio la dimensione della risposta
        info.getDataOutputStream().writeInt(output.length);
        // Invio la stringa
        info.getDataOutputStream().write(output);
    }
    /**
     * Se l'utente richieste la classifica
     * @param ls la richiesta dell'utente di cui voglio sapere la classifica
     * @throws IOException Se l'utente richieste la classifica
     */
    public void IfUserFriendsRankRequest(UserFriendsRank ls) throws IOException{
        // Ottengo il JSON ordinato
        String response = suul.friendsRank(ls);
        // Casto la risposta
        byte[] output = response.getBytes(StandardCharsets.UTF_8);
        // Invio la dimensione della risposta
        info.getDataOutputStream().writeInt(output.length);
        // Invio il JSON
        info.getDataOutputStream().write(output);
    }
    /**
     * Se l'utente vuole sapere il punteggio di un altro utente
     * @param us la richiesta dell'utente di cui voglio sapere il punteggio
     * @throws IOException Se l'utente richieste la classifica
     */
    public void IfUserScoreRequest(UserScore us) throws IOException {
        // Ottengo il punteggio
        Integer points = suul.userScore(us).getPoints();
        // Invio una risposta
        info.getDataOutputStream().writeInt(points);
    }
    /**
     * La richiesta del'utente che vuole smettere di aspettare
     * @param sw chi vuole smettere di aspettare
     */
    public void IfStopWaiting(StopWaitingForMatch sw){
        online.get(clientUser.getUser()).setWaiting(false);
    }
    /**
     * Se ottengo una risposta dal client per una sfida
     * @param challengeAnswer chi ha inviato la sfida
     * @throws IOException Se l'utente richieste la classifica
     */
    public void IfChallengeAnswer(ChallengeAnswer challengeAnswer) throws IOException {
        // Se l'utente sfidato sta aspettando
        if(online.get(challengeAnswer.getUser()).isWaiting()){
            DatagramSocket s = new DatagramSocket();
            final ByteBuffer buf = ByteBuffer.allocate(4);
            // Avvio un socket per la sfida
            ServerSocketChannel socketChannel=ServerSocketChannel.open();
            socketChannel.bind(new InetSocketAddress(0));
            // Se la risposta è positiva
            if(challengeAnswer.getResponse() == 0){
                online.get(clientUser.getUser()).setBusy(true);
                online.get(challengeAnswer.getUser()).setWaiting(false);
                online.get(challengeAnswer.getUser()).setBusy(true);
                buf.putInt(socketChannel.socket().getLocalPort());
                // Avvio la sfida
                new Thread(new ChallengeRunnable(socketChannel,online,clientUser.getUser(),challengeAnswer.getUser(),suul)).start();
            } else {
                // Chiudo il socket
                socketChannel.close();
                buf.putInt(challengeAnswer.getResponse());
            }
            // Invio la risposta alla sfida
            byte[] value = buf.array();
            InetAddress addr = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(value,
                    value.length,
                    addr,
                    online.get(challengeAnswer.getUser()).getUDPPortAnswers());
            s.send(packet);
            info.getDataOutputStream().writeInt(challengeAnswer.getResponse() == 0 ? socketChannel.socket().getLocalPort() : 1);
            s.close();
        } else {
            info.getDataOutputStream().writeInt(0);
        }
    }
    /**
     * Provo a chiudere la connessione
     * @throws IOException Se l'utente richieste la classifica
     */
    public void closeConnection() throws IOException {
        // Rimuovo l'utente dalla lista dei connessi
        online.remove(clientUser.getUser());
        // Chiudo stream e socket
        info.getDataOutputStream().close();
        info.getObjectInputStream().close();
        info.getSocket().close();
        // Stampo un messaggio
        System.out.println("Connessione chiusa da : " + clientUser.getUser());
    }
}
