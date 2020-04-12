package Server.Runnable.TimerTask;

import java.nio.channels.Selector;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 *  Il timer di fine sfida
 *
 * @author Federico Pennino
 */
public class ChallengeTimer extends TimerTask {
    /**
     * Variabili d'istanza
      */
    private Selector select;
    private AtomicBoolean timer;
    /**
     * Il costruttore
     * @param select il selector della sfida
     * @param timeout il timer della partita
     */
    public ChallengeTimer(Selector select, AtomicBoolean timeout) {
        this.select = select;
        this.timer = timeout;
    }
    /**
     * Il metodo da eseguire alla fine del timer
     */
    @Override
    public void run() {
        // Imposto il timer a true
        timer.set(true);
        System.out.println("Il timer della sfida è scaduto");
        // libero il selector
        select.wakeup();
    }

}
