package Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
/**
 * Descrive un utente dopo la registrazione e durante la connessione
 */
public class UserModel implements Serializable {
    /**
     * Variabili d'istanza
     */
    private String user;
    private String password;
    private Integer points = 0;
    private ArrayList<String> friends;
    /**
     * Il costruttore
     */
    public UserModel(){
        super();
        friends = new ArrayList<>();
    }
    /**
     * Restituisce la lista degli utenti
     * @return lista degli utenti dell'utente
     */
    public ArrayList<String> getFriends() {
        return friends;
    }
    /**
     * Imposta lo user dell'utente
     * @param user stringa contenente il nome dell'utente
     * @return this
     */
    public UserModel setUser(String user) {
        this.user = user;
        return this;
    }
    /**
     * Imposta la password
     * @param password la password relativa all'utente
     * @return this
     */
    public UserModel setPassword(String password) {
        this.password = password;
        return this;
    }
    /**
     * Imposta i punti dell'utente
     * @param points i punti da aggiungere
     */
    public void setPoints(Integer points) {
        this.points += points;
    }
    /**
     * Aggiungo un utente alla lista degli amici
     * @param friend amico da aggiungere
     */
    public void addFriend(String friend){
        if(friends.contains(friend))
            return;

        friends.add(friend);
    }
    /**
     * Il metodo toString
     * @return la stringa formattata
     */
    @Override
    public String toString() {
        return "UserModel{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
    /**
     * Se sono uguali gli username
     * @param o l'oggetto con cui confrontarlo
     * @return se l'oggetto è uguale o meno
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return user.equals(userModel.user);
    }
    /**
     * Se sono uguali username e password
     * @param o l'oggetto con cui confrontarlo
     * @return  se l'oggetto è uguale o meno
     */
    public boolean isEqual(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return  userModel.user.equals(this.user) && userModel.password.equals(this.password);
    }
    /**
     * Restituisce l'utente
     * @return l'utente
     */
    public String getUser() {
        return user;
    }
    /**
     * restituisce la password dell'utente
     * @return la password dell'utente
     */
    public String getPassword() {
        return password;
    }
    /**
     * Il numero di punti dell'utente
     * @return il numero di punti
     */
    public Integer getPoints() {
        return points;
    }


}
