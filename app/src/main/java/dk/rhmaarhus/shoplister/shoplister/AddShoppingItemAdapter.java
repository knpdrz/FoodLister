package dk.rhmaarhus.shoplister.shoplister;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rjkey on 07-12-2017.
 */

public class AddShoppingItemAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> foods;
    String food;

    public AddShoppingItemAdapter(Context c, ArrayList<String> foodLists){
        this.context = c;
        this.foods = foodLists;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setData(ArrayList<String> foods){
        this.foods = foods;
    }
    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int i) {
        return foods.get(i);
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
            view = demoInflater.inflate(R.layout.food_item, null);
        }
        food = foods.get(i);
        if(food!=null){
            setFoodData(view, food);
        }
        return view;
    }

    private void setFoodData(View view, String food) {
        TextView listNameTextView =view.findViewById(R.id.foodTextView);
        listNameTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        listNameTextView.setText(food);
    }
}
