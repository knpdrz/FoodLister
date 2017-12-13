package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dk.rhmaarhus.shoplister.shoplister.model.Food;
import dk.rhmaarhus.shoplister.shoplister.model.ShoppingItem;
import dk.rhmaarhus.shoplister.shoplister.utility.FoodFetcher;
import dk.rhmaarhus.shoplister.shoplister.utility.Observer;
import dk.rhmaarhus.shoplister.shoplister.utility.Subject;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_ID;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.SHOPPING_ITEMS_NODE;

public class AddShoppingItemActivity extends AppCompatActivity implements Observer {

    private Button searchBtn, saveItemsBtn, cancelItemsBtn;
    private ListView foodListView;
    private EditText searchField;
    private Subject foodSubject;
    private ArrayList<Food> foodList;
    private FoodFetcher foodFetcher;
    private AddShoppingItemAdapter adapter;
    private ArrayList<String> listOfFoods;
    private ArrayList<ShoppingItem> itemToShoppingList;
    private Map<String, Boolean> hasBeenClicked;
    private String shoppingListID;
    private DatabaseReference shoppingItemDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_item);
        foodFetcher = new FoodFetcher(this);
        searchBtn = findViewById(R.id.searchItemBtn);
        saveItemsBtn = findViewById(R.id.saveItemsBtn);
        cancelItemsBtn = findViewById(R.id.cancelItemsBtn);
        searchField = findViewById(R.id.findFoodText);
        listOfFoods = new ArrayList<String>();
        itemToShoppingList = new ArrayList<ShoppingItem>();
        hasBeenClicked = new HashMap<String, Boolean>();

        //get data from previous activity (list id)
        Intent parentIntent = getIntent();
        shoppingListID = parentIntent.getStringExtra(LIST_ID);

        shoppingItemDatabase = FirebaseDatabase.getInstance().getReference(SHOPPING_ITEMS_NODE + "/" + shoppingListID);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String food = searchField.getText().toString();
                if (food == null){
                    return;
                }

                searchForFood(food, foodFetcher);
            }
        });

        saveItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSelectedItems();

                finish();
            }
        });

        cancelItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        foodList = new ArrayList<Food>();
        prepareListView();

        foodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView listNameTextView = view.findViewById(R.id.foodTextView);
                String name = listNameTextView.getText().toString();

                click(name, listNameTextView);

                ShoppingItem item = new ShoppingItem(name);
                itemToShoppingList.add(item);
            }

        });
    }

    //saving selected items to the database
    private void saveSelectedItems() {
        for(ShoppingItem shoppingItem : itemToShoppingList) {
            shoppingItemDatabase.child(shoppingItem.getName()).setValue(new ShoppingItem(shoppingItem.getName()));
        }
    }

    private void click(String name, TextView listNameTextView) {
        Boolean value = hasBeenClicked.get(name);
        if (value){
            hasBeenClicked.put(name, false);
            listNameTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));

            ShoppingItem item = new ShoppingItem(name);
            itemToShoppingList.remove(item);
        }
        else{
            hasBeenClicked.put(name, true);
            listNameTextView.setBackgroundColor(Color.parseColor("#5882FA"));

            ShoppingItem item = new ShoppingItem(name);
            itemToShoppingList.add(item);
        }
    }


    private void searchForFood(String food, FoodFetcher subject) {
        this.foodSubject = subject;
        foodSubject.register(this);
        foodFetcher.GetFood(food);
    }

    @Override
    public void update(Food[] foodArray) {
        Food[] food = foodArray;
        foodList = new ArrayList<Food>(Arrays.asList(food));
        AddFoodToLists(foodList);
    }

    private void prepareListView(){
        adapter = new AddShoppingItemAdapter(this, listOfFoods);
        foodListView = findViewById(R.id.searchFoodList);
        foodListView.setAdapter(adapter);
    }

    private void AddFoodToLists(ArrayList<Food> foods){
        listOfFoods.clear();
        hasBeenClicked.clear();
        for (Food food : foods){
            listOfFoods.add(food.Name);
            hasBeenClicked.put(food.Name, false);
        }
        adapter.notifyDataSetChanged();
    }
}
