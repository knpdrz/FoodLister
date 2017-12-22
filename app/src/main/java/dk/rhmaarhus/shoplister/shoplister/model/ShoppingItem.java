package dk.rhmaarhus.shoplister.shoplister.model;

/**
 * Created by hulda on 30.11.2017.
 */

public class ShoppingItem {
    private String name;
    private boolean marked;
    private boolean newlyAdded;
    private String uid;

    public ShoppingItem(String name, boolean newlyAdded, String uid){
        this.name = name;
        marked = false;
        this.newlyAdded = newlyAdded;
        this.uid = uid;
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

    public boolean getNewlyAdded() {return newlyAdded;};

    public void setNewlyAdded(boolean newlyAdded) {this.newlyAdded = newlyAdded; }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }
}
