package dk.rhmaarhus.shoplister.shoplister;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingList;
import dk.rhmaarhus.shoplister.shoplister.model.User;
import dk.rhmaarhus.shoplister.shoplister.utility.NotificationHelper;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.USERS_LISTS_NODE;

/**
 * Created by rjkey on 21-12-2017.
 */

public class NotificationService extends IntentService {

    private DatabaseReference usersList;
    private NotificationHelper notificationHelper;
    private final IBinder mBinder = new LocalBinder();
    private User user;

    public NotificationService(){
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper(this);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle!=null){
            String userAsJson = (String)bundle.get("user");
            user = DeserializeJsonString(userAsJson);
        }
        else {
            return START_NOT_STICKY;
        }

        usersList = FirebaseDatabase.getInstance().getReference(USERS_LISTS_NODE + "/" + user.getUid()+ "/" +  LIST_NODE);

        addUsersListListener();

        return START_STICKY;
    }

    private void addUsersListListener() {
        ChildEventListener usersListListener = new ChildEventListener(){

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ShoppingList list = dataSnapshot.getValue(ShoppingList.class);
                NotifyUserAboutNewList(list.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersList.addChildEventListener(usersListListener);
    }

    private void NotifyUserAboutNewList(String nameOfNewList) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentdate = sdf.format(date);

        String notificationText = "You were added to the foodlist " + nameOfNewList + currentdate;
        notificationHelper.CreateNotification(getResources()
                .getString(R.string.app_name), notificationText);
    }

    private User DeserializeJsonString(String userAsJson) {
        Gson gson = new Gson();
        User user = gson.fromJson(userAsJson, User.class);

        return user;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        NotificationService getService(){
            return NotificationService.this;
        }
    }
}
