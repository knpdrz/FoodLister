package dk.rhmaarhus.shoplister.shoplister;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import dk.rhmaarhus.shoplister.shoplister.model.ShoppingItem;

/**
 * Created by hulda on 30.11.2017.
 */

public class ShoppingItemAdapter extends BaseAdapter {
    Context context;
    ArrayList<ShoppingItem> ingredients;
    ShoppingItem ingredient;

    public ShoppingItemAdapter(Context c, ArrayList<ShoppingItem> ingredients){
        this.context = c;
        this.ingredients = ingredients;
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater demoInflater = (LayoutInflater)this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = demoInflater.inflate(R.layout.shopping_item, null);
        }
        ingredient = ingredients.get(position);
        if(ingredient!=null){
            setIngredientData(view);
        }

        return view;
    }

    private void setIngredientData(View view){
        TextView shoppingItemTextView = (TextView)view.findViewById(R.id.shoppingItemTextView);
        shoppingItemTextView.setText(ingredient.getName());
        CheckBox boughtCheckBox = view.findViewById(R.id.boughtCheckBox);
        if(boughtCheckBox.isChecked() != ingredient.getMarked()) {
            boughtCheckBox.toggle();
        }
    }
}
