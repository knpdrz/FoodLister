package dk.rhmaarhus.shoplister.shoplister;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingItem;

import static dk.rhmaarhus.shoplister.shoplister.utility.Globals.SHOPPING_ITEMS_NODE;

/**
 * Created by hulda on 30.11.2017.
 */

public class ShoppingItemAdapter extends BaseAdapter {
    Context context;
    ArrayList<ShoppingItem> ingredients;
    ShoppingItem ingredient;
    public String shoppingListID;

    private DatabaseReference shoppingItemDatabase;

    public ShoppingItemAdapter(Context c, ArrayList<ShoppingItem> ingredients, String shoppingListID){
        this.context = c;
        this.ingredients = ingredients;
        this.shoppingListID = shoppingListID;

        shoppingItemDatabase = FirebaseDatabase.getInstance().getReference(SHOPPING_ITEMS_NODE + "/" + shoppingListID);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setData(ArrayList<ShoppingItem> ingredients){
        this.ingredients = ingredients;
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public Object getItem(int i) {
        return ingredients.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater demoInflater = (LayoutInflater)this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = demoInflater.inflate(R.layout.shopping_item, null);
        }
        ingredient = ingredients.get(position);
        if(ingredient!=null){
            setIngredientData(view);
        }

        final TextView shoppingItemTextView = view.findViewById(R.id.shoppingItemTextView);

        return view;
    }

    @SuppressLint("ResourceAsColor")
    private void setIngredientData(View view){
        TextView shoppingItemTextView = (TextView)view.findViewById(R.id.shoppingItemTextView);
        shoppingItemTextView.setText(ingredient.getName());
        if(ingredient.getMarked()) {
            shoppingItemTextView.setPaintFlags(0);
            shoppingItemTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            shoppingItemTextView.setTextColor(R.color.colorPrimary);
        }
        else {
            shoppingItemTextView.setPaintFlags(0);
            shoppingItemTextView.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
            shoppingItemTextView.setTextColor(Color.BLACK);

        }
    }
}
