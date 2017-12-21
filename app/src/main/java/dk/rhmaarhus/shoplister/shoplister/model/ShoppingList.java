package dk.rhmaarhus.shoplister.shoplister.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Moon on 30.11.2017.
 */

public class ShoppingList {
    private String name;
    private String firebaseKey;
    private boolean newlyAdded;

    public ShoppingList(){}

    public ShoppingList(String name, boolean newlyAdded){
        this.name = name;
        this.newlyAdded = newlyAdded;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public void setName(){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public boolean getNewlyAdded() {return newlyAdded;};

    public void setNewlyAdded(boolean newlyAdded) {this.newlyAdded = newlyAdded; }

}
