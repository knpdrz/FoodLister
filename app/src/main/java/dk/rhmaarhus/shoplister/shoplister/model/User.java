package dk.rhmaarhus.shoplister.shoplister.model;

import java.net.URI;

/**
 * Created by Moon on 30.11.2017.
 */

public class User {
    private String name;
    private String email;
    private URI imageURI;
    private String uid;

    public User(){}
    public User(String name, String email, String uid, URI imageURI){
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.imageURI = imageURI;
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

    public URI getImageURI() {
        return imageURI;
    }
}
