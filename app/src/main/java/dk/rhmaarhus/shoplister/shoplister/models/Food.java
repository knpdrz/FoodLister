package dk.rhmaarhus.shoplister.shoplister.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rjkey on 30-11-2017.
 */

public class Food {
    @SerializedName("name")
    @Expose
    public String Name;
    @SerializedName("image")
    @Expose
    public String Image;
}
