package dk.rhmaarhus.shoplister.shoplister;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingList;
import dk.rhmaarhus.shoplister.shoplister.model.User;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_DETAILS_REQ_CODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_ID;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_MEMBERS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_NAME;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.TAG;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.USERS_LISTS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.USER_INFO_NODE;

public class ListsActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    private ShoppingListAdapter adapter;
    private ListView listView;

    private ArrayList<ShoppingList> shoppingLists;

    private EditText shoppingListEditText;
    private FloatingActionButton addShoppingListBtn;

    //firebase instance variables
    private DatabaseReference userListsDatabase;
    private DatabaseReference usersInfoDatabase;
    private ChildEventListener listListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //service
    private NotificationService notificationService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        //initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();

        shoppingListEditText = findViewById(R.id.newListEditText);

        addShoppingListBtn = findViewById(R.id.addShoppingListBtn);

        addShoppingListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user wants to add a new shopping list
                String newListName = shoppingListEditText.getText().toString();
                if(newListName != null && !newListName.isEmpty()){
                    //get logged in user
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    if(firebaseUser != null){
                        User currentUser = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getUid(),null);
                        addShoppingList(newListName, currentUser);


                    }else{
                        Toast.makeText(ListsActivity.this, "no user is logged in!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


        //setting shopping lists list that will be displayed in the list view
        shoppingLists = new ArrayList<ShoppingList>();

        //setting up the list view of shopping list
        prepareListView();

        //based on https://classroom.udacity.com/courses/ud0352
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //user is signed in
                    onSignedInInitialize(user);
                }else{
                    //user is signed out
                    onSignedOutCleanup();
                   // Create and launch sign-in intent
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .setLogo(R.mipmap.ic_launcher)
                                    .setIsSmartLockEnabled(false)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        //starting notification service
        Intent serviceIntent = new Intent(getApplicationContext(), NotificationService.class);
        FirebaseUser fbUser = firebaseAuth.getInstance().getCurrentUser();

        if(fbUser != null) {
            User currentUser = new User(fbUser.getDisplayName(), fbUser.getEmail(), fbUser.getUid(), null);

            String UserAsJson = SerializeUserObject(currentUser);

            serviceIntent.putExtra("user", UserAsJson);
            startService(serviceIntent);

            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }

    }

    private String SerializeUserObject(User currentUser) {
        Gson gson = new Gson();
        String userAsJson = gson.toJson(currentUser);

        return userAsJson;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            NotificationService.LocalBinder binder = (NotificationService.LocalBinder)iBinder;

            notificationService = binder.getService();

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //todo remove this bit?
            /*case R.id.action_user:
                Log.d(TAG, "User icon was clicked and should be logged out");
                //Reference, code taken from https://firebase.google.com/docs/auth/android/firebaseui
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                //todo open login again
                            }
                        });
                return true;*/
            case R.id.log_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void addShoppingList(String listName, User user){
        ShoppingList shopList = new ShoppingList(listName);
        Log.d(TAG, "addShoppingList: adding "+listName + " owned by " + user.getName());

        shopList.setFirebaseKey(userListsDatabase.push().getKey());
        userListsDatabase.child(shopList.getFirebaseKey()).setValue(shopList);
        shoppingListEditText.getText().clear();

        //add user to listMembers node in firebase database
        FirebaseDatabase
                .getInstance()
                .getReference(LIST_MEMBERS_NODE + "/" + shopList.getFirebaseKey())
                .child(user.getUid())
                .setValue(user.getUid());

    }

    //-------------------------------------------------------------------list view management
    //setting up ListView, that will display contents of shoppingLists
    //clicking on a shopping list results in opening details for that list
    private void prepareListView(){
        adapter = new ShoppingListAdapter(this, shoppingLists);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ShoppingList clickedShoppingList = shoppingLists.get(position);


                Log.d(TAG,"MainActivity: opening details activity for "+clickedShoppingList.getName());

                Intent openListDetailsIntent =
                        new Intent(getApplicationContext(), ListDetailsActivity.class);
                openListDetailsIntent.putExtra(LIST_ID, clickedShoppingList.getFirebaseKey());
                openListDetailsIntent.putExtra(LIST_NAME, clickedShoppingList.getName());
                startActivityForResult(openListDetailsIntent, LIST_DETAILS_REQ_CODE);
            }
        });
    }

    //--------------------------------------------------end of list management


    //call this function to attach a listener to Firebase database
    //that will listen to changes in lists node
    private void attachListsListener(){
        if(listListener == null) {
            listListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    ShoppingList shopList = dataSnapshot.getValue(ShoppingList.class);
                    shoppingLists.add(shopList);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "onChildAdded: list added to lists list!");
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged: not handled yet");
                    //Will not happen

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    for (ShoppingList list : shoppingLists) {
                        //todo check, is not working. Are the keys the same?
                        if (list.getFirebaseKey().equals(dataSnapshot.getKey())) {
                            shoppingLists.remove(list);
                        }
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting lists failed, log a message
                    Log.w(TAG, "load lists:onCancelled", databaseError.toException());
                    // ...
                }
            };

            userListsDatabase.addChildEventListener(listListener);
        }
    }

    private void detachListsListener(){
        //detach db listener
        if(listListener != null){
            userListsDatabase.removeEventListener(listListener);
            listListener = null;
        }
    }
    private void onSignedOutCleanup() {
        detachListsListener();

        shoppingLists.clear();
        adapter.notifyDataSetChanged();
    }

    private void onSignedInInitialize(FirebaseUser firebaseUser) {
        //todo enable this
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //add (or update) the user in usersInfo node in Firebase
        usersInfoDatabase = FirebaseDatabase.getInstance().getReference(USER_INFO_NODE);
        Uri userPhotoUrl = firebaseUser.getPhotoUrl();

        String userPhotoString = (userPhotoUrl == null) ? null : userPhotoUrl.toString();

        User user = new User(firebaseUser.getDisplayName(),
                firebaseUser.getEmail(),
                firebaseUser.getUid(),
                userPhotoString);

        usersInfoDatabase.child(firebaseUser.getUid()).setValue(user);

        //get reference to firebase database
        userListsDatabase = FirebaseDatabase.getInstance().getReference(USERS_LISTS_NODE +"/"+firebaseUser.getUid()+"/"+LIST_NODE);

        //enabling the reading of lists (to which user has access to)
        attachListsListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "user logged in! name = " + user.getEmail(), Toast.LENGTH_SHORT).show();

                onSignedInInitialize(user);
            } else {
                // Sign in failed, check response for error code
                // ...
                Toast.makeText(this, "login fail :<", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        detachListsListener();
        shoppingLists.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onDestroy() {
        if(mConnection != null && mBound){
            unbindService(mConnection);
        }
        super.onDestroy();

    }
}
