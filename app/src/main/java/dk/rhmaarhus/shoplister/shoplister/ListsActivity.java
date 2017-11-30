package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static dk.rhmaarhus.shoplister.shoplister.Globals.TAG;

public class ListsActivity extends AppCompatActivity {
    private ShoppingListAdapter adapter;
    private ListView listView;

    private ArrayList<ShoppingList> shoppingLists;

    private EditText shoppingListEditText;
    private Button addShoppingListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        shoppingListEditText = findViewById(R.id.newListEditText);

        addShoppingListButton = findViewById(R.id.addShoppingListButton);
        addShoppingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user wants to add a new shopping list
                String newListName = shoppingListEditText.getText().toString();
                if(newListName != null && !newListName.isEmpty()){
                    addShoppingList(newListName);
                }

            }
        });

        //setting shopping lists list that will be displayed in the list view
        shoppingLists = new ArrayList<ShoppingList>();

        //todo- temp only
        //adding dummy data to shopping lists list
        shoppingLists.add(new ShoppingList("MyList1"));
        shoppingLists.add(new ShoppingList("MyList2"));
        shoppingLists.add(new ShoppingList("MyList3"));
        shoppingLists.add(new ShoppingList("Party list"));
        shoppingLists.add(new ShoppingList("For kids"));



        //setting up the list view of shopping list
        prepareListView();
    }

    private void addShoppingList(String listName){
        Log.d(TAG, "addShoppingList: adding "+listName);
        shoppingLists.add(new ShoppingList(listName));
        adapter.notifyDataSetChanged();
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
                String clickedShoppingListName = shoppingLists.get(position).getName();
                Log.d(TAG,"MainActivity: opening details activity for "+clickedShoppingListName);

                /*
                //todo
                Intent openListDetailsIntent =
                        new Intent(getApplicationContext(), CityDetailsActivity.class);
                openCityDetailsIntent.putExtra(CITY_NAME, clickedCityString);
                openCityDetailsIntent.putExtra(CWD_OBJECT, cityWeatherData);
                startActivityForResult(openCityDetailsIntent, CITY_DETAILS_REQ_CODE);
*/
            }
        });
    }

    //--------------------------------------------------end of list management
}
