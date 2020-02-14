package Remote.Models;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class SignedUpUsersListModel {
    private ArrayList<UserModel> data;
    private final String nameFile = "userList.json";
    public enum addResult{
        FULL,
        OKAY,
        EXISTS
    }

    public SignedUpUsersListModel(){
        synchronized (this) {
            try {
                FileInputStream fis = new FileInputStream(nameFile);
                InputStreamReader isr = new InputStreamReader(fis);
                UserModel[] dataArray = new Gson().fromJson(isr, (Type) UserModel[].class);
                data = new ArrayList<UserModel>();
                Collections.addAll(data, dataArray);
                fis.close();
            } catch (FileNotFoundException e) {
                data = new ArrayList<UserModel>();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int size(){
        return data.size();
    }

    public void restore(){
        data = new ArrayList<UserModel>();
    }

    public addResult add(UserModel um){
    synchronized (this) {
        for (UserModel uml : data)
            if (uml.equals(um))
                return addResult.EXISTS;

        if (data.add(um))
            return addResult.OKAY;
        else
            return addResult.FULL;
    }
    }

    public boolean store(){
        synchronized (this) {
            try {
                FileOutputStream fos = new FileOutputStream(nameFile);
                Gson gson = new Gson();
                String s = gson.toJson(data);
                byte b[] = s.getBytes();
                fos.write(b);
                fos.close();
            } catch (IOException ioe) {
                return false;
            }
            return true;
        }
    }

}
