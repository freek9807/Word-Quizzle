package Models;

import java.io.Serializable;
/**
 * Nodo della lista della classifica
 *
 * @author Federico Pennino
 */
public class RankNode implements Serializable,Comparable<RankNode> {
    /**
     * Le variabili d'istanza
     */
    private String nick;
    private Integer points;
    /**
     * Il costruttore
     * @param nick l'id dell'utente
     * @param points il numero di punti dell'utente
     */
    public RankNode(String nick, Integer points) {
        this.nick = nick;
        this.points = points;
    }
    /**
     * Restituisce il nome dell'utente
     * @return il nome dell'utente
     */
    public String getNick() {
        return nick;
    }
    /**
     * Restituisce il numero dei punti
     * @return il numero di punti dell'utente
     */
    public Integer getPoints() {
        return points;
    }
    /**
     * Il toString
     * @return la stringa formattata
     */
    @Override
    public String toString() {
        return "RankNode{" +
                "nick='" + nick + '\'' +
                ", points=" + points +
                '}';
    }
    /**
     * La funzione di compare
     * @param rankNode l'oggetto da confrontare
     * @return la precedenza tra i due oggetti
     */
    @Override
    public int compareTo(RankNode rankNode) {
        // Mi serve l'ordine ascendente per la visualizzazione corretta
        // In una tabella
        return -1 * this.getPoints().compareTo(rankNode.getPoints());
    }
}
