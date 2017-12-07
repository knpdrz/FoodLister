package dk.rhmaarhus.shoplister.shoplister.model;

/**
 * Created by Moon on 30.11.2017.
 */

public class ShoppingList {
    public String name;

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    private String firebaseKey;
    public ShoppingList(){;}
    public ShoppingList(String name){
        this.name = name;
    }

}
