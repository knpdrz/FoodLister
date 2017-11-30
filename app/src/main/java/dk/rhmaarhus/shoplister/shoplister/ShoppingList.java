package dk.rhmaarhus.shoplister.shoplister;

/**
 * Created by Moon on 30.11.2017.
 */

public class ShoppingList {
    private String name;

    public ShoppingList(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
    public void setName(){
        this.name = name;
    }
}
