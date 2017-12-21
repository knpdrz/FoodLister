package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingItem;
import dk.rhmaarhus.shoplister.shoplister.model.User;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_ID;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_MEMBERS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_NAME;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.RESULT_UNFOLLOW;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.SETTINGS_REQ_CODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.SHARE_SCREEN_REQ_CODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.SHOPPING_ITEMS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.TAG;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.USER_INFO_NODE;

public class ListDetailsActivity extends AppCompatActivity {

    private ShoppingItemAdapter shoppingItemAdapter;
    private ListView shoppingItemListView;

    private String shoppingListID;
    private String shoppingListName;

    private TextView shoppingListNameTextView;

    private Button addIngredientBtn, clearBtn, chatBtn;
    private FloatingActionButton addFabBtn;

    ArrayList<ShoppingItem> ingredientList;
    ArrayList<String> friendsIdsList;
    ArrayList<User> friendsList;

    private DatabaseReference shoppingItemDatabase;
    private DatabaseReference friendsIdsDatabase;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter friendsAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        //Display the back arrow todo??
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        clearBtn = findViewById(R.id.clearBtn);
        chatBtn = findViewById(R.id.chatBtn);
        addFabBtn = findViewById(R.id.addFabBtn);
        shoppingListNameTextView = findViewById(R.id.shoppingListNameTextView);

        ingredientList = new ArrayList<ShoppingItem>();
        friendsIdsList = new ArrayList<String>();
        friendsList = new ArrayList<User>();

        //getting list details from main activity (ListsActivity)
        Intent parentIntent = getIntent();
        shoppingListID = parentIntent.getStringExtra(LIST_ID);
        shoppingListName = parentIntent.getStringExtra(LIST_NAME);

        shoppingListNameTextView.setText(shoppingListName);

        //Preparing the ingredients list view
        prepareList();

        //get reference to firebase database with shopping list items
        shoppingItemDatabase = FirebaseDatabase.getInstance().getReference(SHOPPING_ITEMS_NODE + "/" + shoppingListID);
        addShoppingItemsListener();

