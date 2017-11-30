package dk.rhmaarhus.shoplister.shoplister;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
            setUsersListData(view);
        }

        return view;
    }

    private void setUsersListData(View view){
        TextView listNameTextView = (TextView)view.findViewById(R.id.userNameTextView);
        listNameTextView.setText(user.getName());

    }
}
