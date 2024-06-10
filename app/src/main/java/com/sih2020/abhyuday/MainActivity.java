package com.sih2020.abhyuday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.sih2020.abhyuday.Adapters.FeaturedAdapter;
import com.sih2020.abhyuday.Adapters.FeaturedHelperClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;


public class MainActivity extends AppCompatActivity {

    RecyclerView featuredRecycler;
    RecyclerView.Adapter adapter;
    ArrayList<FeaturedHelperClass> featuredNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        featuredRecycler=findViewById(R.id.featured_recycler);
        featuredRecycler();

    }

    private void featuredRecycler() {
        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        featuredNews=new ArrayList<>();

        //Get Covid data data from api
        //String url = "https://disease.sh/v3/covid-19/gov/india";
        //String url = "https://disease.sh/v3/covid-19/all";
        String url = "https://disease.sh/v3/covid-19/countries?sort=cases";


        Log.e("DEBUG","Covid request");
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e("DEBUG","Covid response");

                        // Handle the JSON object and
                        // handle it inside try and catch
                        try {

                            // Creating object of JSONObject
                            JSONArray jsonArray = new JSONArray(response);
                            Log.e("DEBUG",response.toString());
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                //Log.e("DEBUG",jsonObject.toString());
                                String country=jsonObject.getString("country");

                                float cases=jsonObject.getLong("cases");
                                String number=truncateNumber(cases);
                                String flag=jsonObject.getJSONObject("countryInfo").getString("flag");
                                Log.e("DEBUG",flag);

                                featuredNews.add(new FeaturedHelperClass(flag,country+" Count","In "+country+" count of positive cases exceeds "+number));

                            }


//
//                            featuredNews.add(new FeaturedHelperClass(R.drawable.global_logo,"Global Count","The Global count of positive cases exceeds "+number));
//                            featuredNews.add(new FeaturedHelperClass(R.drawable.india_logo,"India Count","In India the count of positive cases exceeds 1M"));
//
                            adapter =new FeaturedAdapter(featuredNews);
                            featuredRecycler.setAdapter(adapter);
                            // Set the data in text view
                            // which are available in JSON format
                            // Note that the parameter inside
                            // the getString() must match
                            // with the name given in JSON format

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue
                = Volley.newRequestQueue(this);
        requestQueue.add(request);



        Log.e("DEBUG","Executed till main 1");

        Log.e("DEBUG","Executed till main 2");


    }

    public String truncateNumber(float floatNumber) {
        long million = 1000000L;
        long billion = 1000000000L;
        long trillion = 1000000000000L;
        long number = Math.round(floatNumber);
        if ((number >= million) && (number < billion)) {
            float fraction = calculateFraction(number, million);
            return Float.toString(fraction) + "M";
        } else if ((number >= billion) && (number < trillion)) {
            float fraction = calculateFraction(number, billion);
            return Float.toString(fraction) + "B";
        }
        return Long.toString(number);
    }

    public float calculateFraction(long number, long divisor) {
        long truncate = (number * 10L + (divisor / 2L)) / divisor;
        float fraction = (float) truncate * 0.10F;
        return fraction;
    }
}
