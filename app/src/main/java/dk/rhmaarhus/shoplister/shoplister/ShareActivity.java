package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingItem;
import dk.rhmaarhus.shoplister.shoplister.model.ShoppingList;
import dk.rhmaarhus.shoplister.shoplister.model.User;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_ID;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_MEMBERS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.TAG;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.USER_INFO_NODE;

public class ShareActivity extends AppCompatActivity {
    private UsersAdapter adapter;
    private ListView listView;

    private ArrayList<User> users;

    private EditText findUserEditText;
    private Button addUserBtn;

    private DatabaseReference usersInfoDatabase;
    private DatabaseReference listMembersDatabase;
    private String shoppingListID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        findUserEditText = findViewById(R.id.addUserEditText);

        addUserBtn = findViewById(R.id.addUserBtn);
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user wants to add a new friend
                findAndAddUser(findUserEditText.getText().toString());
            }
        });

        //get data from previous activity (list id)
        Intent parentIntent = getIntent();
        shoppingListID = parentIntent.getStringExtra(LIST_ID);


        //setting users list that will be displayed in the list view
        users = new ArrayList<User>();

        //get reference to listMembers to be able to share list with others
        listMembersDatabase = FirebaseDatabase.getInstance().getReference(LIST_MEMBERS_NODE + "/" + shoppingListID);

        //setting up the list view of users (friends)
        prepareListView();
    }

    //called by list adapter, when user clicked 'share' button of a particular user
    public void shareListWithUser(User userToShareWith){
        listMembersDatabase.child(userToShareWith.getUid()).setValue(userToShareWith.getUid());
        Log.d(TAG, "!!! now we're sharing this list with "+userToShareWith.getName());
    }

    //todo this is going to be searching for user in our db
    private void findAndAddUser(String userName){
        addUserToListView(userName);
    }

    //-------------------------------------------------------------------list view management
    //adds user to list view
    private void addUserToListView(String userName){
        Log.d(TAG, "addUserToListView: adding  NOONE NOW COMMENTED"+userName);
        //TOdo
        /*users.add(new User(userName));
        adapter.notifyDataSetChanged();*/
    }

    //setting up ListView, that will display contents of users list
    private void prepareListView(){
        prepareUsersDataset();

        adapter = new UsersAdapter(this, users);
        listView = (ListView)findViewById(R.id.shareListView);
        listView.setAdapter(adapter);

    }

    //todo temporary solution
    //gets all users from firebase and set them in users arraylist
    private void prepareUsersDataset() {
        usersInfoDatabase = FirebaseDatabase.getInstance().getReference(USER_INFO_NODE);
        addUsersInfoListener();
    }

    //call this function to attach a listener to Firebase database
    //that will listen to changes in userInfo node
    private void addUsersInfoListener(){
        ChildEventListener usersInfoListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                User user = dataSnapshot.getValue(User.class);

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(firebaseUser != null){
                    //we don't won't to be friends with ourselves
                    //so don't add currently logged in user to users list
                    if(!firebaseUser.getUid().equals(user.getUid())){
                        users.add(user);
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onChildAdded: user added to users list!");
                    }
                }else{
                    Log.d(TAG, "onChildAdded: there's noone logged in- that's a problem, contact dev");
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                for(int i = 0; i < users.size(); i++ ) {
                    if(users.get(i).getUid().equals(user.getUid())) {
                        users.set(i, user);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                for(int i = 0; i < users.size(); i++ ) {
                    if(users.get(i).getUid().equals(user.getUid())) {
                        users.remove(i);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting lists failed, log a message
                Log.w(TAG, "load users:onCancelled", databaseError.toException());
                // ...
            }
        };
        usersInfoDatabase.addChildEventListener(usersInfoListener);
    }
    //--------------------------------------------------end of list management


}
