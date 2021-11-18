package com.example.mypranayweatherapp;

import static android.location.LocationManager.NETWORK_PROVIDER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
//import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

//import org.bouncycastle.crypto.tls.CipherType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private RelativeLayout RLhome;
    private ProgressBar PBloading;
    private TextView TVcityname, TVtemperature, TVcondition;
    private RecyclerView RVweather;
    private TextInputEditText CityEdt;
    private ImageView IVblack, IVicon, IVsearch;
    private ArrayList<WeatherRvmodel> weatherRvmodelArrayList;
    private WeatherRVadapter weatherRVadapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String CityName;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        setContentView(R.layout.activity_main);
        RLhome = findViewById(R.id.idRLHome);
        PBloading = findViewById(R.id.idPBloading);
        TVcityname = findViewById(R.id.idTVCityName);
        TVtemperature = findViewById(R.id.idTVTemperature);
        TVcondition = findViewById(R.id.idTVCondition);
        RVweather = findViewById(R.id.idRVWeather);
        CityEdt = findViewById(R.id.idEdtCity);
        IVicon = findViewById(R.id.idIVIcon);
        IVblack = findViewById(R.id.idIVBlack);
        IVsearch = findViewById(R.id.idIVSearch);
        weatherRvmodelArrayList = new ArrayList<>();
        weatherRVadapter = new WeatherRVadapter(this, weatherRvmodelArrayList);
        RVweather.setAdapter(weatherRVadapter);




        IVsearch .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String City = CityEdt.getText().toString();
              if(City.isEmpty()){
                  Toast.makeText(MainActivity.this, "Plesae enter city name", Toast.LENGTH_SHORT).show();
              } else{
                  TVcityname.setText(CityName);
                  getWeatherinfo(City);
              }
        }
    });


     }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE) {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "Please Provide Permission..", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }




    private String getCityName(double longitude, double latitude) {
        String CityName = "Not found";
        Geocoder gcd= new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address>  addresses = gcd.getFromLocation(latitude,longitude,10);

            for (Address adr : addresses){
                if(adr!=null){
                    String city =adr.getLocality();
                    if(city!=null && !city.equals("")){
                        CityName = city;
                    } else {
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this, "User city not found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return CityName;
    }

    private void getWeatherinfo(String cityName){
        String url = "https://api.weatherapi.com/v1/forecast.json?key=25d9e22bfa044d38881144212211010&q=" + cityName + "&days=1&aqi=no&alerts=no";
        TVcityname.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PBloading.setVisibility(View.GONE);
                RLhome.setVisibility(View.VISIBLE);
                weatherRvmodelArrayList.clear();


                try {
                    String Temperature = response.getJSONObject("current").getString("temp_c");
                    TVtemperature.setText(Temperature+"°c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(IVicon);
                    TVcondition.setText(condition);
                    if(isDay==1){
                        Picasso.get().load("https://www.theweathernetwork.com/us/photos/view/25865/fallen-leaves/35742322").into(IVblack);
                    } else{
                        Picasso.get().load("https://www.theweathernetwork.com/us/photos/view/25865/thursday-evening-sunset/35742292").into(IVblack);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forcastO =forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forcastO.getJSONArray("hour");

                    for(int i=0; i<hourArray.length(); i++){
                        JSONObject hourobj =hourArray.getJSONObject(i);
                        String time = hourobj.getString("time");
                        String temper = hourobj.getString("temp_c");
                        String imag = hourobj.getJSONObject("condition").getString("icon");
                        String wind = hourobj.getString("wind_kmp");
                        weatherRvmodelArrayList.add(new WeatherRvmodel(time,temper, imag, wind));
                    }
                    weatherRVadapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name...", Toast.LENGTH_SHORT).show();
            }

        });
        requestQueue.add(jsonObjectRequest);


    }
}


