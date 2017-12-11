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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingList;
import dk.rhmaarhus.shoplister.shoplister.model.User;

import static android.icu.text.DateTimePatternGenerator.PatternInfo.OK;
import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_DETAILS_REQ_CODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_ID;
import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_NODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.SHOPPING_ITEMS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.TAG;
import static dk.rhmaarhus.shoplister.shoplister.Globals.USERS_NODE;

public class ListsActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

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

        //send authentication intent
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);


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
                    //get logged in user
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    if(firebaseUser != null){
                        User currentUser = new User(firebaseUser.getEmail(), firebaseUser.getUid());
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



    }

    private void addShoppingList(String listName, User user){
        ShoppingList shopList = new ShoppingList(listName, user);
        Log.d(TAG, "addShoppingList: adding "+listName + " owned by " + user.getName());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "user logged in! name = " + user.getEmail(), Toast.LENGTH_SHORT).show();

                //enabling the reading of lists (to which user has access to)
                addListsListener();


            } else {
                // Sign in failed, check response for error code
                // ...
                Toast.makeText(this, "login fail :<", Toast.LENGTH_SHORT).show();

            }
        }
    }

    //call this function to attach a listener to Firebase database
    //that will listen to changes in lists node
    private void addListsListener(){
        ChildEventListener listListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ShoppingList shopList = dataSnapshot.getValue(ShoppingList.class);
                shoppingLists.add(shopList);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onChildAdded: list added to lists list!");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //?
                Log.d(TAG, "onChildChanged: list list changed!");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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
        listsDatabase.addChildEventListener(listListener);
    }


}
