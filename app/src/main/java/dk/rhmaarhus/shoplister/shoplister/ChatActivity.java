package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import dk.rhmaarhus.shoplister.shoplister.model.ChatMessage;

public class ChatActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private Button sendBtn;
    private EditText message;
    private ListView chatView;
    private FirebaseListAdapter<ChatMessage> adapter;
    private DatabaseReference chatDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //send authentication intent
        List<AuthUI.IdpConfig> providers = Arrays.asList(
        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

        chatDatabase = FirebaseDatabase.getInstance().getReference("chat");

        // Create and launch sign-in intent
        startActivityForResult(
        AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build(),
        RC_SIGN_IN);
        chatView = findViewById(R.id.chatView);
        displayChatMessages();
        //prepareListView();

        message = findViewById(R.id.newMessageTextView);
        sendBtn = findViewById(R.id.sendMessageBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessage chatMessage = new ChatMessage(message.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                chatDatabase.setValue(chatMessage);
                chatDatabase.push();
                message.setText("");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "user logged in! name = " + user.getEmail(), Toast.LENGTH_SHORT).show();

            } else {
                // Sign in failed, check response for error code
                // ...
                Toast.makeText(this, "login fail :<", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void prepareListView(){

    }

    private void displayChatMessages(){

    }
}
