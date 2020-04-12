package Server.Runnable;

import Models.ClientConnectionInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Si mantiene in attesa di una risposta da parte del client riguardo alla sfida
 *
 * @author Federico Pennino
 */
public class ChallengeMsgWaiterRunnable implements Runnable {
    /**
     * Il socket su cui aspetto la sfida
     */
    private final DatagramSocket datagramSocket;
    /**
     * Se si è chiusa la partita o meno
     */
    private boolean stop = false;
    /**
     * Gli utenti connessi
     */
    private final ConcurrentHashMap<String,ClientConnectionInfo> users;
    /**
     * Il costruttore
     * @param datagramSocket Il socket su cui aspettare la partita
     * @param users la lista degli utenti connessi
     */
    public ChallengeMsgWaiterRunnable(DatagramSocket datagramSocket, ConcurrentHashMap<String, ClientConnectionInfo> users){
        this.datagramSocket = datagramSocket;
        this.users = users;
    }
    /**
     * Aspetto un nuovo messaggio
     */
    @Override
    public void run() {
        while (!stop){
            //Preparazione delle informazioni da ricevere
            byte[] buf = new byte[1024];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            try {
                datagramSocket.receive(recv);
                //Messaggio ricevuto dal Client
                String messaggio = (new String(recv.getData()).trim());
                String[] request = messaggio.split("#");
                // Se l'utente è valido
                if(users.containsKey(request[1])){
                    // Se non sta aspettando o è impegnato
                    if(!users.get(request[1]).isWaiting() && !users.get(request[1]).isBusy()){
                        // Invio un nuovo messaggio di sfida
                        byte[] msg = request[0].getBytes();
                        InetAddress addr = InetAddress.getByName("localhost");
                        DatagramPacket packet = new DatagramPacket(msg,msg.length,addr,users.get(request[1]).getUDPPort());
                        users.get(request[0]).setWaiting(true);
                        datagramSocket.send(packet);
                    } else {
                        sendNegativeResponse(request,3);
                    }
                } else {
                    sendNegativeResponse(request,2);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        datagramSocket.close();
    }
    /**
     * Smetto di ascoltare
     */
    public void stop(){
        stop = true;
    }
    /**
     * Se la risposta è negativo
     * @param request contiene chi ha inviato la richiesta e chi l'ha ricevuta
     * @param code il tipo di errore
     * @throws IOException Se ci sono problemi di connessione con il server
     */
    public void sendNegativeResponse(String[] request,int code) throws IOException {
        final ByteBuffer res = ByteBuffer.allocate(4);
        res.putInt(code);
        byte[] value = res.array();
        InetAddress addr = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(value,
                value.length,
                addr,
                users.get(request[0]).getUDPPortAnswers());
        datagramSocket.send(packet);
        System.out.println("L'utente "+ request[1] + " invitato da "+ request[0] +" non è connesso o non disponibile");
    }
}
