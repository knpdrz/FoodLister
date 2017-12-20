package dk.rhmaarhus.shoplister.shoplister;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.bumptech.glide.Glide;

import dk.rhmaarhus.shoplister.shoplister.model.User;

/**
 * Created by Moon on 13.12.2017.
 */
//based on https://developer.android.com/training/material/lists-cards.html
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{
    private ArrayList<User> friendsDataset;

    //based on http://tech.umanlife.com/2017/03/06/implement-an-horizontal-listview.html
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView friendNameTextView;
        public ImageView friendImageView;

        public ViewHolder(View v) {
            super(v);
            friendNameTextView = v.findViewById(R.id.friendNameTextView);
            friendImageView = v.findViewById(R.id.friendImageView);
        }
    }

    public FriendsAdapter(ArrayList<User> friendsDataset){
        this.friendsDataset = friendsDataset;
    }


    //create new views- invoked by the layout manager
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_item_view, parent, false);
        //todo set view's size, margins, paddings and layout params here if needed
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder holder, int position) {
        // - get element from friends dataset at this position
        // - replace the contents of the view with that element
        User user = friendsDataset.get(position);
        holder.friendNameTextView.setText(user.getName());

        //based on https://github.com/udacity/and-nd-firebase
        //set user photo, if they have any
        boolean photoAvailable = user.getImageUrl() != null;
        if(photoAvailable){
            Glide.with(holder.friendImageView.getContext())
                    .load(user.getImageUrl())
                    .into(holder.friendImageView);
        }else{
            //user doesn't have a photo, set a default one
            holder.friendImageView.setImageResource(R.drawable.icon_user);

        }
    }

    @Override
    public int getItemCount() {
        return friendsDataset.size();
    }
}
