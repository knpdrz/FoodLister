package dk.rhmaarhus.shoplister.shoplister.model;

import java.util.Date;

/**
 * Created by rjkey on 12-12-2017.
 */
//https://code.tutsplus.com/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397
public class ChatMessage {

    private String message;
    private String user;
    private long messageTime;

    public ChatMessage(String chatMessage, String chatUser){
        message = chatMessage;
        user = chatUser;
        messageTime = new Date().getTime();
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

    public long getMessageTime(){
        return messageTime;
    }

    public void setMessageTime(long time){
        messageTime = time;
    }


}
