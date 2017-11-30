package dk.rhmaarhus.shoplister.shoplister;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingItem;

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
        ArrayList<ShoppingItem> ingredientList = new ArrayList<ShoppingItem>();
        ingredientList.add(new ShoppingItem("banana"));
        ingredientList.add(new ShoppingItem("apple"));
        ingredientList.add(new ShoppingItem("pear"));
        ingredientList.add(new ShoppingItem("risotto rice"));
        ShoppingItem item = new ShoppingItem("Caramel");
        item.flipMarked();
        ingredientList.add(item);

        shoppingItemAdapter = new ShoppingItemAdapter(this, ingredientList);
        shoppingItemListView = findViewById(R.id.shoppingItemListView);
        shoppingItemListView.setAdapter(shoppingItemAdapter);
    }
}
