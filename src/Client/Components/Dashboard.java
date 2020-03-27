package Client.Components;

import Client.TCP.TCPClient;
import Client.UI;
import Request.AddFriend;
import Request.ListFriends;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Dashboard extends JFrame implements ActionListener {
    private int dx;
    private int dy;
    private Logo logo;
    // Le variabili di istanza
    JButton jButton = new JButton("Aggiungi Amico");
    JButton jButton1 = new JButton("Sfida Amico");
    JButton jButton2 = new JButton("Classifica");
    JButton jButton3 = new JButton("Logout");
    // Mantengo i dati della connessione TCP con il server
    TCPClient client;
    // Il costruttore
    public Dashboard(TCPClient client)
    {
        this.client = client;
        // Dimensione della schermata
        setSize(600, 400);
        //setResizable(false);
        centerFrame();
        // Pannello generale
        JPanel pa = new JPanel();
        pa.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Pannello laterale con i bottoni
        JPanel jPanel = new JPanel(new GridLayout(1,1,10,10));
        jPanel.setBorder(new EmptyBorder(0,10,2,10));
        // Costruisco il pannello laterale
        JPanel jPanel2 = new JPanel(new GridLayout(2,1));
        JPanel jPanel3 = new JPanel(new GridLayout(1,1));
        // Questo è il pannello che conterrà i bottoni
        JPanel jPanel1 = new JPanel(new GridLayout(4,1,15,0));
        jPanel.add(jPanel1);
        // Imposto gli eventi sui bottoni
        setJButtonEvent();
        // Li aggiungo al pannello
        addButton(jPanel1);
        // set the layout
        pa.setLayout(new GridLayout(1,2));
        // Aggiungo il logo
        logo = new Logo(dx/2 , dy);
        jPanel3.add(logo);
        jPanel2.add(jPanel3);
        // Aggiungo il pannello
        jPanel2.add(jPanel1);
        // Aggiungo al pannello principale
        pa.add(jPanel2);
        pa.add(jPanel);

        // Initializing the JTable
        FriendsTable j = new FriendsTable();
        j.setBounds(30, 40, 200, 300);
        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(j);
        jPanel.add(sp);
        //set a selected index
        // Aggiungo al Frame
        setContentPane(pa);
        this.setResizable(false);
        // Chiusura di default
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Lo rendo visibile
        setVisible(true);
    }
    // Aggiungo un gestore di eventi ai bottoni (la classe stessa)
    private void setJButtonEvent(){
        jButton.addActionListener(this);
        jButton1.addActionListener(this);
        jButton2.addActionListener(this);
        jButton3.addActionListener(this);
    }
    // Aggiungo i bottoni al panel
    private void addButton(JPanel jPanel){
        jPanel.add(jButton);
        jPanel.add(jButton1);
        jPanel.add(jButton2);
        jPanel.add(jButton3);
    }
    // Gestisco gli eventi
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JButton jButton = (JButton) actionEvent.getSource();
        IfActionLogout(jButton);
    }
    // Nel caso in cui il bottone cliccato si il logout
    private void IfActionLogout(JButton button) {
        if(button.equals(jButton3)){
            try {
                // Chiudo la connessione
                client.closeConnection();
                showDialog("Login eseguito con successo","Logout",JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                showDialog(e.getMessage(),"Errore nella disconnessione",JOptionPane.ERROR_MESSAGE);
            } finally {
                // Chiudo la schermata
                this.setVisible(false);
                // Creo una nuova UI di Login
                new UI();
            }
        }

        if(button.equals(jButton)){
            String name = JOptionPane.showInputDialog(
                    this,
                    "Chi vuoi aggiungere come amico?"
            );
            AddFriend request = new AddFriend();
            request.setName(name);
            try {
                client.addFriend(request);
                showDialog("Hai un nuovo amico nella tua lista","Richiesta amicizia",JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | IllegalArgumentException e) {
                showDialog(e.getMessage(),"Errore nell'invio della richiesta",JOptionPane.ERROR_MESSAGE);
            }
        }

        if(button.equals(jButton1)){
            ListFriends ls = new ListFriends();
            ls.setUser(this.client.getUserModel().user);
            try {
                client.listFriends(ls);
            } catch (IOException e) {
                showDialog(e.getMessage(),"Errore nell'invio della richiesta",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Mostro il Dialog
    private void showDialog(String msg,String title, int type){
        JOptionPane.showMessageDialog(this,
                msg,
                title,
                type);
    }

    // Posiziono l'interfaccia a centro
    private void centerFrame() {

        Dimension windowSize = getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();

        this.dx = windowSize.width;
        this.dy = windowSize.height;
        int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2;
        setLocation(dx, dy);
    }
}
