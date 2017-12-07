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

    public ShoppingItem(){;}

    public String getName(){
        return name;
    }
    public void setName(){
        this.name = name;
    }
    public void flipMarked() {this.marked = !this.marked;}
    public Boolean getMarked() {return this.marked;}
    public void setMarked(boolean marked) {this.marked = marked;}
}
