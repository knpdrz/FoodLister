package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
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
import com.google.firebase.database.Query;

import java.util.ArrayList;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingItem;
import dk.rhmaarhus.shoplister.shoplister.model.ShoppingList;
import dk.rhmaarhus.shoplister.shoplister.model.User;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.EMAIL_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_ID;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_MEMBERS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_NAME;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.TAG;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.USERS_LISTS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.USER_INFO_NODE;

public class ShareActivity extends AppCompatActivity {
    private UsersAdapter adapter;
    private ListView listView;

    private ArrayList<User> users;
    private ArrayList<String> membersIdsList;

    private EditText findUserEditText;
    private Button addUserButton;

    private DatabaseReference usersInfoDatabase;
    private DatabaseReference listMembersDatabase;

    private ChildEventListener lastUsersInfoListener;

    private Query emailQuery;

    private String shoppingListID;
    private String shoppingListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        findUserEditText = findViewById(R.id.searchForUserEditText);

        addUserButton = findViewById(R.id.searchForUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user wants search for a new friend to share the list with (by email)
                users.clear();
                adapter.notifyDataSetChanged();
                Log.d(TAG, "----all users should be cleared now, searching for " + findUserEditText.getText().toString());
                if(lastUsersInfoListener != null){
                    emailQuery.removeEventListener(lastUsersInfoListener);
                }
                prepareQueriedUsersDataset(findUserEditText.getText().toString());
            }
        });

        //get data from previous activity (list id)
        Intent parentIntent = getIntent();
        shoppingListID = parentIntent.getStringExtra(LIST_ID);
        shoppingListName = parentIntent.getStringExtra(LIST_NAME);


        //setting users list that will be displayed in the list view
        users = new ArrayList<User>();

        //setting list of ids of members of this list
        membersIdsList = new ArrayList<String>();

        //get reference to listMembers to be able to share list with others
        listMembersDatabase = FirebaseDatabase.getInstance().getReference(LIST_MEMBERS_NODE + "/" + shoppingListID);
        addMembersListener();

        //setting up the list view of users (friends)
        prepareListView();
    }

    //function gets called after user clicks 'search for users' button
    //it gets users whose emails start with 'emailToSearchFor'
    private void prepareQueriedUsersDataset(String emailToSearchFor) {
        usersInfoDatabase = FirebaseDatabase.getInstance().getReference(USER_INFO_NODE);
        emailQuery = usersInfoDatabase
                .orderByChild(EMAIL_NODE)
                .startAt(emailToSearchFor)
                .endAt(emailToSearchFor+"\uf8ff");

        addQueriedUsersInfoListener();
    }

    //called by list adapter, when user clicked 'share' button of a particular user
    public void shareListWithUser(User userToShareWith){
        //add member to this particular list
        listMembersDatabase.child(userToShareWith.getUid()).setValue(userToShareWith.getUid());

        //get hold of a list handler of a user that we're trying to share the list with
        ShoppingList shoppingList = new ShoppingList(shoppingListName);
        shoppingList.setFirebaseKey(shoppingListID);

        //add list to that added user's lists at usersLists/addedUserID/lists
        FirebaseDatabase
                .getInstance()
                .getReference(USERS_LISTS_NODE + "/" + userToShareWith.getUid()+"/"+LIST_NODE)
                .child(shoppingListID)
                .setValue(shoppingList);

    }


    //setting up ListView, that will display contents of users list
    private void prepareListView(){
        adapter = new UsersAdapter(this, users);
        listView = (ListView)findViewById(R.id.shareListView);
        listView.setAdapter(adapter);
    }

    //call this function to attach a listener to Firebase database
    //that will listen to changes in userInfo node
    //of users whose email match the email query
    private void addQueriedUsersInfoListener(){
        lastUsersInfoListener = new ChildEventListener() {
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
        emailQuery.addChildEventListener(lastUsersInfoListener);
    }


    //--------------------------------------------------end of list management

    // returns true if @param user is among people who have access to this list
    // used by list adapter
    public boolean userIsAFriend(User user){
        return membersIdsList.contains(user.getUid());
    }

    private void addMembersListener(){
        ChildEventListener membersListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String newUserId = (String)dataSnapshot.getValue();
                membersIdsList.add(newUserId);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //id won't change
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String removedFriendsId = dataSnapshot.getValue(String.class);
                for(int i = 0; i < membersIdsList.size(); i++ ) {
                    if(membersIdsList.get(i).equals(removedFriendsId)) {
                        //remove id from id's list
                        membersIdsList.remove(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting friends failed, log a message
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        };
        listMembersDatabase.addChildEventListener(membersListener);
    }

}
