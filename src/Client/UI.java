package Client;

import Client.Components.Dashboard;
import Client.Components.Logo;
import Client.Runnable.WaitForChallengeRunnable;
import Client.TCP.TCPClient;
import Models.UserModel;
import Remote.Exception.PasswordNotValidException;
import Remote.Exception.UserAlreadyExistsException;
import Remote.I_RMI_API_Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Questa classe costruisce il JFrame iniziale, quello della login/signup
 *
 * @author Federico Pennino
 */
public class UI extends JFrame implements ActionListener {
    /**
     * I componenti relativi all'interfaccia
     */
    private JPanel jPanel = new JPanel(new GridLayout(2, 1));
    private JPanel jPanel1 = new JPanel();
    private JPanel jPanel2 = new JPanel();
    private JLabel userLabel = new JLabel("USERNAME :");
    private JLabel passwordLabel = new JLabel("PASSWORD :");
    private JTextField userTextField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JButton loginButton = new JButton("ACCEDI");
    private JButton signUpButton = new JButton("REGISTRATI");
    /**
     * Dimensioni finestra
     */
    private int dx;
    private int dy;
    /**
     *  Questo è il costruttore, imposta le forme e le azioni del JFrame che poi renderà visibile
     */
    public UI() {
        // Costruisco la UI
        super("Word Quizzle");
        setSize(310, 420);
        // La posiziono al centro
        this.centerFrame();
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        this.getContentPane().add(jPanel);
        setResizable(false);
        setVisible(true);
        // Imposto le azioni dei JButton
        setButtonAction();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    /**
     * Questo meotodo imposta i layout
     */
    public void setLayoutManager() {
        jPanel1.setLayout(new BorderLayout());
        jPanel2.setLayout(null);
    }
    /**
     * Posiziona le componenti nell'interfaccia
     */
    public void setLocationAndSize() {
        // Imposto dimensione e posizione di ogni componente
        userLabel.setBounds(30, 0, 100, 30);
        passwordLabel.setBounds(30, 220 - 15 - 150, 100, 30);
        userTextField.setBounds(120, 0, 150, 30);
        passwordField.setBounds(120, 220 - 15 - 150, 150, 30);
        loginButton.setBounds(20, 300 - 30 - 150, 100, 30);
        signUpButton.setBounds(140, 300 - 30 - 150, 130, 30);
    }
    /**
     * Aggiungo le componenti grafiche
     */
    public void addComponentsToContainer() {
        // Aggiungo ogni componente al container
        jPanel.add(jPanel1);
        jPanel.add(jPanel2);
        jPanel1.add(new Logo(dx, dy / 2), BorderLayout.CENTER);
        jPanel2.add(userLabel);
        jPanel2.add(passwordLabel);
        jPanel2.add(userTextField);
        jPanel2.add(passwordField);
        jPanel2.add(loginButton);
        jPanel2.add(signUpButton);
    }
    /**
     * Imposto i listener ai bottoni dell'interfaccia
     */
    public void setButtonAction() {
        loginButton.addActionListener(this);
        signUpButton.addActionListener(this);
    }
    /**
     * Eseguo le azioni associate ai listener
     * @param e questo parametro descrive il tipo di evento e la sorgente che lo ha generato
     */
    public void actionPerformed(ActionEvent e) {
        // Mi connetto tramite RMI per la registrazione
        try {
            IfActionIs((JButton) e.getSource());
        }
        // In caso di errore mostro un Dialog
        catch (NotBoundException | UserAlreadyExistsException | PasswordNotValidException | IOException ex) {
            this.showDialog(ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Questo metodo fa da switch tra la richiesta di SignIn e di SignUp
     * @param b Il bottone che ha generato l'evento
     * @throws IOException Se non riesce a comunicare con il Server TCP
     * @throws PasswordNotValidException Se la password inserita non è valida
     * @throws UserAlreadyExistsException Se l'utente esiste già
     * @throws NotBoundException Se non c'e' un binding associato al registry
     */
    public void IfActionIs(JButton b) throws IOException, PasswordNotValidException, UserAlreadyExistsException, NotBoundException {
        if (b.equals(signUpButton)) {
            IfSignUp();
        }
        if (b.equals(loginButton)) {
            IfSignIn();
        }
    }
    /**
     * Eseguo un tentativo di registrazione
     * @throws UserAlreadyExistsException Se l'utente esiste già
     * @throws RemoteException Se l'RMI lancia una eccezione
     * @throws PasswordNotValidException Se la password inserita non è valida
     * @throws NotBoundException Se non c'e' un binding associato al registry
     */
    public void IfSignUp() throws UserAlreadyExistsException, RemoteException, PasswordNotValidException, NotBoundException {
        Registry reg = LocateRegistry.getRegistry(5099);
        I_RMI_API_Client data = (I_RMI_API_Client) reg.lookup("SignUp");
        // Provo la registrazione tramite RMI
        data.registration(userTextField.getText(), new String(passwordField.getPassword()));
        this.showDialog("Registrazione avvenuta con successo !", "Successo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Eseguo un tentativo di login
     * @throws IOException Se genera un errore la connessione TCP
     */
    public void IfSignIn() throws IOException {
        // Eseguo il login
        TCPClient client = new TCPClient(new UserModel().setUser(userTextField.getText()).setPassword(new String(passwordField.getPassword())));
        if (client.getResult()) {
            this.setVisible(false);
            // Ottengo la porta UDP relativa all'utente
            int port = client.getPortUDP();
            // Creo un socket UDP su porta dinamica
            DatagramSocket s = new DatagramSocket();
            DatagramSocket datagramSocketAnswers = new DatagramSocket();
            // Invio la porta UDP su cui aspetto le richieste di partita
            client.sendUDPPort(s.getLocalPort());
            client.sendUDPPort(datagramSocketAnswers.getLocalPort());
            Dashboard dashboard = new Dashboard(client, port, s,datagramSocketAnswers);
            // Attivo un thread che aspetta le richieste di sfida
            client.setChallengeWaiter(new WaitForChallengeRunnable(s,dashboard));
            dashboard.open();
        } else {
            this.showDialog("Username o password non valide!", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Mostro un Dialog
     * @param msg Il messaggio da visualizzare
     * @param title Il titolo da visualizzare
     * @param type IL tipo di Dialog da visualizzare
     */
    private void showDialog(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this,
                msg,
                title,
                type);
    }

    /**
     * Posiziono l'interfaccia a centro
     */
    private void centerFrame() {
        // Ottengo le dimesioni dell'ambiente
        Dimension windowSize = getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        // Imposto le coordinate
        this.dx = windowSize.width;
        this.dy = windowSize.height;
        int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2;
        // Lo posiziono
        setLocation(dx, dy);
    }
}
