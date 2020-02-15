package Client;

import Client.Components.Logo;
import Remote.Exception.PasswordNotValidException;
import Remote.Exception.UserAlreadyExistsException;
import Remote.I_RMI_API_Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class UI extends JFrame implements ActionListener {
    // I componenti relativi all'interfaccia
    JPanel jPanel = new JPanel(new GridLayout(2, 1));
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JLabel userLabel = new JLabel("USERNAME :");
    JLabel passwordLabel = new JLabel("PASSWORD :");
    JTextField userTextField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("ACCEDI");
    JButton signupButton = new JButton("REGISTRATI");


    public UI() {
        // Costruisco la UI
        super("Word Quizzle");
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        setSize(310, 420);
        this.getContentPane().add(jPanel);
        // La posiziono al centro
        this.centerFrame();
        setResizable(false);
        setVisible(true);
        // Imposto le azioni dei JButton
        setButtonAction();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    // Imposto i layout
    public void setLayoutManager() {
        jPanel1.setLayout(new BorderLayout());
        jPanel2.setLayout(null);
    }
    // Posiziono le componenti nell'interfaccia
    public void setLocationAndSize() {
        //Setting location and Size of each components using setBounds() method.
        userLabel.setBounds(30, 0, 100, 30);
        passwordLabel.setBounds(30, 220 - 15 - 150, 100, 30);
        userTextField.setBounds(120, 0, 150, 30);
        passwordField.setBounds(120, 220 - 15 - 150, 150, 30);
        loginButton.setBounds(20, 300 - 30 - 150, 100, 30);
        signupButton.setBounds(170, 300 - 30 - 150, 100, 30);

    }
    // Aggiungo le componenti grafiche
    public void addComponentsToContainer() {
        //Adding each components to the Container
        jPanel.add(jPanel1);
        jPanel.add(jPanel2);
        jPanel1.add(new Logo(5, 5), BorderLayout.CENTER);
        jPanel2.add(userLabel);
        jPanel2.add(passwordLabel);
        jPanel2.add(userTextField);
        jPanel2.add(passwordField);
        jPanel2.add(loginButton);
        jPanel2.add(signupButton);
    }
    // Imposto i listener
    public void setButtonAction() {
        signupButton.addActionListener(this);
    }
    // Eseguo le azioni
    public void actionPerformed(ActionEvent e) {
        // Mi connetto tramite RMI per la registrazione
        if ((JButton) e.getSource() == signupButton) {
            try {
                Registry reg = LocateRegistry.getRegistry(5099);
                I_RMI_API_Client data = (I_RMI_API_Client) reg.lookup("SignUp");
                // Provo la registrazione tramite RMI
                data.registration(userTextField.getText(), new String(passwordField.getPassword()));
                this.showDialog("Registrazione avvenuta con successo !","Successo",JOptionPane.INFORMATION_MESSAGE);
            }
            // In caso di errore mostro un Dialog
            catch (RemoteException | NotBoundException | UserAlreadyExistsException | PasswordNotValidException ex) {
                this.showDialog(ex.getMessage(),"Errore",JOptionPane.ERROR_MESSAGE);
            }
        }
        // Eseguo il login
        else if ((JButton) e.getSource() == loginButton) {

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

        int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2;
        setLocation(dx, dy);
    }
}
