package dk.rhmaarhus.shoplister.shoplister;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import dk.rhmaarhus.shoplister.shoplister.model.User;
import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.TAG;

/**
 * Created by Moon on 30.11.2017.
 */

class UsersAdapter extends BaseAdapter{
    Context context;
    ArrayList<User> users;
    User user;

    public UsersAdapter(Context c, ArrayList<User> users){
        this.context = c;
        this.users = users;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setData(ArrayList<User> users){
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater demoInflater = (LayoutInflater)this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = demoInflater.inflate(R.layout.user_list_item, null);
        }
        user = users.get(i);
        if(user != null){
            setUsersListData(view, user);
        }

        return view;
    }

    private void setUsersListData(View view, final User selectedUser){
        TextView listNameTextView = (TextView)view.findViewById(R.id.userItemNameTextView);
        listNameTextView.setText(selectedUser.getName());

        Button shareButton = view.findViewById(R.id.userItemShareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call function of ShareActivity to share the list with selectedUser
                ((ShareActivity)context).shareListWithUser(selectedUser);
            }
        });

    }
}
