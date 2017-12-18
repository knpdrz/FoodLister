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

    public ShoppingList(){}

    public ShoppingList(String name){
        this.name = name;

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

}
