package Client.Runnable;

import Client.Components.ProgressDialog;

import javax.swing.*;
import java.util.List;

/**
 * Questa classe serve a mostrare una barra di attesa senza bloccare il Main Thread
 *
 * @author Federico Pennino
 */
public class ProgressBarSwingWorker extends SwingWorker<Void, Integer> {
    /**
     * Dialog da mostrare
     */
    ProgressDialog log;
    /**
     * Il costruttore
     * @param log la barra da mostrare
     */
    public ProgressBarSwingWorker(ProgressDialog log) {
        this.log = log;
    }
    /**
     * Aggiornamento in background della UI
     * @return null
     */
    @Override
    public Void doInBackground() {
        for (int i = 0; i < 7; i++) {
            try {
                Thread.sleep(1000);
                publish(i);
            } catch (InterruptedException e) {
                System.out.println("Ho ricevuto il messaggio");
            }
        }
        return null;
    }
    /**
     * nasconde la Dialog
     */
    @Override
    public void done() {
        log.setVisible(false);
        log.dispose();
    }
    /**
     * Durante l'esecuzione aggiorno la UI con i nuovi valori
     * @param ints Nuovo valore del Timer
     */
    @Override
    protected void process(List<Integer> ints) {
        log.setProgress(ints.get(0));
    }

}
