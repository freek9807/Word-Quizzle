package Models;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Objects;
/**
 * Descrive la condizione di un giocatore durante la partita
 *
 * @author Federico Pennino
 */
public class MatchClientSituation {
    /**
     * Variabili d'istanza
     */
    private String name;
    private Integer points = 0;
    private Integer num_of_connection = 0;
    private SocketChannel socket;
    private ArrayList<Integer> single_points = new ArrayList<>();
    /**
     * Il costruttore
     * @param socket il socket su cui si svolge la sfida
     */
    public MatchClientSituation(SocketChannel socket) {
        this.socket = socket;
    }
    /**
     * Registra una nuova connessione
     * @return il numero di connessioni
     */
    public Integer addConnection(){
        return ++num_of_connection;
    }
    /**
     * Il nome dell'utente
     * @param name il nome dell'utente
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Registra il punteggio dell'utente
     * @param points il numero di punti da registrare
     * @return il numero di punti dell'utente
     */
    public Integer addPoints(Integer points){
        single_points.add(points);
        this.points += points;
        return this.points;
    }
    /**
     * Restituisce i punti fatti per turno
     * @return la lista dei punti per turno
     */
    public ArrayList<Integer> getSingle_points() {
        return single_points;
    }
    /**
     * Restituisce il nome dell'utente
     * @return nome dell'utente
     */
    public String getName() {
        return name;
    }
    /**
     * Restituisce il numero di punti fatti dall'utente
     * @return il numero di punti fatti dall'utente
     */
    public Integer getPoints() {
        return points;
    }
    /**
     * Restituisce il numero di connessioni fatte dall'utente
     * @return il numero di connessioni del client
     */
    public Integer getNum_of_connection() {
        return num_of_connection;
    }
    /**
     * Il socket su cui l'utente scambia informazioni
     * @return il socket
     */
    public SocketChannel getSocket() {
        return socket;
    }
    /**
     * Se due MatchClientSituation sono uguali
     * @param o l'oggetto da confrontare
     * @return se è uguale o meno
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchClientSituation that = (MatchClientSituation) o;
        return Objects.equals(socket, that.socket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket);
    }
}
