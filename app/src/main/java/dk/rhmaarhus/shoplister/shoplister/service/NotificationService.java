package dk.rhmaarhus.shoplister.shoplister.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dk.rhmaarhus.shoplister.shoplister.R;
import dk.rhmaarhus.shoplister.shoplister.model.ChatMessage;
import dk.rhmaarhus.shoplister.shoplister.model.ShoppingItem;
import dk.rhmaarhus.shoplister.shoplister.model.ShoppingList;
import dk.rhmaarhus.shoplister.shoplister.model.User;
import dk.rhmaarhus.shoplister.shoplister.utility.NotificationHelper;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.CHAT_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.SHOPPING_ITEMS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.USERS_LISTS_NODE;

/**
 * Created by rjkey on 21-12-2017.
 */

public class NotificationService extends IntentService {

    private DatabaseReference usersList;
    private NotificationHelper notificationHelper;
    private final IBinder mBinder = new LocalBinder();
    private User user;
    private ArrayList<String> listsToTrack;
    private Map<DatabaseReference, String> chatListeners;
    private Map<DatabaseReference, String> itemListeners;
    private String currentUser;

    public NotificationService(){
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper(this);
        listsToTrack = new ArrayList<String>();
        chatListeners = new HashMap<DatabaseReference, String>();
        itemListeners = new HashMap<DatabaseReference, String>();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String userAsJson = (String) bundle.get("user");
                user = DeserializeJsonString(userAsJson);
            }else{
                return START_NOT_STICKY;
            }
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
                if(list.getNewlyAdded()) {
                    NotifyUserAboutNewList(list.getName());
                    list.setNewlyAdded(false);
                    usersList.child(dataSnapshot.getKey()).setValue(list);
                }
                listsToTrack.add(list.getFirebaseKey());

                CreateChatChildListenerForShoppingList(list.getFirebaseKey(), list.getName());
                CreateShoppingItemListenerForShoppingList(list.getFirebaseKey(), list.getName());
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

    private void CreateShoppingItemListenerForShoppingList(String firebaseKey, String name) {
        DatabaseReference itemListener = FirebaseDatabase.getInstance().getReference(SHOPPING_ITEMS_NODE + "/" + firebaseKey);
        addItemListener(itemListener);
        itemListeners.put(itemListener, name);
    }

    private void addItemListener(final DatabaseReference itemListener) {
        ChildEventListener itemEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ShoppingItem item = dataSnapshot.getValue(ShoppingItem.class);
                if (item.getNewlyAdded() && currentUser != item.getUid()){
                    item.setNewlyAdded(false);
                    itemListener.child(dataSnapshot.getKey()).setValue(item);
                    String listname = itemListeners.get(itemListener);
                    NotifyUserAboutItemsInShoppingList(item.getName(), listname, "Add");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                ShoppingItem item = dataSnapshot.getValue(ShoppingItem.class);
                if (currentUser != item.getUid()){
                    String listname = itemListeners.get(itemListener);
                    NotifyUserAboutItemsInShoppingList(item.getName(), listname, "Change");
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ShoppingItem item = dataSnapshot.getValue(ShoppingItem.class);
                if (currentUser != item.getUid()){
                    String listname = itemListeners.get(itemListener);
                    NotifyUserAboutItemsInShoppingList(item.getName(), listname, "Remove");
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        itemListener.addChildEventListener(itemEventListener);
    }

    private void NotifyUserAboutItemsInShoppingList(String itemname, String listname, String type) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentdate = sdf.format(date);
        String notificationText="";

        switch (type){
            case "Add":
                notificationText = itemname + " " + getText(R.string.addedToShoppingList) + " " + listname + " " + getText(R.string.at) + " " + currentdate;
                break;
            case "Change":
                notificationText = getText(R.string.changesToAShoppingList)  + " " + listname + " " + getText(R.string.at) + " " + currentdate;
                break;
            case "Remove":
                notificationText = itemname + " " + getText(R.string.removedFromShoppingList) + " " + listname + " " + getText(R.string.at) + " " + currentdate;
                break;
        }

        notificationHelper.CreateNotification(getResources()
                .getString(R.string.app_name), notificationText);
    }

    private void NotifyUserAboutNewList(String nameOfNewList) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentdate = sdf.format(date);

        String notificationText = getText(R.string.youWereAdded) + " " + nameOfNewList + " " +  currentdate;
        notificationHelper.CreateNotification(getResources()
                .getString(R.string.app_name), notificationText);
    }

    private void CreateChatChildListenerForShoppingList(String firebaseKey, String listname) {
        DatabaseReference chatListener = FirebaseDatabase.getInstance().getReference(CHAT_NODE + "/" + firebaseKey);
        addChatListener(chatListener);
        chatListeners.put(chatListener, listname);
    }

    private void addChatListener(final DatabaseReference chatListener) {
        ChildEventListener chatEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                if (message.getNewlyAdded() && currentUser != message.getUid()){
                    message.setNewlyAdded(false);
                    chatListener.child(dataSnapshot.getKey()).setValue(message);
                    String listname = chatListeners.get(chatListener);
                    NotifyUserAboutNewChatMessage(message.getUser(), listname);
                }

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
        chatListener.addChildEventListener(chatEventListener);
    }

    private void NotifyUserAboutNewChatMessage(String user, String listname) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentdate = sdf.format(date);

        String notificationText = user + " " + getText(R.string.wroteMessage) + " " + listname + " " + getText(R.string.chat) + " " + getText(R.string.at) + " " + currentdate;
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
        public NotificationService getService(){
            return NotificationService.this;
        }
    }
}
