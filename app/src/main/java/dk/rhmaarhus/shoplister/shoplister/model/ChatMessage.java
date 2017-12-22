package dk.rhmaarhus.shoplister.shoplister.model;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.TAG;

/**
 * Created by rjkey on 12-12-2017.
 */
//https://code.tutsplus.com/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397
public class ChatMessage {

    private String message;
    private String user;
    private String messageTime;
    private String uid;
    private boolean newlyAdded;

    public ChatMessage(String chatMessage, String chatUser, String uid, boolean newlyAdded){
        message = chatMessage;
        user = TrimUsername(chatUser);
        messageTime = GetGMTTime();
        this.uid = uid;
        this.newlyAdded = newlyAdded;

        TrimUsername(user);
    }

    private String GetGMTTime() {
        TimeZone myTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        long time = date.getTime();

        String gmtDate = String.valueOf(time);

        TimeZone.setDefault(myTimeZone);
        return gmtDate;
    }

    private String TrimUsername(String user) {
        int spacePos = user.indexOf(" ");
        String string = user;
        if (spacePos > 0){
            string = user.substring(0, spacePos);
        }
        return string;
    }

    public ChatMessage(){

    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getUser(){
        return user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public String getMessageTime(){
        return messageTime;
    }

    public void setMessageTime(String time){
        try{
            long longTime = Long.parseLong(time);
            Date d = new Date(longTime);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String currentdate = sdf.format(d);
            messageTime = currentdate;

        }catch(Exception e){
            Log.d(TAG, "setMessageTime: " + e);
            messageTime = time;
        }
    }

    public boolean getNewlyAdded() {return newlyAdded;};

    public void setNewlyAdded(boolean newlyAdded) {this.newlyAdded = newlyAdded; }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }
}
