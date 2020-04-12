package Client.Components;
import Settings.Constants;
import javax.swing.*;
/**
 * Un Dialog che al suo interno contiene una ProgressBar
 *
 * @author Federico Pennino
 */
 public class ProgressDialog extends JDialog {
    /**
     *  La progressBar da visualizzare
     */
    private JProgressBar bar;
    /**
     * Il costruttore
     */
    public ProgressDialog(){
        setModal(true);
        bar = new JProgressBar();
        bar.setMaximum(105);
        bar.setStringPainted(true);
        add(bar);
        pack();
    }
    /**
     * Imposta il progresso della barra
     * @param i i secondi che sono passati dall'attivazione del Thread
     */
    public void setProgress(int i){
        bar.setString("Mancano " + ((Constants.SOCKET_TIME_OUT / 1000) - 1 - i) + " s");
        bar.setValue(i * 17);
    }
    /**
     * Nasconde la barra
     */
    public void close(){
        bar.setVisible(false);
    }
}
