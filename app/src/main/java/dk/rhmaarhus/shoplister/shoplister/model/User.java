package dk.rhmaarhus.shoplister.shoplister.model;

/**
 * Created by Moon on 30.11.2017.
 */

public class User {
    private String name;
    private String email;
    private String image;
    private String uid;

    public User(){}
    public User(String name, String email, String uid){
        this.uid = uid;
        this.name = name;
        this.email = email;
        //todo !
        this.image = "thiswillbeurlorsth";
    }

    public String getEmail(){
        return email;
    }

    public String getName(){
        return name;
    }

    public String getUid() {
        return uid;
    }
}
