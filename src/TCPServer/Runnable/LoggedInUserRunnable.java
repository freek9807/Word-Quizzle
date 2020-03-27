package TCPServer.Runnable;

import Models.SignedUpUsersListModel;
import Models.UserModel;
import Request.AddFriend;
import Request.ListFriends;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Questa classe gestisce il Runnable della connessione Server-Client
public class LoggedInUserRunnable implements Runnable {
    // Le variabili d'istanza
    private Socket socket;
    private ObjectInputStream ois;
    private DataOutputStream dos;
    // Le informazioni dell'utente che fa riferimento a questo Runnable
    private UserModel clientUser;
    private SignedUpUsersListModel suul;

    public LoggedInUserRunnable(
            Socket socket,
            ObjectInputStream ois,
            DataOutputStream dos,
            UserModel clientUser,
            SignedUpUsersListModel suul
    ) {
        this.clientUser = clientUser;
        this.socket = socket;
        this.ois = ois;
        this.dos = dos;
        this.suul = suul;
    }

    @Override
    public void run() {
        try {
            // Aspetto che mi arrivi un nuovo Object dal client
            while(true){
                Object obj = (Object) ois.readObject();

                if(obj instanceof AddFriend){
                    AddFriend friend = (AddFriend) obj;
                    boolean res = suul.addFriendEdge(clientUser,new UserModel().setUser(friend.getName()));
                    System.out.println("Arco di amicizia aggiunto tra : "+ clientUser.user +" - "+ friend.getName());
                    dos.writeBoolean(res);
                }

                if(obj instanceof ListFriends){
                    ListFriends ls = (ListFriends) obj;
                    ArrayList<String> friends = suul.retrieveFriends(ls);
                    Gson gson = new Gson();
                    String json = gson.toJson(friends);

                    byte[] output = json.getBytes(StandardCharsets.UTF_8);
                    dos.writeInt(output.length);
                    dos.write(output);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Se qualcosa va storto fermo la connessione al client
            try {
                ois.close();
                dos.close();
                socket.close();
                System.out.println("Connessione chiusa da : " + clientUser.user);
            } catch (IOException ex) {
                System.out.println("Errore nella chiusura del socket");
            }
        }
    }
}
