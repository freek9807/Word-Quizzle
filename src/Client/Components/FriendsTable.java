package Client.Components;
import Client.Components.Model.RankTableModel;
import Client.TCP.TCPClient;
import Settings.Constants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
/**
 * Questa classe implementa l'interfaccia della tabella laterale
 *
 * @author Federico Pennino
 */
public class FriendsTable extends JTable implements ActionListener {
    /**
     * Il client a cui inviare le richieste per aggiornare la Tabella
     */
    private TCPClient client;
    /**
     *  Il timer che gestisce l'operazione di aggiornamento
     */
    private Timer timer;
    /**
     * Il costruttore
     * @param client Il client TCP a cui richiedere l'operazione di aggiornamento
     */
    public FriendsTable(TCPClient client) {
        super();
        // Conservo il client
        this.client = client;
        // Ottengo per la prima volta i valori con un'azione vuota
        actionPerformed(null);
        // Imposto il timer per tutte le volte successivo
        timer = new Timer(Constants.UPDATE_FRIEND_TABLE,this);
        timer.setRepeats(true);
        timer.start();
    }
    /**
     * Metodo che ferma il timer
     */
    public void stopTimer(){
        timer.stop();
    }
    /**
     * Esegue l'azione del Timer
     * @param actionEvent L'evento e chi lo ha generato
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            // Imposto i nuovi valori nella tabella
            RankTableModel model = new RankTableModel(client.userRank());
            setModel(model);
        } catch (IOException e) {
            System.out.println("Errore nella lista classifica");
        }
    }
}
