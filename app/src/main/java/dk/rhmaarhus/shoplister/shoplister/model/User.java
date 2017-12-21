package dk.rhmaarhus.shoplister.shoplister.model;

import java.io.Serializable;

/**
 * Created by Moon on 30.11.2017.
 */

public class User implements Serializable {
    private String name;
    private String email;
    private String imageUrl;
    private String uid;

    public User(){}
    public User(String name, String email, String uid, String imageUrl){
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }
}
