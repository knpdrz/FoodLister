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

class ShoppingListAdapter extends BaseAdapter{
    Context context;
    ArrayList<ShoppingList> shoppingLists;
    ShoppingList shoppingList;

    public ShoppingListAdapter(Context c, ArrayList<ShoppingList> shoppingLists){
        this.context = c;
        this.shoppingLists = shoppingLists;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setData(ArrayList<ShoppingList> shoppingLists){
        this.shoppingLists = shoppingLists;
    }

    @Override
    public int getCount() {
        return shoppingLists.size();
    }

    @Override
    public Object getItem(int i) {
        return shoppingLists.get(i);
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
            view = demoInflater.inflate(R.layout.list_item, null);
        }
        shoppingList = shoppingLists.get(i);
        if(shoppingList != null){
            setShoppingListData(view);
        }

        return view;
    }

    private void setShoppingListData(View view){
        TextView listNameTextView = (TextView)view.findViewById(R.id.listNameTextView);
        listNameTextView.setText(shoppingList.getName());

    }
}
