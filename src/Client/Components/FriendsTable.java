package Client.Components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FriendsTable extends JTable implements ActionListener {

    DefaultTableModel model;

    public FriendsTable() {
        super();
        model =  new DefaultTableModel(new Object[][] {},
                new Object[] { "Nome", "Punteggio" });
        setModel(model);
        Timer timer = new Timer(1500,this);
        timer.setRepeats(true);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("Ciao");
        //model.addRow(new Object[]{"prova","ciao"});
    }
}
