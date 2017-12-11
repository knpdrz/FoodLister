package dk.rhmaarhus.shoplister.shoplister.model;

/**
 * Created by Moon on 30.11.2017.
 */

public class User {
    private String name;
    private String image;
    private String uid;

    public User(){}
    public User(String name, String uid){
        this.uid = uid;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getUid() {
        return uid;
    }
}
