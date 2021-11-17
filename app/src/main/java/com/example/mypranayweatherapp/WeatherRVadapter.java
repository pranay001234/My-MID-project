package com.example.mypranayweatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVadapter extends RecyclerView.Adapter<WeatherRVadapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRvmodel> weatherRvmodelArrayList;

    public WeatherRVadapter(Context context, ArrayList<WeatherRvmodel> weatherRvmodelArrayList) {
        this.context = context;
        this.weatherRvmodelArrayList = weatherRvmodelArrayList;
    }

    @NonNull
    @Override
    public WeatherRVadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item1,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVadapter.ViewHolder holder, int position) {
        //
        WeatherRvmodel model = weatherRvmodelArrayList.get(position);
        holder.TVtemperature.setText(model.getTemperature() + "°c");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.IVcondition);
        holder.TVwind.setText(model.getWindSpeed()+"km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(model.getTime());
            holder.TVtime.setText(output.format(t));
        } catch (ParseException e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherRvmodelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView TVwind,TVtemperature,TVtime;
        private ImageView IVcondition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVwind = itemView.findViewById(R.id.idTVWindSpeed);
            TVtemperature = itemView.findViewById(R.id.idTVTemperature);
            TVtime = itemView.findViewById(R.id.idTVTime);
            IVcondition =itemView.findViewById(R.id.idIVCondition);
        }
    }
}
