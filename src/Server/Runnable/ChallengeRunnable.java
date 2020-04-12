package Server.Runnable;

import Models.ClientConnectionInfo;
import Models.MatchClientSituation;
import Models.SignedUpUsersListModel;
import Models.Words;
import Server.Runnable.TimerTask.ChallengeTimer;
import Settings.Constants;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Il runnable che gestisce la vera e propria sfida tra due utenti
 *
 * @author Federico Pennino
 */
public class ChallengeRunnable implements Runnable {
    /**
     * Variabili d'istanza
     */
    private ServerSocketChannel socketChannel;
    private ConcurrentHashMap<String,ClientConnectionInfo> users;
    private AtomicBoolean timeout = new AtomicBoolean(false);
    private MatchClientSituation[] clients = new MatchClientSituation[2];
    private ArrayList<Future<Words>> words;
    private String user1;
    private String user2;
    private SignedUpUsersListModel suul;
    private Timer timer;
    /**
     * Il costruttore
     * @param socketChannel il socket su cui accettare i due utenti
     * @param users la lista con le informazioni di connessione di tutti gli utenti
     * @param cl nome utente 1
     * @param ci nome utente 2
     * @param suul Il descrittore di tutti gli utenti registrati
     */
    public ChallengeRunnable(ServerSocketChannel socketChannel, ConcurrentHashMap<String, ClientConnectionInfo> users, String cl, String ci, SignedUpUsersListModel suul) {
        this.socketChannel = socketChannel;
        this.timeout.set(false);
        this.users = users;
        words = new ArrayList<>();
        this.user1 = cl;
        this.user2 = ci;
        this.suul = suul;
    }
    /**
     * Il metodo che viene eseguito
     */
    @Override
    public void run() {
        // Avvio un ThreadPool su cui verranno caricate le traduzioni delle parole
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Constants.NUMBER_OF_WORDS);
        try {
            ArrayList<String> ls = getItalianWords();
            // Mando in background la traduzione delle parole in italiano
            for(int i = 0; i < Constants.NUMBER_OF_WORDS; i++)
                words.add(executor.submit(new GetTranslationRunnable(ls.get(i))));
            // Avvio il selettore e la partita
            Selector selector = Selector.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            setTimer(selector);
            System.out.println("\t La sfida ha inizio tra " + user1 + " e " + user2 + " ha inizio");
            while (!timeout.get()){
                // Aspetto nuove connessioni
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                    // Loop delle chiavi
                    while (iter.hasNext()) {

                        SelectionKey key = iter.next();

                        if (key.isAcceptable()) {
                            SocketChannel client = socketChannel.accept();
                            handleIsAcceptable(client,selector);
                        }

                        if(key.isWritable()) {
                            SocketChannel client = (SocketChannel) key.channel();
                            // Invio le nuove parole
                            handleIsWritable(client,selector);
                            // Controllo se gli utenti hanno risposto a tutte le parole
                            checkEndOfMatch();
                        }

                        if (key.isReadable()) {
                            SocketChannel client = (SocketChannel) key.channel();
                            handleIsReadable(selector,key,client);
                        }

                        iter.remove();
                    }
            }
            System.out.println("\t La sfida tra " + user1 + " e " + user2 + " è finita con i seguenti risultati :");
            // Invio agli utenti i messaggi di chiusura della partita
            sendUserEndOfMatch(selector);
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * Ottengo la lista delle parole tra cui scegliere
     * @return la lista delle parole
     * @throws IOException se ci sono problemi a contattare il server
     */
    private ArrayList<String> getItalianWords() throws IOException {

        File file = new File(Constants.WORDS_FILE_NAME);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<String> ls = new ArrayList<>();
        String line;

        while((line=bufferedReader.readLine())!=null) {
            ls.add(line.trim());
        }
        // Mischio le parole
        Collections.shuffle(ls);

        return ls;
    }
    /**
     * Imposta il timer della partita
     * @param selector il selettore della partita
     */
    private void setTimer(Selector selector){
        timer = new Timer();
        timer.schedule(new ChallengeTimer(selector,timeout),Constants.TIMER_TIME);
    }
    /**
     * Gestisce quando una chiave è leggibile, legge il messaggio e lo valuto
     * @param selector il selettore della partita
     * @param key la chiave che si sta valutando
     * @param client il socket su cui si scambiano i messaggi
     * @throws IOException Se ci sono problemi di comunicazione
     * @throws ExecutionException Se c'è un problema di esecuzione
     * @throws InterruptedException Se viene interrotta l'esecuzione
     */
    private void handleIsReadable(Selector selector, SelectionKey key, SocketChannel client) throws IOException, ExecutionException, InterruptedException {
        // Ottengo l'indice dell'utente corrente
        int current = clients[0].getSocket().equals(client) ? 0 : 1;

        try{
            ByteBuffer buffer = ByteBuffer.allocate(256);
            client.read(buffer);
            // Ottengo la parola da valutare
            String output = new String(buffer.array()).trim();
            // Valuto la parola
            HandlerClientConnection(output,client,key,selector,current);

            buffer.flip();
            buffer.clear();
        } catch (IOException e){
            System.out.println(clients[current].getName() + "Ha chiuso la connessione anticipatamente");
            client.close();
        }
    }
    /**
     * Se la chiave è accettabile, salvo il socket che descrive la connessione
     * @param client il socket su cui scambiare i messaggi
     * @param selector il selettore che descrive la partita
     * @throws IOException Se ci sono problemi di comunicazione
     */
    private void handleIsAcceptable(SocketChannel client, Selector selector) throws IOException {
        // Assegno il nuovo client che si sta connettendo
        if(clients[0] == null){
            clients[0] = new MatchClientSituation(client);
        } else if(!clients[0].getSocket().equals(client) && clients[1] == null) {
            clients[1] = new MatchClientSituation(client);
        }
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }
    /**
     * Se la chiave è scrivibile, invio una nuova parola
     * @param client il socket su cui vengono scambiati i messaggi
     * @param selector il selettore della partita
     * @throws IOException Se c'è un problema nella comunicazione
     * @throws ExecutionException Se c'è un problema di esecuzione
     * @throws InterruptedException Se c'è un problema d'interruzione
     */
    private void handleIsWritable(SocketChannel client, Selector selector) throws IOException, ExecutionException, InterruptedException {
        // Guardo chi è scrivibile
        int current = clients[0].getSocket().equals(client) ? 0 : 1;
        // Ottengo quante connessioni ha già eseguito
        int actual = clients[current].getNum_of_connection();
    try {
        // Aggiungo una nuova connessione
        clients[current].addConnection();
        // Se l'utente ha già raggiunto il numero di parole massimo invio il punteggio
        String word = (actual == Constants.NUMBER_OF_WORDS) ? "" + clients[current].getPoints() : words.get(actual).get().getIt();
        // Invio il messaggio
        byte [] message = word.getBytes();
        ByteBuffer buff = ByteBuffer.wrap(message);
        client.write(buff);
        client.register(selector, SelectionKey.OP_READ);
        buff.clear();

    } catch (IOException e){
        System.out.println(clients[current].getName() + " ha chiuso la connessione anticipatamente");
        client.close();
    }
    }
    /**
     * Gestisce il tipo di messaggio ricevuto dal client
     * @param output La parola inviata dal client
     * @param client Il socket su cui avviene lo scambio di parole
     * @param key la chiave che si sta valutando
     * @param selector il selettore su cui avviene la partita
     * @param i L'utente che ha inviato il messaggio
     * @throws IOException Se ci sono problemi di connessione
     * @throws ExecutionException Se ci sono problemi di esecuzione
     * @throws InterruptedException Se la connessione viene interrotta
     */
    private void HandlerClientConnection(String output, SocketChannel client, SelectionKey key, Selector selector, int i) throws IOException, ExecutionException, InterruptedException {
        // Se è la prima volta che l'utente si connette
        // Allora salvo il nome
        if (clients[i].getNum_of_connection() == 0) {
            clients[i].setName(output);
            client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            key.interestOps(SelectionKey.OP_WRITE);
        } else {
            // In caso contrario allora valuto la traduzione
            int actual = clients[i].getNum_of_connection() - 1;
            String it_word = words.get(actual).get().getIt();
            String en_word = words.get(actual).get().getEn();
            // Tolgo le maiuscole in maniera da valutare le parole in forma univoca
            boolean res = en_word.toLowerCase().equals(output.toLowerCase());

            System.out.println("\t\tLa risposta a " + it_word + " di " + clients[i].getName() + " ( " + output + " ) è " + (res ? "giusta" : "sbagliata. La giusta : " + en_word));
            // Aggiungo il punteggio
            if (res) {
                clients[i].addPoints(Constants.IS_WORD_CORRECT);
            } else {
                clients[i].addPoints(Constants.IS_WORD_INCORRECT);
            }

            client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }
    /**
     * Invio agli utenti i messaggi di chiusura della partita
     * @param selector il selettore su cui avvengono le comunicazioni
     * @throws IOException Se ci sono problemi di comunicazione
     */
    private void sendUserEndOfMatch(Selector selector) throws IOException {
        for (int i = 0;i < 2;i++){
            try {
                clients[i].getSocket().register(selector,SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                // Ottengo le informazioni sull'avversario
                int other = i == 0 ? 1 : 0;
                String msg ="" + clients[other].getPoints();
                // Se il timer è scaduto
                if(timeout.get()){
                    // E l'utente non ha finito
                    if(hasUserNotFinished(i)){
                        msg = "t#" + clients[i].getPoints() + "#" + clients[other].getPoints();
                    }
                }
                System.out.println("\t - " + clients[i].getName() + " ha totalizzato " + clients[i].getPoints() + " punti");
                // Altrimenti invio il punteggio dell'avversario
                byte [] message = (msg).getBytes();
                ByteBuffer buff = ByteBuffer.wrap(message);
                clients[i].getSocket().write(buff);
                clients[i].getSocket().close();
                users.get(clients[i].getName()).setBusy(false);
                buff.flip();
                buff.clear();
            } catch (ClosedChannelException e){
                System.out.println("\t - Non è stato possibile comunicare con " + clients[i].getName() + " per inviare il suo punteggio di "+ clients[i].getPoints() + " punti");
            }
            suul.addPoints(clients[i].getPoints(),clients[i].getName());
        }
    }
    /**
     * Controlla se entrambi i giocatori hanno inviato tutte le parole
     */
    private void checkEndOfMatch(){
        if(!(clients[0] == null || clients[1] == null)){
            if(isMatchEnd()) {
                timeout.set(true);
                timer.cancel();
            }
        }
    }
    /**
     * Condizione per cui il match si definisce finito
     * @return se il match è finito o no
     */
    private boolean isMatchEnd(){
        return clients[0].getNum_of_connection() + clients[1].getNum_of_connection() == 2 + 2 * Constants.NUMBER_OF_WORDS;
    }
    /**
     * Se un singolo utente ha finito la partita
     * @param i quale utente sto controllando
     * @return se l'utente ha finito o meno
     */
    private boolean hasUserNotFinished(int i){
        return clients[i].getNum_of_connection() != 1 + Constants.NUMBER_OF_WORDS;
    }
}
