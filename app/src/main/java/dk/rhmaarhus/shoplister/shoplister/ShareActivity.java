package dk.rhmaarhus.shoplister.shoplister;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import static dk.rhmaarhus.shoplister.shoplister.Globals.TAG;

public class ShareActivity extends AppCompatActivity {
    private UsersAdapter adapter;
    private ListView listView;

    private ArrayList<User> users;

    private EditText findUserEditText;
    private Button addUserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        findUserEditText = findViewById(R.id.addUserEditText);

        addUserBtn = findViewById(R.id.addUserBtn);
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user wants to add a new friend
                findAndAddUser(findUserEditText.getText().toString());
            }
        });

        //setting users list that will be displayed in the list view
        users = new ArrayList<User>();

        //todo- temp only
        //adding dummy data to users list
        users.add(new User("Angelo"));
        users.add(new User("Frank"));
        users.add(new User("Toby"));
        users.add(new User("Michael"));


        //setting up the list view of users (friends)
        prepareListView();
    }

    //todo this is going to be searching for user in our db
    private void findAndAddUser(String userName){
        addUserToListView(userName);
    }


    //adds user to list view
    private void addUserToListView(String userName){
        Log.d(TAG, "addUserToListView: adding "+userName);
        users.add(new User(userName));
        adapter.notifyDataSetChanged();
    }

    //-------------------------------------------------------------------list view management
    //setting up ListView, that will display contents of users list
    private void prepareListView(){
        adapter = new UsersAdapter(this, users);
        listView = (ListView)findViewById(R.id.shareListView);
        listView.setAdapter(adapter);

    }

    //--------------------------------------------------end of list management
}