        //and reference to ids of people who share this particular list
        friendsIdsDatabase = FirebaseDatabase.getInstance().getReference(LIST_MEMBERS_NODE + "/" + shoppingListID);
        addFriendsIdsListener();
        prepareRecyclerView();


        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(ShoppingItem shoppingItem : ingredientList) {
                    if(shoppingItem.getMarked()) {
                        shoppingItemDatabase.child(shoppingItem.getName()).removeValue();
                    }
                }
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                chatIntent.putExtra(LIST_ID, shoppingListID);
                startActivity(chatIntent);
            }
        });

        addFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prepareIngredientsList();
                Intent addShoppingItemIntent =
                        new Intent(getApplicationContext(), AddShoppingItemActivity.class);
                addShoppingItemIntent.putExtra(LIST_ID, shoppingListID);
                startActivity(addShoppingItemIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent openSettingsActivityIntent =
                        new Intent(getApplicationContext(), SettingsActivity.class);
                openSettingsActivityIntent.putExtra(LIST_ID, shoppingListID);
                openSettingsActivityIntent.putExtra(LIST_NAME, shoppingListName);
                startActivityForResult(openSettingsActivityIntent, SETTINGS_REQ_CODE);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void prepareList() {
        shoppingItemAdapter = new ShoppingItemAdapter(this, ingredientList, shoppingListID);
        shoppingItemListView = findViewById(R.id.shoppingItemListView);
        shoppingItemListView.setAdapter(shoppingItemAdapter);

        shoppingItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Log.d(TAG,"Details activity, marking " + ingredientList.get(position).getName());

                ingredientList.get(position).flipMarked();

                shoppingItemDatabase.child(ingredientList.get(position).getName()).setValue(ingredientList.get(position));
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    private void prepareRecyclerView(){
        //set recycler view for scrollable friends list
        //based on https://developer.android.com/training/material/lists-cards.html
        recyclerView = (RecyclerView) findViewById(R.id.friendsRecyclerView);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);

        // specifying an adapter
        friendsAdapter = new FriendsAdapter(friendsList);
        recyclerView.setAdapter(friendsAdapter);

    }

    private void addShoppingItemsListener(){
        ChildEventListener shoppingItemListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ShoppingItem item = dataSnapshot.getValue(ShoppingItem.class);
                ingredientList.add(item);
                shoppingItemAdapter.notifyDataSetChanged();
                Log.d(TAG, "onChildAdded: adding list item: " + item.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ShoppingItem item = dataSnapshot.getValue(ShoppingItem.class);
                for(int i = 0; i < ingredientList.size(); i++ ) {
                    if(ingredientList.get(i).getName().equals(item.getName())) {
                        ingredientList.set(i, item);
                        break;
                    }
                }
                shoppingItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ShoppingItem item = dataSnapshot.getValue(ShoppingItem.class);
                for(int i = 0; i < ingredientList.size(); i++ ) {
                    if(ingredientList.get(i).getName().equals(item.getName())) {
                        ingredientList.remove(i);
                        break;
                    }
                }
                shoppingItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting shopping items failed, log a message
                Log.w(TAG, "load item:onCancelled", databaseError.toException());
                // ...
            }
        };
        shoppingItemDatabase.addChildEventListener(shoppingItemListener);

    }

    private void addFriendsIdsListener(){
        ChildEventListener friendsIdsListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String newUserId = (String)dataSnapshot.getValue();

                //we don't want to display currently logged in user as friend
                //so get currently logged in user
                //check if the one we got from firebase is that one
                //and if they are NOT the same person, add them to friends id's list
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(firebaseUser != null){
                    if(!firebaseUser.getUid().equals(newUserId)){
                        friendsIdsList.add(newUserId);
                        addFriendListener(newUserId);
                    }
                }else{
                    Log.d(TAG, "onChildAdded: noone is logged in, that shouldn't happen, contact dev");
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //will never be called- the id's won't change
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String removedFriendsId = dataSnapshot.getValue(String.class);
                for(int i = 0; i < friendsIdsList.size(); i++ ) {
                    if(friendsIdsList.get(i).equals(removedFriendsId)) {
                        //remove id from id's list
                        friendsIdsList.remove(i);
                        //and friend from friends list
                        for(int j = 0; j < friendsList.size(); j++ ) {
                            if(friendsList.get(j).getUid().equals(removedFriendsId)) {
                                friendsList.remove(j);
                                break;
                            }
                        }
                        break;
                    }
                }
                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting friends failed, log a message
                Log.w(TAG, "load friend:onCancelled", databaseError.toException());
            }
        };
        friendsIdsDatabase.addChildEventListener(friendsIdsListener);
    }

    private void addFriendListener(String friendId){
        ValueEventListener friendEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // username or user photo of this particular friend changed
                // so find them on friendsList and update the value
                User changedFriend = dataSnapshot.getValue(User.class);

                boolean friendExisted = false;
                for(int i = 0; i < friendsList.size(); i++ ) {
                    if(friendsList.get(i).getUid().equals(changedFriend.getUid())) {
                        friendsList.set(i, changedFriend);
                        friendExisted = true;
                        break;
                    }
                }

                if(!friendExisted){
                    //we are adding a new friend
                    friendsList.add(changedFriend);
                }
                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //get reference to that particular friend info in the db
        //and add a value listener to it
        FirebaseDatabase
                .getInstance()
                .getReference(USER_INFO_NODE + "/" + friendId)
                .addValueEventListener(friendEventListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_REQ_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_UNFOLLOW) {
                //Check if there is noyone left watching the list, then possible to delete it
                if(friendsIdsList.size() == 0) {
                    shoppingItemDatabase.removeValue();
                }
                //Need to close the list since it's not followed anymore
                finish();
            }
        }
    }

}
