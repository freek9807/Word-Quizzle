package Models;
/**
 *  Corrispondenza tra le parole italiane ed inglesi
 *
 * @author Federico Pennino
 */
public class Words {
    /**
     * Variabili d'istanza
     */
    private String it;
    private String en;
    /**
     * La parola italiana
     * @return la parola in italiano
     */
    public String getIt() {
        return it;
    }
    /**
     * La parola inglese
     * @return la parola in inglese
     */
    public String getEn() {
        return en;
    }
    /**
     * Il costruttore
     * @param it la parola italiana
     * @param en la parola inglese
     */
    public Words(String it, String en) {
        this.it = it;
        this.en = en;
    }
}
