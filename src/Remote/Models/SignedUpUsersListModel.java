package Remote.Models;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public  class SignedUpUsersListModel {
    private ArrayList<UserModel> data;
    private final String nameFile = "userList.json";
    public enum addResult{
        FULL,
        OKAY,
        EXISTS
    }

    public SignedUpUsersListModel(){ }

    private void retrieve(){
            try {
                FileInputStream fis = new FileInputStream(nameFile);
                InputStreamReader isr = new InputStreamReader(fis);
                UserModel[] dataArray = new Gson().fromJson(isr,(Type) UserModel[].class);
                data = new ArrayList<UserModel>();
                Collections.addAll(data, dataArray);
                fis.close();
            } catch (FileNotFoundException e) {
                data = new ArrayList<UserModel>();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public int size(){
        return data.size();
    }

    public void restore(){
        synchronized (this) {
            data = new ArrayList<UserModel>();
            this.store();
        }
    }

    public synchronized addResult add(UserModel um){
        synchronized (this) {
            this.retrieve();
            for (UserModel uml : data) {
                if (uml.equals(um))
                    return addResult.EXISTS;
            }

            if (data.add(um)) {
                this.store();
                return addResult.OKAY;
            } else
                return addResult.FULL;
        }
    }

    private boolean store(){
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
