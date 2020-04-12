package Client.Runnable;
import Settings.Constants;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

public class WaitForAnswersOrTimeOut implements Callable<Integer> {
    /**
     * Varibili d'istanza
     */
    private String name;
    private int port;
    private DatagramSocket s;
    private DatagramSocket datagramSocket;
    private ProgressBarSwingWorker sw1;
    /**
     * Il costruttore
     * @param name Nome dello sfidante
     * @param port Porta del server
     */
    public WaitForAnswersOrTimeOut(String name, int port) {
        this.name = name;
        this.port = port;
    }

    /**
     * Imposto il serverSocket che invierà la sfida
     * @param s socket che invierà la sfida
     */
    public void setS(DatagramSocket s) {
        this.s = s;
    }

    /**
     * Imposto il socket su cui ricevere la risposta
     * @param datagramSocket Il socket su cui aspettare la risposta
     */
    public void setDatagramSocket(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }
    /**
     *  Imposto il Runnable che in background manda la dialog
     * @param sw1 Swinger Worker che esegue la Dialog
     */
    public void setSw1(ProgressBarSwingWorker sw1) {
        this.sw1 = sw1;
    }

    /**
     * Il Callable che aspetta una risposta
     * @return restituisce se il timer è scaduto o meno
     */
    @Override
    public Integer call() {
        // Imposto il buffer su cui ricevere la risposta
        byte[] buf = new byte[1024];
        // Imposto che il timer è scaduto
        int timeout = 0;
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        byte[] msg = name.getBytes();
        try {
            // Invio la richiesta
            InetAddress addr = InetAddress.getByName("localhost");
            DatagramPacket hi = new DatagramPacket(msg, msg.length, addr, port);
            s.send(hi);
            // Ottengo la risposta
            datagramSocket.setSoTimeout(Constants.SOCKET_TIME_OUT);
            datagramSocket.receive(recv);
            // Cancello il worker
            sw1.cancel(true);
            ByteBuffer buff = ByteBuffer.wrap(recv.getData());
            // Imposto che il timer non è scaduto e ho ottenuto una risposta
            timeout = buff.getInt();
        } catch (SocketTimeoutException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Chiudo la socket
                datagramSocket.setSoTimeout(0);
            } catch (SocketException ex) {
                ex.printStackTrace();
            }
        }
        return timeout;
    }
}
