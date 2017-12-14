package dk.rhmaarhus.shoplister.shoplister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dk.rhmaarhus.shoplister.shoplister.model.ChatMessage;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.CHAT_NODE;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.LIST_ID;

public class ChatActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private Button sendBtn;
    private EditText message;
    private ListView chatView;
    private FirebaseListAdapter<ChatMessage> adapter;
    private DatabaseReference chatDatabase;
    private String shoppingListID;
    private String shoppingListName;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messages = new ArrayList<ChatMessage>();

        Intent parentIntent = getIntent();
        shoppingListID = parentIntent.getStringExtra(LIST_ID);

        prepareListView();

        chatDatabase = FirebaseDatabase.getInstance().getReference(CHAT_NODE + "/" + shoppingListID);
        addChatListener();

        message = (EditText)findViewById(R.id.newMessageTextView);
        sendBtn = findViewById(R.id.sendMessageBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessage chatMessage = new ChatMessage(message.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                String key = String.valueOf(chatMessage.getMessageTime()+FirebaseAuth.getInstance().getCurrentUser().getUid());
                chatDatabase.child(key).setValue(chatMessage);
                message.setText("");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void prepareListView(){
        chatAdapter = new ChatAdapter(this, messages);
        chatView = findViewById(R.id.chatView);
        chatView.setAdapter(chatAdapter);
    }

    private void displayChatMessages(){

    }

    private void addChatListener() {
        ChildEventListener chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                messages.add(message);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        chatDatabase.addChildEventListener(chatListener);
    }
}
