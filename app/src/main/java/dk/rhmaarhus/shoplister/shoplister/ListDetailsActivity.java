package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingItem;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_ID;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_NAME;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.SHARE_SCREEN_REQ_CODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.SHOPPING_ITEMS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.TAG;

public class ListDetailsActivity extends AppCompatActivity {

    private ShoppingItemAdapter shoppingItemAdapter;
    private ListView shoppingItemListView;

    private String shoppingListID;
    private String shoppingListName;

    private TextView shoppingListNameTextView;

    private Button shareBtn, addIngredientBtn, clearBtn;

    ArrayList<ShoppingItem> ingredientList;

    private DatabaseReference shoppingItemDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        shareBtn = findViewById(R.id.shareBtn);
        addIngredientBtn = findViewById(R.id.addIngredientBtn);
        clearBtn = findViewById(R.id.clearBtn);
        shoppingListNameTextView = findViewById(R.id.shoppingListNameTextView);
        ingredientList = new ArrayList<ShoppingItem>();

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

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(ShoppingItem shoppingItem : ingredientList) {
                    if(shoppingItem.getMarked()) {
                        //todo delete from the database and then make sure that onChildDeleted is implemented too
                    }
                }
            }
        });
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

}
