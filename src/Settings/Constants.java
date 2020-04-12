package Settings;

public class Constants {
    /**
     * La durata del socket di sfida UDP
     */
    public static final int SOCKET_TIME_OUT = 7000;
    /**
     * Tempo di aggiornamento della tabella degli amici
     */
    public static final int UPDATE_FRIEND_TABLE = 3000;
    /**
     * Timer della sfida
     */
    public static final int TIMER_TIME = 30000;
    /**
     * Numero di parole della partita
     */
    public static final int NUMBER_OF_WORDS = 3;
    /**
     * Punteggio parola corretta
     */
    public static final int IS_WORD_CORRECT = +3;
    /**
     * Punteggio parola sbagliata
     */
    public static final int IS_WORD_INCORRECT = -1;
    /**
     * File con le parole
     */
    public static final String WORDS_FILE_NAME = "words";
    /**
     * Il file contenente gli utenti registrati
     */
    public static final String SIGNED_UP_USER_FILE = "userList.json";
}
