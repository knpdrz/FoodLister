package dk.rhmaarhus.shoplister.shoplister.model;

/**
 * Created by hulda on 30.11.2017.
 */

public class ShoppingItem {
    private String name;
    private boolean marked;

    public ShoppingItem(String name){
        this.name = name;
        marked = false;
    }

    public String getName(){
        return name;
    }
    public void setName(){
        this.name = name;
    }
    public void flipMarked() {this.marked = !this.marked;}
}
