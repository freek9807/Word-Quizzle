package Models;
import Request.ListFriends;
import Request.UserFriendsRank;
import Request.UserScore;
import Settings.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Descrive il file contenente la lista degli utenti
 */
public  class SignedUpUsersListModel {
    /**
     * Il nome del file in cui sono/saranno salvati gli utenti
     */
    private final String nameFile = Constants.SIGNED_UP_USER_FILE;
    /**
     * L'astrazione del file utenti
     */
    File file = new File(nameFile);
    /**
     * L'hash map in cui salvo le coppie nickUtente - Utente
     */
    private final ConcurrentHashMap<String,UserModel> users = new ConcurrentHashMap<>();
    /**
     * I possibili valori di un inserimento nell'hash map
     */
    public enum addResult{
        FULL,
        OKAY,
        EXISTS
    }
    /**
     * Il costruttore
     */
    public SignedUpUsersListModel() {
        try{
            // Se il file esiste recupero gli utenti
            if(file.exists()){
                this.retrieve();
            } else{
                // Altrimenti provo a crearlo
                if(!file.createNewFile())
                    throw new IOException("Impossibile creare il file con gli utenti");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Recupero gli utenti dal file
     */
    private void retrieve(){
            try {
                // Apro il file per leggerlo e faccio il parsing del JSON
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                UserModel[] dataArray = new Gson().fromJson(isr,(Type) UserModel[].class);
                // Aggiungo i valori a una nuova ArrayList
                ArrayList<UserModel> data = new ArrayList<>();
                Collections.addAll(data, dataArray);
                // Inserisco i valori nella hash map
                data.forEach(userModel -> {
                    // Controllo che nessuno cerchi di modificare il valore da inserire
                    synchronized (userModel){
                        users.put(userModel.getUser(),userModel);
                    }
                });
                // Chiudo lo stream
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    /**
     * Ottengo la dimensione della hash map
     * @return il numero di utenti registrati
     */
    public int size(){
        return users.size();
    }
    /**
     * Azzero la lista degli utenti
     */
    public synchronized void restore(){
        users.clear();
        this.store();
    }
    /**
     * Provo ad aggiungere un nuovo utente
     * @param um l'utente da aggiungere
     * @return se tutto è andato okay
     */
    public addResult add(UserModel um){
        if(users.putIfAbsent(um.getUser(),um) == null){
            // Aggiungo l'utente e salvo il file
            this.store();
            return addResult.OKAY;
        } else {
            // Se l'utente è già presente annullo tutto
            return addResult.EXISTS;
        }
    }
    /**
     * Controllo se l'utente esiste
     * @param um l'utente da controllare
     * @return se è un utente valido o meno
     */
    public boolean isValid(UserModel um){
        return users.containsKey(um.getUser()) && users.get(um.getUser()).getPassword().equals(um.getPassword());
    }
    /**
     * Aggiungo un nuovo arco di amicizia
     * @param a l'utente che richiede l'aggiunta
     * @param b l'utente da aggiungere agli amici
     * @return se l'operazione è andata a buon fine o meno
     */
    public boolean addFriendEdge(UserModel a, UserModel b){
        // Se i due utenti sono validi proseguo
        if(!users.containsKey(a.getUser()) || !users.containsKey(b.getUser()) || a.equals(b)){
            System.out.println("Mi fermo qui 1");
            return false;
        }
        // Ottengo i due utenti
        final UserModel au = users.get(a.getUser());
        final UserModel bu = users.get(b.getUser());
        // Li sincronizzo
        synchronized (au){
            synchronized (bu){
                // Se l'arco esiste già annullo tutto
                for(String um: au.getFriends()){
                    if(um.equals(bu.getUser())){
                        return false;
                    }
                }
                // Aggiungo gli archi
                bu.addFriend(a.getUser());
                au.addFriend(b.getUser());
            }
        }
        // Salvo nel file
        this.store();
        return true;
    }
    /**
     * Recupero gli amici di utente
     * @param lf la richiesta della lista degli amici
     * @return il json con la lista degli amici
     */
    public String retrieveFriends(ListFriends lf){
        UserModel user = users.get(lf.getUser());
        // Aspetto che tutti abbiano rilasciato l'oggetto
        // e restituisco il json con le informazioni aggiornte
        synchronized (user){
            return new Gson().toJson(user.getFriends());
        }
    }
    /**
     * Recupero la classifica degli amici di un utente
     * @param lf Richiesta di listare la lista degli utenti in base al punteggio
     * @return la classifica in formato JSON
     */
    public String friendsRank(UserFriendsRank lf){
        // Lista da restituire
        ArrayList<RankNode> ls = new ArrayList<>();
        // Recupero l'utente dall richiesta (oggetto lf)
        UserModel user = users.get(lf.getUser());

        // Recupero le informazioni dell'utente
        synchronized (user){
            ls.add(new RankNode("<html><b>" +  user.getUser() + "</b></html>" ,user.getPoints()));
        }
        // Per ogni amico dell'utente
        for(String lsUser : user.getFriends()){
            final UserModel friend = users.get(lsUser);
            // Aggiungo l'utente e il punteggio alla lista da restituire
            synchronized (friend){
                ls.add(new RankNode(friend.getUser(),friend.getPoints()));
            }
        }
        // Ordino la lista con user - punteggi
        Collections.sort(ls);
        // la restituisco
        return new Gson().toJson(ls);
    }
    /**
     * Aggiunta dei punti a un utente
     * @param points il numero dei punti di aggiungere
     * @param user l'utente a cui aggiungere i punti
     */
    public void addPoints(int points,String user){
        // Recupero l'utente dall richiesta (oggetto lf)
        UserModel userM = users.get(user);

        // Recupero le informazioni dell'utente
        synchronized (user){
            userM.setPoints(points);
        }

        this.store();
    }
    /**
     * Ottengo il punteggio di un utente
     * @param us Richiesta dell'utente di cui si vuole sapere il punteggio
     * @return il nodo contenente i punti dell'utente richiesto
     */
    public RankNode userScore(UserScore us){

        UserModel user = null;
        RankNode node;
        // Guardo se in users è presente l'utente
        if(users.containsKey(us.getUser())){
            user = users.get(us.getUser());
        }
        // Se è presente allora
        if(user != null){
            // Aspetto che sia libero
            synchronized (user){
                node = new RankNode(us.getUser(),user.getPoints());
            }
            // Restituisco il valore
            return node;
        }
        // Altrimenti non restituisco nulla
        return null;
    }
    /**
     * Salva le informazioni sul file
     */
    private synchronized void store(){
            try {
                // Creo uno stream in uscita
                FileOutputStream fos = new FileOutputStream(file);
                // Creo un oggetto GSON che formatti bene il testo
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                // Trasporto la HashMap in una ArrayList
                ArrayList<UserModel> data = new ArrayList<>();
                users.forEach((s, userModel) -> {
                    synchronized (userModel){
                        data.add(userModel);
                    }
                });
                // Scrivo tutto sul file
                String s = gson.toJson(data);
                byte[] b = s.getBytes();
                fos.write(b);
                // Chiudo lo stream
                fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
