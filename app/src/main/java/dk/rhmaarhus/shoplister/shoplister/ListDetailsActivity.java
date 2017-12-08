package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_DETAILS_REQ_CODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_ID;
import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_NODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.SHARE_SCREEN_REQ_CODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.SHOPPING_ITEMS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.Globals.TAG;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingItem;
import dk.rhmaarhus.shoplister.shoplister.model.ShoppingList;

public class ListDetailsActivity extends AppCompatActivity {

    private ShoppingItemAdapter shoppingItemAdapter;
    private ListView shoppingItemListView;

    private String shoppingListID;
    private TextView shoppingListNameTextView;

    private Button shareBtn, addIngredientBtn;

    ArrayList<ShoppingItem> ingredientList;

    private DatabaseReference shoppingItemDatabase;
    private DatabaseReference listsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        shareBtn = findViewById(R.id.shareBtn);
        addIngredientBtn = findViewById(R.id.addIngredientBtn);
        shoppingListNameTextView = findViewById(R.id.shoppingListNameTextView);
        ingredientList = new ArrayList<ShoppingItem>();

        Intent parentIntent = getIntent();
        shoppingListID = parentIntent.getStringExtra(LIST_ID);

        shoppingListNameTextView = findViewById(R.id.shoppingListNameTextView);
        listsDatabase = FirebaseDatabase.getInstance().getReference(LIST_NODE);
        shoppingItemDatabase = FirebaseDatabase.getInstance().getReference(SHOPPING_ITEMS_NODE + "/" + shoppingListID);

        ValueEventListener listListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String name = dataSnapshot.getValue(ShoppingList.class).name;
                shoppingListNameTextView.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        listsDatabase.addValueEventListener(listListener);

        shoppingItemAdapter = new ShoppingItemAdapter(this, ingredientList, shoppingListID);
        shoppingItemListView = findViewById(R.id.shoppingItemListView);
        shoppingItemListView.setAdapter(shoppingItemAdapter);

        ChildEventListener shoppingItemListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ShoppingItem item = dataSnapshot.getValue(ShoppingItem.class);
                ingredientList.add(item);
                shoppingItemAdapter.notifyDataSetChanged();
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
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        shoppingItemDatabase.addChildEventListener(shoppingItemListener);

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

}
