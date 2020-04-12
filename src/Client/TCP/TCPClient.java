package Client.TCP;

import Client.Components.ProgressDialog;
import Client.Runnable.ProgressBarSwingWorker;
import Client.Runnable.WaitForAnswersOrTimeOut;
import Client.Runnable.WaitForChallengeRunnable;
import Models.RankNode;
import Models.UserModel;
import Request.*;

import Settings.Constants;
import com.google.gson.Gson;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Descrive la connessione e le operazione mantenuto con il server
 *
 * @author Federico Pennino
 */
public class TCPClient {

    private boolean result;
    private DataInputStream dis;
    private ObjectOutputStream oos;
    private SocketChannel client;
    private UserModel userModel;
    private WaitForChallengeRunnable challengeWaiter;

    /**
     * Il costruttore
     * @param user l'utente che cerca di connettersi
     * @throws IOException Se ci sono problemi di connessione
     */
    public TCPClient(UserModel user)
            throws IOException {
        // Imposto tutto il necessario per la connessione
        this.userModel = user;
        InetSocketAddress hA = new InetSocketAddress("localhost", 5454);
        client = SocketChannel.open(hA);
        System.out.println("Invio la richiesta al server...");
        oos = new ObjectOutputStream(client.socket().getOutputStream());
        // Invio le credenziali al server
        oos.writeObject(user);
        dis = new DataInputStream(client.socket().getInputStream());
        // Ottengo una risposta
        result = dis.readBoolean();
    }
    /**
     * Restiusce l'utente connesso
     * @return l'utente connesso
     */
    public UserModel getUserModel() {
        return userModel;
    }
    /**
     * Chiudo la connessione con il server
     * @throws IOException Se ci sono problemi di connessione
     */
    public synchronized void closeConnection() throws IOException {
        // Chiudo la connessione al server
        dis.close();
        oos.close();
        client.close();
        challengeWaiter.stop();
        System.out.println("Ho chiuso la connessione al server");
    }
    /**
     * Invia la richiesta di aggiunta di un arco di amicizia
     * @param friend La richiesta contenente l'utente da aggiungere
     * @throws IOException Se ci sono problemi di connessione
     * @throws IllegalArgumentException Se l'utente non è valido
     */
    public synchronized void addFriend(AddFriend friend) throws IOException,IllegalArgumentException {
        oos.writeObject(friend);
        if(!dis.readBoolean())
            throw new IllegalArgumentException("Utente amico non registrato!");
    }
    /**
     * Ottiene dal server la lista degli amici
     * @param ls richiesta di lista utenti amici
     * @return restituisce la lista con gli utenti
     * @throws IOException Se ci sono problemi di connessione
     */
    public synchronized String listFriends(ListFriends ls) throws IOException {
        oos.writeObject(ls);
        int dim = dis.readInt();
        byte[] input = new byte[dim];
        dis.readFully(input);
        return new String(input, StandardCharsets.UTF_8);
    }
    /**
     * Restiuisce la lista gli amici in base ai loro punteggi
     * @return Lista ordinata degli amici in base ai punteggi
     * @throws IOException Se ci sono problemi di connessione
     */
    public synchronized RankNode[] userRank() throws IOException {
        UserFriendsRank ls = new UserFriendsRank();
        ls.setUser(userModel.getUser());
        oos.writeObject(ls);
        int dim = dis.readInt();
        byte[] input = new byte[dim];
        dis.readFully(input);
        String in = new String(input, StandardCharsets.UTF_8);
        return new Gson().fromJson(in, RankNode[].class);
    }
    /**
     * Restituisce il punteggio di un utente
     * @param us Richiesta contenente l'utente di cui si vuole sapere il punteggio
     * @return Il punteggio dell'utente
     * @throws IOException Se ci sono problemi di connessione
     */
    public synchronized Integer userScore(UserScore us) throws IOException {
        // Controllo se l'utente richiesto sia un amico
        ListFriends ls = new ListFriends();
        ls.setUser(userModel.getUser());
        String[] friends =new Gson().fromJson(listFriends(ls), String[].class);
        // Controllo che l'utente sia tra gli amici
        if(Arrays.asList(friends).contains(us.getUser())){
            oos.writeObject(us);
            return dis.readInt();
        }
        return null;
    }
    /**
     * Restituisce il risultato del login
     * @return il risultato del login
     */
    public synchronized Boolean getResult() {
        return result;
    }
    /**
     * Restituisce la port UDP su cui ricevere le richieste dal server
     * @return porta UDP per richieste server
     * @throws IOException Se ci sono problemi di connessione
     */
    public synchronized int getPortUDP() throws IOException {
        return dis.readInt();
    }
    /**
     * Invia la porta su cui aspetta le richieste di sfida il server
     * @param port porta su cui aspettare richiesta di sfida
     * @throws IOException Se ci sono problemi di connessione
     */
    public synchronized void sendUDPPort(int port) throws IOException {
        oos.writeInt(port);
    }
    /**
     * Invia una risposta a una sfida ricevuta al server
     * @param response risposta da inviare al server
     * @return restituisce la risposta ricevuta dal server
     * @throws IOException Se ci sono problemi di connessione
     */
    public synchronized int sendChallengeAnswer(ChallengeAnswer response) throws IOException {
        oos.writeObject(response);
        return dis.readInt();
    }
    /**
     * Attiva il server su cui aspettare una sfida dal server
     * @param challengeWaiter il Runnable su cui aspetta una risposta il client
     */
    public synchronized void setChallengeWaiter(WaitForChallengeRunnable challengeWaiter) {
        this.challengeWaiter = challengeWaiter;
        challengeWaiter.setClient(this);
        new Thread(challengeWaiter).start();
    }
    /**
     * Gestisce la sfida con un utente avversario
     *
     * @param name Utente da sfidare
     * @param datagramSocket Il socket su cui aspettare la risposta
     * @param s socket che invierà la sfida
     * @param port Porta del server UDP
     * @throws InterruptedException Se uno dei Thread viene interrotto
     * @throws IOException Se ci sono problemi di connessione
     */
    public synchronized void handleMatchAnswers(String name, DatagramSocket datagramSocket, DatagramSocket s, int port) throws InterruptedException, IOException {
        // Ottengo la lista degli amici
        ListFriends ls = new ListFriends();
        ls.setUser(userModel.getUser());
        String[] friends =new Gson().fromJson(listFriends(ls), String[].class);
        // Controllo che l'utente sia tra gli amici
        if(Arrays.asList(friends).contains(name)){
            name = userModel.getUser() + "#" + name ;
            // Imposto un dialog con un timer
            ProgressDialog log = new ProgressDialog();
            log.setLocationRelativeTo(null);
            log.setTitle("Aspettando per risposta");
            // Attivo un Executor che manda in background il dialog
            ExecutorService pool = Executors.newFixedThreadPool(2);
            ProgressBarSwingWorker sw1 = new ProgressBarSwingWorker(log);
            pool.submit(sw1);
            // Mi metto ad aspettare una richiesta
            WaitForAnswersOrTimeOut wait = new WaitForAnswersOrTimeOut(name,port);
            wait.setSw1(sw1);
            wait.setS(s);
            wait.setDatagramSocket(datagramSocket);
            Future<Integer> future = pool.submit(wait);
            // Mosto il timer
            log.setVisible(true);
            try {
                // Valuto la risposta del server
                responseFromServer(future);
            } catch (ExecutionException e) {
                errorDialog("Errore imprevisto");
            } finally {
                pool.shutdown();
            }
        } else {
            errorDialog("L'utente non è tra i tuoi amici");
        }
    }
    /**
     * Gestisco la risposta dal server
     * @param future la risposta ricevuta
     * @throws ExecutionException Se c'è stato un errore in esecuzione
     * @throws InterruptedException Se c'è stata una interruzione
     * @throws IOException Se ci sono problemi di connessione
     */
    private void responseFromServer(Future<Integer> future) throws ExecutionException, InterruptedException, IOException {
        switch (future.get()){
            // Se l'utente non ha risposto in tempo
            case 0:
                oos.writeObject(new StopWaitingForMatch());
                errorDialog("L'utente non ha risposto in tempo");
                break;
            // Se l'utente ha declinato l'invito
            case 1:
                oos.writeObject(new StopWaitingForMatch());
                errorDialog("L'utente ha rifiutato la sfida");
                break;
                // Se l'utente non è connesso
            case 2:
                oos.writeObject(new StopWaitingForMatch());
                errorDialog("L'utente non è connesso o non esiste");
                break;
            // Se non è disponibile
            case 3:
                oos.writeObject(new StopWaitingForMatch());
                errorDialog("L'utente non è disponibile");
                break;
            // Se l'utente ha accettanto
            default:
                InetSocketAddress hostAddress = new InetSocketAddress("localhost", future.get());
                SocketChannel client = SocketChannel.open(hostAddress);
                System.out.println("Client sending messages to server...");
                // Invio un messaggio di presentazione al server
                sendPresentationMessage(client);
                AtomicBoolean timer = new AtomicBoolean(false);
                // Inizia la sfida
                String[] values = sendTranslation(client,timer);
                // Controllo il timer
                if(!timer.get()){
                    getResultCloseConnection(client);
                } else {
                    showDialog("Il timer è scaduto il tuo punteggio è stato di : " + values[1] +" punti e il tuo avversario : " + values[2]);
                }
                break;
        }
    }
    /**
     * Invia un messaggio di presentazione al server
     * @param client il socket su cui inviare la presentazione
     * @throws IOException Se ci sono problemi di connessione
     */
    private void sendPresentationMessage(SocketChannel client) throws IOException {
        byte [] message = getUserModel().getUser().getBytes();
        ByteBuffer buff = ByteBuffer.wrap(message);
        client.write(buff);
        buff.clear();
    }
    /**
     * Gestisce lo scambio di parole con il server
     * @param client il socket su cui avviene lo scambio di messaggi
     * @param timer La referenza al timer, verrà settata a true se la sfida non finisce in tempo
     * @return restituisce se è scaduto il timer i punteggi ottenuti
     * @throws IOException Se ci sono problemi di connessione
     */
    private String[] sendTranslation(SocketChannel client, AtomicBoolean timer) throws IOException {
        String[] values = null;
        // Avvio la sfida
        for (int i = 0; i < Constants.NUMBER_OF_WORDS; i++) {
            ByteBuffer b = ByteBuffer.allocate(256);
            int read = client.read(b);
            // Se il messaggio indica la fine del timer contiene #
            if(new String(b.array()).trim().contains("#")){
                timer.set(true);
                values = new String(b.array()).trim().split("#");
                break;
            } else {
                // Invio una nuova traduzione e aspetto risposta
                byte [] msg = nextWordToTranslate(new String(b.array()).trim());
                ByteBuffer bMsg = ByteBuffer.wrap(msg);
                client.write(bMsg);
            }
        }
        return values;
    }
    /**
     * Gestisce le ultime risposte ricevute dal server
     * @param client client su cui avvengono gli scambi di messaggi
     * @throws IOException Se ci sono problemi di connessione
     */
    private void getResultCloseConnection(SocketChannel client) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(256);
        int read = client.read(b);
        // Se l'ultimo messaggio indica fine del timer
        if(new String(b.array()).trim().contains("#")){
            String[] values = new String(b.array()).trim().split("#");
            showDialog( "Il timer è scaduto il tuo punteggio è stato di : " + values[1] +" punti e il tuo avversario : " + values[2]);
        } else {
            // Mostro due dialog con i punteggi
            String output = new String(b.array()).trim();
            showDialog("Hai totalizzato " + output +" punti");
            b.clear();
            b = ByteBuffer.allocate(256);
            client.read(b);
            output = new String(b.array()).trim();
            showDialog("Il tuo avversario ha totalizzato " + output +" punti");
            client.close();
        }
    }
    /**
     * Mostra un dialog con un messaggio
     * @param message il messaggio da visualizzare
     */
    private void showDialog(String message){
        JOptionPane.showMessageDialog(null,message);
    }
    /**
     * Ottiene la traduzione di una parola da parte dell'utente
     * @param word la parola tradotta
     * @return lo stream di byte della parola
     */
    private byte[] nextWordToTranslate(String word){
        String translation = (String)JOptionPane.showInputDialog(
                null,
                "Traduci la seguente parola:\n"
                        + word,
                "Prossima parola da tradurre",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "ham");
        return (translation == null ? "" : translation).getBytes();
    }
    /**
     * Dialog di errore
     * @param msg il messaggio da visualizzare
     */
    private void errorDialog(String msg){
        JOptionPane.showMessageDialog(null,
                msg,
                "Errore",
                JOptionPane.WARNING_MESSAGE);

    }
    /**
     * Lo stream su cui scrive
     * @return lo stream su cui scrive
     */
    public ObjectOutputStream getOos() {
        return oos;
    }
}
