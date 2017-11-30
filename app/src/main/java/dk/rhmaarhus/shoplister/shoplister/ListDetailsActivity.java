package dk.rhmaarhus.shoplister.shoplister;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class ListDetailsActivity extends AppCompatActivity {

    ShoppingItemAdapter shoppingItemAdapter;
    ListView shoppingItemListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);
        
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
