package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static dk.rhmaarhus.shoplister.shoplister.Globals.LIST_NAME;
import static dk.rhmaarhus.shoplister.shoplister.Globals.SHARE_SCREEN_REQ_CODE;

public class ListDetailsActivity extends AppCompatActivity {

    private ShoppingItemAdapter shoppingItemAdapter;
    private ListView shoppingItemListView;

    private String shoppingListName;
    private TextView shoppingListNameTextView;

    private Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        shareButton = findViewById(R.id.shareButton);
        shoppingListNameTextView = findViewById(R.id.shoppingListNameTextView);

        //retrieve name of the list that will be displayed in the activity
        Intent parentIntent = getIntent();
        shoppingListName = parentIntent.getStringExtra(LIST_NAME);
        shoppingListNameTextView.setText(shoppingListName);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openShareActivityIntent =
                        new Intent(getApplicationContext(), ShareActivity.class);
                openShareActivityIntent.putExtra(LIST_NAME, shoppingListName);
                startActivityForResult(openShareActivityIntent, SHARE_SCREEN_REQ_CODE);
            }
        });
        shoppingListNameTextView = findViewById(R.id.shoppingListNameTextView);


        prepareIngredientsList();


    }

    private void prepareIngredientsList() {
        ArrayList<String> ingredientList = new ArrayList<String>();
        ingredientList.add("banana");
        ingredientList.add("apple");
        ingredientList.add("risotto rice");
        ingredientList.add("Ben and Jerry's");

        shoppingItemAdapter = new ShoppingItemAdapter(this, ingredientList);
        shoppingItemListView = findViewById(R.id.shoppingItemListView);
        shoppingItemListView.setAdapter(shoppingItemAdapter);
    }
}
