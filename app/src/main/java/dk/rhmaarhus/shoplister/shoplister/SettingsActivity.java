package dk.rhmaarhus.shoplister.shoplister;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_ID;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_MEMBERS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.RESULT_UNFOLLOW;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.SHOPPING_ITEMS_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.USERS_LISTS_NODE;

public class SettingsActivity extends AppCompatActivity {

    private Button stopFollowingBtn;
    private DialogInterface.OnClickListener dialogClickListener;
    private DatabaseReference listMembersDatabase;
    private DatabaseReference userListsDatabase;
    private String shoppingListID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent parentIntent = getIntent();
        shoppingListID = parentIntent.getStringExtra(LIST_ID);

        listMembersDatabase = FirebaseDatabase.getInstance().getReference(LIST_MEMBERS_NODE + "/" + shoppingListID);

        stopFollowingBtn = findViewById(R.id.stopFollowingBtn);

        stopFollowingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmUnfollow();
            }
        });

        //Example taken from Stackoverflow: https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android
        dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked unfollow the list and go back to listActivity
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            userListsDatabase = FirebaseDatabase.getInstance().getReference(USERS_LISTS_NODE + "/" + firebaseUser.getUid() + LIST_NODE);
                            userListsDatabase.child(shoppingListID).removeValue();
                            listMembersDatabase.child(firebaseUser.getUid()).removeValue();
                            setResult(RESULT_UNFOLLOW);
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked, nothing to be done
                            break;
                    }
                }
        };
    }

    private void confirmUnfollow() {
        //Same example as earlier: https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmUnfollowWarning).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }
}
