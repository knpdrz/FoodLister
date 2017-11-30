package dk.rhmaarhus.shoplister.shoplister;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hulda on 30.11.2017.
 */

public class IngredientsAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> ingredients;
    String ingredient;

    public IngredientsAdapter(Context c, ArrayList<String> ingredients){
        this.context = c;
        this.ingredients = ingredients;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setData(ArrayList<String> ingredients){
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
            view = demoInflater.inflate(R.layout.ingredients_item, null);
        }
        ingredient = ingredients.get(position);
        if(ingredient!=null && ingredient != ""){
            setIngredientData(view);
        }

        return view;
    }

    private void setIngredientData(View view){
        TextView ingredientItemTextView = (TextView)view.findViewById(R.id.ingredientItemTextView);
        ingredientItemTextView.setText(ingredient);

    }
}
