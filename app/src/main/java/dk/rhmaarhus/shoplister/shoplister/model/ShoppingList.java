package dk.rhmaarhus.shoplister.shoplister.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Moon on 30.11.2017.
 */

public class ShoppingList {
    public String name;
    private Map<String, User> members;
    private String firebaseKey;

    public ShoppingList(){}

    public ShoppingList(String name, User user){
        this.name = name;
        this.members = new HashMap<String,User>();
        members.put(user.getUid(),user);
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }


    public Map<String, User> getMembers() {
        return members;
    }
}
