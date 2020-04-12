package Client.Runnable;

import Client.Components.Dashboard;
import Client.TCP.TCPClient;
import Request.ChallengeAnswer;
import Settings.Constants;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WaitForChallengeRunnable implements Runnable {
    /**
     * Variabili d'istanza
     */
    private DatagramSocket s;
    private boolean stop = false;
    private Dashboard frame;
    private TCPClient client;
    private boolean timer = false;

    /**
     * Il costruttore
     * @param s socket su cui aspetto la risposta
     * @param ui La UI su cui sto lavorando
     */
    public WaitForChallengeRunnable(DatagramSocket s, Dashboard ui){
        frame = ui;
        this.s = s;
    }

    /**
     * Esegue la sfida vera e propria
     */
    @Override
    public void run() {
        // Fino a che non c'è uno stop
        while(!stop){
            // Imposto il pacchetto da ricevere
            byte[] buffer = new byte[512];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            try {
                s.receive(response);
                String user =  new String(buffer, 0, response.getLength());
                // Ottengo dall'utente una risposta
                int n = showOptionDialog(user);
                // Rispondo al server
                n = client.sendChallengeAnswer(new ChallengeAnswer(user,n));
                frame.setVisible(false);
                switch (n){
                    // Se il timer è scaduto
                    case 0:
                        JOptionPane.showMessageDialog(frame,
                                "Hai impiegato troppo tempo a rispondere",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                    // Se il tuo rifiuto è stato inviato
                    case 1:
                        JOptionPane.showMessageDialog(frame,
                                "Il tuo rifiuto è stato inviato",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                        // Se hai accettato
                    default:
                        InetSocketAddress hostAddress = new InetSocketAddress("localhost", n);
                        SocketChannel clientS = SocketChannel.open(hostAddress);

                        // Rispondo al server
                        sendPresentationMessage(clientS);
                        // Ottengo le parole
                        String[] values = receiveWords(clientS);
                        try{
                            // Controllo se il timer è finito
                            checkTimerIsEnd(clientS,values);
                        } catch (NullPointerException e){
                            System.out.println("Errore nell'interfaccia");
                        }
                        break;
                }
                frame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Ferma l'esecuzione
     */
    public void stop(){
        // Fermo l'esecuzione
        stop = false;
    }

    /**
     * Permette all'utente di decidere se accettare o meno la sfida
     * @param user Utente che ha sfidato
     * @return risposta
     */
    private int showOptionDialog(String user){
        //Opzioni di scelta
        Object[] options = {"Si",
                "No"};
        // Dialog di scelta
        return JOptionPane.showOptionDialog(frame,
                "Accetti la sfida di "+ user +" ?",
                "Richiesta di sfida",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
    }

    /**
     * Ottiene dall'utente la parola tradotta
     * @param word Prossima parola da tradurre
     * @return traduzione
     */
    private byte[] nextWordToTranslate(String word){
        // Ottengo la traduzione dall'utente
        String translation = (String)JOptionPane.showInputDialog(
                null,
                "Traduci la seguente parola:\n"
                        + word,
                "Prossima parola da tradurre",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "ham");
        // La normalizzo
        return (translation == null ? "" : translation).getBytes();
    }

    /**
     * Invia un messaggio di presentazione al server
     * @param clientS socket su cui inviarlo
     * @throws IOException Se la comunicazione con il server salta
     */
    private void sendPresentationMessage(SocketChannel clientS) throws IOException {
        // Invio all'utente un messaggio con il nome dell'utente
        byte [] message = client.getUserModel().getUser().getBytes();
        ByteBuffer buff = ByteBuffer.wrap(message);
        clientS.write(buff);
        buff.clear();
    }

    /**
     * Ottengo dal server le parole
     * @param clientS socket su cui dialogare
     * @return Se il timer è scaduto restituisce i punteggi
     * @throws IOException Se la comunicazione con il server salta
     */
    private String[] receiveWords(SocketChannel clientS) throws IOException {
        String[] values = null;
        // Avvio il match
        for (int i = 0; i < Constants.NUMBER_OF_WORDS; i++) {
            ByteBuffer b = ByteBuffer.allocate(256);
            int read = clientS.read(b);
            // Se dal server ottengo una risposta di fine timer
            if(new String(b.array()).trim().contains("#")){
                timer = true;
                values = new String(b.array()).trim().split("#");
                break;
            } else {
                // Altrimenti chiedo una nuova parola e rispondo
                byte [] msg = nextWordToTranslate(new String(b.array()).trim());
                ByteBuffer bMsg = ByteBuffer.wrap(msg);
                clientS.write(bMsg);
            }
        }
        return values;
    }

    /**
     * Controlla se il timer è scaduto e in caso affermativo mostra il dialog finale, altrimenti mostra due dialog con i risultati
     * @param clientS socket su cui dialogare
     * @param values valori restituiti dal timer
     * @throws IOException Se la comunicazione con il server salta
     */
    private void checkTimerIsEnd(SocketChannel clientS, String[] values) throws IOException {
        if(!timer){
            ByteBuffer b = ByteBuffer.allocate(256);
            int read = clientS.read(b);
            // Se il timer è scaduto all'ultimo turno
            if(new String(b.array()).trim().contains("#")){
                values = new String(b.array()).trim().split("#");
                showDialog( "Il timer è scaduto il tuo punteggio è stato di : " + values[1] +" punti e il tuo avversario : " + values[2]);
            } else {
                // Altrimenti ottengo il mio punteggio
                String output = new String(b.array()).trim();
                showDialog("Hai totalizzato " + output +" punti");
                b.clear();
                // Ottengo il punteggio del mio avversario
                b = ByteBuffer.allocate(256);
                clientS.read(b);
                output = new String(b.array()).trim();
                showDialog("Il tuo avversario ha totalizzato " + output +" punti");
                clientS.close();
            }
        } else {
            // Se il timer era già scaduto
            showDialog("Il timer è scaduto il tuo punteggio è stato di : " + values[1] +" punti e il tuo avversario : " + values[2]);
        }
    }

    /**
     * Mostro un dialog con un messaggio
     * @param message il messaggio da mostrare
     */
    private void showDialog(String message){
        JOptionPane.showMessageDialog(null,message);
    }

    /**
     * Imposto il client su cui dialogare
     * @param client il client con cui scambiare messaggi
     */
    public void setClient(TCPClient client) {
        this.client = client;
    }
}
