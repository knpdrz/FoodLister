package dk.rhmaarhus.shoplister.shoplister;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class AddShoppingItemActivity extends AppCompatActivity {

    private Button searchBtn;
    private ListView foodListView;
    private EditText searchField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_item);

        searchBtn = findViewById(R.id.searchItemBtn);
        foodListView = findViewById(R.id.searchFoodList);
        searchField = findViewById(R.id.findFoodText);
    }

    @Override
    protected void onStart() {
        super.onStart();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchForFood();
            }
        });

    }

    private void SearchForFood() {

    }
}
