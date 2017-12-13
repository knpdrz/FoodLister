package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingItem;
import dk.rhmaarhus.shoplister.shoplister.model.ShoppingList;
import dk.rhmaarhus.shoplister.shoplister.model.User;

import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_ID;
import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_MEMBERS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_NAME;
import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_NODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.SHARE_SCREEN_REQ_CODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.SHOPPING_ITEMS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.TAG;

public class ListDetailsActivity extends AppCompatActivity {

    private ShoppingItemAdapter shoppingItemAdapter;
    private ListView shoppingItemListView;

    private String shoppingListID;
    private String shoppingListName;

    private TextView shoppingListNameTextView;

    private Button shareBtn, addIngredientBtn;

    ArrayList<ShoppingItem> ingredientList;
    ArrayList<User> friendsList;

    private DatabaseReference shoppingItemDatabase;
    private DatabaseReference friendsDatabase;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter friendsAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        shareBtn = findViewById(R.id.shareBtn);
        addIngredientBtn = findViewById(R.id.addIngredientBtn);
        shoppingListNameTextView = findViewById(R.id.shoppingListNameTextView);

        ingredientList = new ArrayList<ShoppingItem>();
        friendsList = new ArrayList<User>();

        //getting list details from main activity (ListsActivity)
        Intent parentIntent = getIntent();
        shoppingListID = parentIntent.getStringExtra(LIST_ID);
        shoppingListName = parentIntent.getStringExtra(LIST_NAME);

        shoppingListNameTextView.setText(shoppingListName);

        shoppingItemAdapter = new ShoppingItemAdapter(this, ingredientList, shoppingListID);
        shoppingItemListView = findViewById(R.id.shoppingItemListView);
        shoppingItemListView.setAdapter(shoppingItemAdapter);

        //get reference to firebase database with shopping list items
        shoppingItemDatabase = FirebaseDatabase.getInstance().getReference(SHOPPING_ITEMS_NODE + "/" + shoppingListID);
        addShoppingItemsListener();


        //and reference to people who share this particular list
        friendsDatabase = FirebaseDatabase.getInstance().getReference(LIST_MEMBERS_NODE + "/" + shoppingListID);
        addFriendsListener();
        prepareRecyclerView();

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openShareActivityIntent =
                        new Intent(getApplicationContext(), ShareActivity.class);
                openShareActivityIntent.putExtra(LIST_ID, shoppingListID);
                startActivityForResult(openShareActivityIntent, SHARE_SCREEN_REQ_CODE);
            }
        });

        addIngredientBtn.setOnClickListener(new View.OnClickListener() {
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

    private void addFriendsListener(){
        ChildEventListener friendsListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                User item = dataSnapshot.getValue(User.class);
                friendsList.add(item);

                //todo does it work?
                friendsAdapter.notifyDataSetChanged();
                Log.d(TAG, "onChildAdded: adding friend " + item.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User item = dataSnapshot.getValue(User.class);
                for(int i = 0; i < friendsList.size(); i++ ) {
                    if(friendsList.get(i).getName().equals(item.getName())) {
                        friendsList.set(i, item);
                        break;
                    }
                }
                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User item = dataSnapshot.getValue(User.class);
                for(int i = 0; i < friendsList.size(); i++ ) {
                    if(friendsList.get(i).getName().equals(item.getName())) {
                        friendsList.remove(i);
                        break;
                    }
                }
                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting friends failed, log a message
                Log.w(TAG, "load friend:onCancelled", databaseError.toException());
                // ...
            }
        };
        friendsDatabase.addChildEventListener(friendsListener);

    }

}
