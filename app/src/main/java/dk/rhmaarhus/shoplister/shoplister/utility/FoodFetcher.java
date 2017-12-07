package dk.rhmaarhus.shoplister.shoplister.utility;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dk.rhmaarhus.shoplister.shoplister.model.Food;

/**
 * Created by rjkey on 30-11-2017.
 */

public class FoodFetcher implements Subject {

    private String APIUrl = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/food/ingredients/" +
            "autocomplete?&metaInformation=false&number=10&query=";
    private String MashapeKey = "X-Mashape-Key"; //Header Key
    private String MashapeHost = "X-Mashape-Host"; //Header Key
    private String MashapeKeyValue = "58I2ZEiFNZmshHTQLPkiMoCuxEApp1v7qGQjsnjGYgsy8VzF5V"; //Header Value
    private String MashapeHostValue = "spoonacular-recipe-food-nutrition-v1.p.mashape.com"; //Header Value
    private RequestQueue queue;
    private ArrayList<Observer> observers;

    public FoodFetcher(Context context){
        queue = Volley.newRequestQueue(context);
        observers = new ArrayList<>();
    }

    public void GetFood(String food){
        RequestFoodFromAPI(food);
    }

    private void RequestFoodFromAPI(String food) {
        String foodUrl = APIUrl + food;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, foodUrl,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Food[] food = ParseJsonToFood(response);

                if (food != null){
                    notifyObserver(food);
                }
                else{
                    //SHIT! Food is null!!
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Handle error
            }
        }){
            //https://stackoverflow.com/questions/17049473/how-to-set-custom-header-in-volley-request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(MashapeKey, MashapeKeyValue);
                params.put(MashapeHost, MashapeHostValue);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private Food[] ParseJsonToFood(String jsonString){
        Gson gson = new Gson();
        Food[] food = gson.fromJson(jsonString, Food[].class);
        return food;
    }

    @Override
    public void register(Observer newObserver) {
        observers.add(newObserver);
    }

    @Override
    public void unregister(Observer deleteObserver) {
        int observerIndex = observers.indexOf(deleteObserver);
        observers.remove(observerIndex);
    }

    @Override
    public void notifyObserver(Food[] foodList) {
        for (Observer observer : observers){
            observer.update(foodList);
        }
    }
}
