package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingList;

import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_DETAILS_REQ_CODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_ID;
import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_NODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.SHOPPING_ITEMS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.TAG;

public class ListsActivity extends AppCompatActivity {
    private ShoppingListAdapter adapter;
    private ListView listView;

    private ArrayList<ShoppingList> shoppingLists;

    private EditText shoppingListEditText;
    private Button addShoppingListBtn;

    private DatabaseReference listsDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // Write a message to the database
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        listsDatabase = FirebaseDatabase.getInstance().getReference(LIST_NODE);

        shoppingListEditText = findViewById(R.id.newListEditText);

        addShoppingListBtn = findViewById(R.id.addShoppingListBtn);
        addShoppingListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user wants to add a new shopping list
                String newListName = shoppingListEditText.getText().toString();
                if(newListName != null && !newListName.isEmpty()){
                    addShoppingList(newListName);
                }

            }
        });

        ChildEventListener listListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ShoppingList shopList = dataSnapshot.getValue(ShoppingList.class);
                shoppingLists.add(shopList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //?
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        listsDatabase.addChildEventListener(listListener);

        //setting shopping lists list that will be displayed in the list view
        shoppingLists = new ArrayList<ShoppingList>();


        //setting up the list view of shopping list
        prepareListView();

    }

    private void addShoppingList(String listName){
        ShoppingList shopList = new ShoppingList(listName);
        Log.d(TAG, "addShoppingList: adding "+listName);

        shopList.setFirebaseKey(listsDatabase.push().getKey());
        listsDatabase.child(shopList.getFirebaseKey()).setValue(shopList);
        shoppingListEditText.getText().clear();
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
                String clickedShoppingListID = shoppingLists.get(position).getFirebaseKey();
                Log.d(TAG,"MainActivity: opening details activity for "+clickedShoppingListID);

                Intent openListDetailsIntent =
                        new Intent(getApplicationContext(), ListDetailsActivity.class);
                openListDetailsIntent.putExtra(LIST_ID, clickedShoppingListID);
                startActivityForResult(openListDetailsIntent, LIST_DETAILS_REQ_CODE);
            }
        });
    }

    //--------------------------------------------------end of list management
}
