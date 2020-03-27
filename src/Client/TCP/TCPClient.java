package Client.TCP;

import Models.UserModel;
import Request.AddFriend;
import Request.ListFriends;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class TCPClient {

        private boolean result;
        private DataInputStream dis;
        private ObjectOutputStream oos;
        private SocketChannel client;
        private UserModel userModel;
        public TCPClient(UserModel user)
                throws IOException {
            this.userModel = user;
            InetSocketAddress hA = new InetSocketAddress("localhost", 5454);
            client = SocketChannel.open(hA);
            Socket socket = client.socket();
            System.out.println("Invio la richiesta al server...");
            oos = new ObjectOutputStream(client.socket().getOutputStream());
            oos.writeObject(user);
            dis = new DataInputStream(client.socket().getInputStream());
            result = dis.readBoolean();
        }

    public UserModel getUserModel() {
        return userModel;
    }

    public void closeConnection() throws IOException {
            dis.close();
            oos.close();
            client.close();
            System.out.println("Ho chiuso la connessione al server");
        }

        public void addFriend(AddFriend friend) throws IOException,IllegalArgumentException {
            oos.writeObject(friend);
            if(!dis.readBoolean())
                throw new IllegalArgumentException("Utente amico non registrato!");
        }

        public void listFriends(ListFriends ls) throws IOException {
            oos.writeObject(ls);
            int dim = dis.readInt();
            byte[] input = new byte[dim];
            dis.readFully(input);
            String str = new String(input, StandardCharsets.UTF_8);
            System.out.println(str);
        }

        public Boolean getResult() {
        return result;
    }

}
