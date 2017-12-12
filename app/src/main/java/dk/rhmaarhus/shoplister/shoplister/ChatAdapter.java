package dk.rhmaarhus.shoplister.shoplister;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dk.rhmaarhus.shoplister.shoplister.model.ChatMessage;

/**
 * Created by rjkey on 12-12-2017.
 */

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChatMessage> messages;
    private ChatMessage message;

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater demoInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = demoInflater.inflate(R.layout.chatmessage, null);
        }
        message = messages.get(i);
        if(message!=null){
            setMessage(view);
        }
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private void setMessage(View view){
        TextView userText = view.findViewById(R.id.userTextView);
        TextView messageText = view.findViewById(R.id.messageTextView);
        TextView timeText = view.findViewById(R.id.timeTextView);

        userText.setText(message.getUser());
        messageText.setText(message.getMessage());
        timeText.setText((int) message.getMessageTime());
    }
}
