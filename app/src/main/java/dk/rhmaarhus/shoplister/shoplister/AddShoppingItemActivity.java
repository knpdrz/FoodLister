package dk.rhmaarhus.shoplister.shoplister;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import dk.rhmaarhus.shoplister.shoplister.model.Food;
import dk.rhmaarhus.shoplister.shoplister.utility.FoodFetcher;
import dk.rhmaarhus.shoplister.shoplister.utility.Observer;
import dk.rhmaarhus.shoplister.shoplister.utility.Subject;

public class AddShoppingItemActivity extends AppCompatActivity implements Observer {

    private Button searchBtn;
    private ListView foodListView;
    private EditText searchField;
    private Subject foodSubject;
    private ArrayList<Food> foodList;
    private FoodFetcher foodFetcher;
    private AddShoppingItemAdapter adapter;
    private ArrayList<String> listOfFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_item);
        foodFetcher = new FoodFetcher(this);
        searchBtn = findViewById(R.id.searchItemBtn);
        searchField = findViewById(R.id.findFoodText);
        listOfFoods = new ArrayList<String>();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String food = searchField.getText().toString();
                if (food == null){
                    return;
                }

                SearchForFood(food, foodFetcher);
            }
        });
        foodList = new ArrayList<Food>();
        prepareListView();

        foodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Hello", "Hoe!");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void SearchForFood(String food, FoodFetcher subject) {
        this.foodSubject = subject;
        foodSubject.register(this);
        foodFetcher.GetFood(food);
    }

    @Override
    public void update(Food[] foodArray) {
        Food[] food = foodArray;
        foodList = new ArrayList<Food>(Arrays.asList(food));
        AddFoodToList(foodList);
    }

    private void prepareListView(){
        adapter = new AddShoppingItemAdapter(this, listOfFoods);
        foodListView = findViewById(R.id.searchFoodList);
        foodListView.setAdapter(adapter);
    }

    private void AddFoodToList(ArrayList<Food> foods){
        listOfFoods.clear();
        for (Food food : foods){
            listOfFoods.add(food.Name);
        }
        adapter.notifyDataSetChanged();

    }
}
