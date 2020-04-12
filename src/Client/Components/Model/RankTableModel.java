package Client.Components.Model;

import Models.RankNode;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Questa classe descrive il modello della tabella laterale che associa utenti-punteggio
 *
 * @author Federico Pennino
 */
public class RankTableModel extends AbstractTableModel {
    /**
     * Valori contenuti nella tabella
     */
    ArrayList<RankNode> ls;
    /**
     * Nomi delle colonne
     */
    String[] colName = {"Nickname","Punteggio"};
    /**
     * Il costruttore
     * @param ls la lista dei ranking di tutti gli utenti amici
     */
    public RankTableModel(RankNode[] ls) {
        this.ls = new ArrayList<>(Arrays.asList(ls));
    }
    /**
     * Restituisco il nome della colonna
     * @param column Il numero della colonna
     * @return Il nome associato alla colonna
     */
    @Override
    public String getColumnName(int column) {
        return colName[column];
    }
    /**
     * Quale cella può essere editata , nessuna
     * @param rowIndex la posizione riga
     * @param columnIndex la posizione colonna
     * @return sempre false
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    /**
     * Il numero di righe
     * @return il numero di righe della tabella
     */
    @Override
    public int getRowCount() {
        return ls.size();
    }
    /**
     * Il numero di colonne
     * @return il numero di colonne della tabella
     */
    @Override
    public int getColumnCount() {
        return colName.length;
    }
    /**
     * Il valore di ogni cella
     * @param row La posizione lungo le righe
     * @param col La posizione lungo le colonne
     * @return Il valore nella data posizione
     */
    @Override
    public Object getValueAt(int row, int col) {
        // Ottengo la riga dalla lista
        RankNode node = ls.get(row);
        // Setto il valore alla colnna
        switch (col){
            case 0:
                return node.getNick();
            case 1:
                return node.getPoints();
        }
        // LA JVM mi obbliga :-)
        return null;
    }
}
