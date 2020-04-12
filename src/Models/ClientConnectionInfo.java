package Models;

import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
/**
 * Classe che descrive la connessione di un utente, contiene i socket e gli stream mantenuti con il server
 *
 * @author Federico Pennino
 */
public class ClientConnectionInfo {
    /**
     * Variabili d'istanza
     */
    private boolean isWaiting = false;
    private boolean isBusy = false;
    private Integer UDPPort;
    private ObjectInputStream ois;
    private DataOutputStream dos;
    private Integer UDPPortAnswers;
    private Socket socket;
    /**
     * restituisce la porta UDP
     * @return porta UDP
     */
    public Integer getUDPPort() {
        return UDPPort;
    }
    /**
     * Setta la porta UDP
     * @param UDPPort porta UDP a cui connettersi
     */
    public void setUDPPort(Integer UDPPort) {
        this.UDPPort = UDPPort;
    }
    /**
     * Stream di input
     * @return Stream di input
     */
    public ObjectInputStream getObjectInputStream() {
        return ois;
    }
    /**
     * Stream di input
     * @param ois stream di input
     */
    public void setObjectInputStream(ObjectInputStream ois) {
        this.ois = ois;
    }
    /**
     * Stream di output
     * @return stream di output
     */
    public DataOutputStream getDataOutputStream() {
        return dos;
    }
    /**
     * Stream di output
     * @param dos stream di output
     */
    public void setDataOutputStream(DataOutputStream dos) {
        this.dos = dos;
    }
    /**
     * Socket connessione TCP
     * @return socket connessione TCP
     */
    public Socket getSocket() {
        return socket;
    }
    /**
     * Socket di connessione TCP
     * @param socket socket connessione TCP
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    /**
     * Se l'utente sta aspettando una risposta dal server
     * @return lo stato di attesa dell'utente
     */
    public boolean isWaiting() {
        return isWaiting;
    }
    /**
     * Se l'utente sta aspettando una risposta dal server
     * @param waiting lo stato di attesa dell'utente
     */
    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }
    /**
     * Se l'utente è occupato
     * @return lo stato d'impegno dell'utente
     */
    public boolean isBusy() {
        return isBusy;
    }
    /**
     * Se l'utente è impegnato
     * @param busy lo stato dell'utente
     */
    public void setBusy(boolean busy) {
        isBusy = busy;
    }
    /**
     *  La porta UDP su cui l'utente aspetta la risposta
     * @return porta UDP
     */
    public Integer getUDPPortAnswers() {
        return UDPPortAnswers;
    }
    /**
     * La porta UDP su cui l'utente aspetta la risposta
     * @param UDPPortAnswers porta UDP
     */
    public void setUDPPortAnswers(Integer UDPPortAnswers) {
        this.UDPPortAnswers = UDPPortAnswers;
    }
}
