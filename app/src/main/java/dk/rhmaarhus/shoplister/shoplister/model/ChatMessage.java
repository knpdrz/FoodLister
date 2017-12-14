package dk.rhmaarhus.shoplister.shoplister.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rjkey on 12-12-2017.
 */
//https://code.tutsplus.com/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397
public class ChatMessage {

    private String message;
    private String user;
    private String messageTime;

    public ChatMessage(String chatMessage, String chatUser){
        message = chatMessage;
        user = TrimUsername(chatUser);
        messageTime = GetCurrentTime();

        TrimUsername(user);
    }

    private String GetCurrentTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentdate = sdf.format(date);
        return currentdate;
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
        messageTime = time;
    }


}
