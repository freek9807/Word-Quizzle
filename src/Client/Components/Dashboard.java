package Client.Components;

import Client.TCP.TCPClient;
import Client.UI;
import Request.AddFriend;
import Request.ListFriends;
import Request.UserScore;
import com.google.gson.Gson;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramSocket;

public class Dashboard extends JFrame implements ActionListener {
    /**
     * Le coordinate della finestra
     */
    private int dx;
    private int dy;
    /**
     * Istanze della parte UDP della DashBoard
     */
    private int port;
    DatagramSocket datagramSocketAnswers;
    DatagramSocket s;
    /**
     * Variabili d'istanza dell'interfaccia
     */
    JButton jButton = new JButton("Aggiungi Amico");
    JButton jButton1 = new JButton("Sfida Amico");
    JButton jButton2 = new JButton("Lista amici");
    JButton jButton3 = new JButton("Punteggio utente");
    JButton jButton4 = new JButton("Logout");
    FriendsTable j;
    /**
     * Mantengo i dati della connessione TCP con il server
     */
    TCPClient client;

    /**
     * Il costruttore
     * @param client mantengo la connessione TCP ottenuta durante la fase di login
     * @param port Porta UDP del server
     * @param s Socket su cui invio la richiesta di sfida al server
     * @param datagramSocketAnswers Socket su cui attendo la risposta di sfida dal server
     */
    public Dashboard(TCPClient client, int port, DatagramSocket s,DatagramSocket datagramSocketAnswers)
    {
        // Recupero il client
        this.client = client;
        // Salvo la porta
        this.port = port;
        // Salvo il Socket UDP
        this.s = s;
        this.datagramSocketAnswers = datagramSocketAnswers;
        // Dimensione della schermata
        setSize(600, 400);
        //setResizable(false);
        centerFrame();
        // Pannello generale
        JPanel pa = new JPanel();
        pa.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Pannello laterale con i bottoni
        JPanel jPanel = new JPanel(new GridLayout(1,1,10,10));
        jPanel.setBorder(new EmptyBorder(0,10,0,10));
        // Costruisco il pannello laterale
        JPanel jPanel2 = new JPanel(new GridLayout(2,1));
        JPanel jPanel3 = new JPanel(new GridLayout(1,1));
        // Questo è il pannello che conterrà i bottoni
        JPanel jPanel1 = new JPanel(new GridLayout(5,1,15,0));
        jPanel.add(jPanel1);
        // Imposto gli eventi sui bottoni
        setJButtonEvent();
        // Li aggiungo al pannello
        addButton(jPanel1);
        // set the layout
        pa.setLayout(new GridLayout(1,2));
        // Aggiungo il logo
        Logo logo = new Logo(dx / 2, dy);
        jPanel3.add(logo);
        jPanel2.add(jPanel3);
        // Aggiungo il pannello
        jPanel2.add(jPanel1);
        // Aggiungo al pannello principale
        pa.add(jPanel2);
        pa.add(jPanel);
        // Initializing the JTable
        j = new FriendsTable(client);
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
    }
    /**
     * Mostro la dashboard
     */
     public void open(){
        // Lo rendo visibile
        setVisible(true);
    }
    /**
     * Aggiungo un gestore di eventi ai bottoni (la classe stessa)
     */
    private void setJButtonEvent(){
        jButton.addActionListener(this);
        jButton1.addActionListener(this);
        jButton2.addActionListener(this);
        jButton3.addActionListener(this);
        jButton4.addActionListener(this);
    }
    /**
     * Aggiungo i bottoni al panel
     * @param jPanel zona in cui verranno visualizzati i bottoni
     */
    private void addButton(JPanel jPanel){
        jPanel.add(jButton);
        jPanel.add(jButton1);
        jPanel.add(jButton2);
        jPanel.add(jButton3);
        jPanel.add(jButton4);
    }
    /**
     * Gestisco gli eventi
     * @param actionEvent la descrizione dell'evento e cià che lo ha generato
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JButton jButton = (JButton) actionEvent.getSource();
        IfAction(jButton);
    }
    /**
     * Nel caso in cui venga premuto un bottone
     * @param button il bottone dell'azione
     */
    private void IfAction(JButton button) {
        // Questa variabile controlla se non sono in fase di logout
        boolean logout = false;
        // Messaggio da vedere nella barra in alto
        String message = "Errore generico";
        try{
            // Se ho premuto il logout
            if(button.equals(jButton4)){
                logout = true;
                message = "Errore nella disconnessione";
                IfLogout();
            }
            //o per una nuova richiesta di amicizia
            if(button.equals(jButton)){
                message = "Errore nella richiesta di nuova amicizia";
                IfFriendRequest();
            }
            // o per visualizzare i miei amici
            if(button.equals(jButton2)) {
                message = "Errore nella richiesta della classifica";
                IfFriendList();
            }
            // o per visualizzare il punteggio di un utente
            if(button.equals(jButton3)){
                message = "Errore nella richesta di nuova punteggio utente";
                IfUserScore();
            }

            //o è una richiesta di sfida
            if(button.equals(jButton1)){
                message = "Errore nella richiesta di una sfida";
                IfStartMatch();
            }
        } catch (IOException  | IllegalArgumentException e) {
            // Mostro un Dialog con l'errore
            showDialog(e.getMessage(),message,JOptionPane.ERROR_MESSAGE);
        } finally {
            // Se l'utente ha effettuato il logout allora chiudo la UI
            if(logout){
                // Chiudo la schermata
                this.setVisible(false);
                // Creo una nuova UI di Login
                new UI();
            }
        }
    }
    /**
     * Se l'utente ha tentato il logout
     * @throws IOException Nel caso in cui non riesca a comunicare con il server
     */
    private void IfLogout() throws IOException{
        // Chiudo la connessione
        client.closeConnection();
        // Elimino il timer che aggiorna la barra sinistra con gli utenti e il punteggio
        j.stopTimer();
        // Restituisco un messaggio
        showDialog("Login eseguito con successo","Logout",JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Se devo inviare una richiesta di amicizia
     * @throws IOException Nel caso in cui non riesca a comunicare con il server
     */
    private void IfFriendRequest() throws IOException{
        // Mostro un messaggio
        String name = JOptionPane.showInputDialog(
                this,
                "Chi vuoi aggiungere come amico?"
        );
        // Se l'utente non ha lasciato la casella vuota
        if(name != null){
            // Costruisco e invio la richiesta
            AddFriend request = new AddFriend();
            request.setUser(name);
            client.addFriend(request);
            // Mostro un dialog informativo
            showDialog("Hai un nuovo amico nella tua lista","Richiesta amicizia",JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /**
     * Se l'utente vuole la lista dei suoi amici
     * @throws IOException Nel caso in cui non riesca a comunicare con il server
     */
    private void IfFriendList() throws IOException{
        // Invio la richiesta
        ListFriends ls = new ListFriends();
        ls.setUser(client.getUserModel().getUser());
        String list = client.listFriends(ls);
        // Faccio il parsing del JSON
        String[] dataArray = new Gson().fromJson(list,(java.lang.reflect.Type) String[].class);
        // Costruisco il messaggio del Dialog
        StringBuilder str = new StringBuilder("\n");
        for(String usr: dataArray){
            str.append(" \n  - ").append(usr);
        }
        // Mostro il dialog
        showDialog("I tuoi amici sono : " + str.append("\n\n").toString(),"Lista degli amici",JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Se l'utente vuole sapere il punteggio di un altro utente
     * @throws IOException Nel caso in cui non riesca a comunicare con il server
     */
    private void IfUserScore() throws IOException{
        UserScore ls = new UserScore();
        // Mostro un dialog per sapere chi è l'utente desiderato
        String name = JOptionPane.showInputDialog(
                this,
                "Di chi vuoi sapere il punteggio ?"
        );
        // Se l'utente inserito è valido allo invio la richiesta
        if(name != null){
            ls.setUser(name);
            Integer score = client.userScore(ls);
            if(score != null){
                showDialog("Il punteggio di " + name + " è : " +  score,"Punteggio amico",JOptionPane.INFORMATION_MESSAGE);
            } else {
                showDialog("L'utente selezionato non è tra i tuoi amici" ,"Punteggio amico",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /**
     * Se l'utente vuole avviare un nuovo match
     * @throws IOException Nel caso in cui non riesca a comunicare con il server
     */
    private void IfStartMatch() throws IOException {
        // Ottengo la lista degli amici
        ListFriends ls = new ListFriends();
        ls.setUser(client.getUserModel().getUser());
        String list = client.listFriends(ls);
        // Mostro all'utente un dialog con la lista dei suoi amici
        // In questa lista dovrà scegliere quale sfidare
        String name = getValueFromUser(new Gson().fromJson(list,String[].class));
        // Nascondo la dashboard
        this.setVisible(false);
        try {
            // Gestisco la richiesta di sfida
            client.handleMatchAnswers(name,datagramSocketAnswers,s,port);
        } catch (InterruptedException e) {
            System.out.println("Si è chiuso in maniera inaspettata");
        } finally {
            // Mostro di nuovo la dashboard
            this.setVisible(true);
        }
    }
    /**
     * Mostro all'utente la lista dei suoi amici e gli chiedo di sceglierne uno da sfidare
     * @param lst Lista degli amici
     * @return L'amico scelto
     */
    private String getValueFromUser(String[] lst){
        // Chiedo all'utente quale utente vuole sfidare
        String name = (String) JOptionPane.showInputDialog(
                this,
                "Chi vuoi sfidare :\n",
                "Richiesta sfida",
                JOptionPane.PLAIN_MESSAGE,
                null,
                lst,
                "ham");
        // Normalizzo il valore
        return name == null ? "" : name;
    }
    /**
     * Mostro un dialog
     * @param msg Il messaggio del dialog
     * @param title Il titolo del dialog
     * @param type Il tipo del dialog
     */
    private void showDialog(String msg,String title, int type){
        JOptionPane.showMessageDialog(this,
                msg,
                title,
                type);
    }
    /**
     * Posiziono l'interfaccia a centro
     */
    private void centerFrame() {
        // Ottengo le dimensioni dell'ambiente
        Dimension windowSize = getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        // Ottengo le coordinate
        this.dx = windowSize.width;
        this.dy = windowSize.height;
        int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2;
        setLocation(dx, dy);
    }
}
