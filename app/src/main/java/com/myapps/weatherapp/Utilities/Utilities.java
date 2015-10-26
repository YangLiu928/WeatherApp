package com.myapps.weatherapp.Utilities;

import android.content.Context;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.myapps.weatherapp.Models.QueryData;
import com.myapps.weatherapp.Models.WeatherData;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by YangLiu on 10/24/2015.
 */
public class Utilities {
    public static final String WEATHER_QUERY_URL = "http://api.wunderground.com/api/249b924676fc85f7";





    public static JsonObject queryWeatherData(QueryData queryData, Context context) {
        try {
            StringBuilder stringBuilder = new StringBuilder(WEATHER_QUERY_URL);
            if (queryData.getDaysToDisplay()>=1 && queryData.getDaysToDisplay()<5) {
                stringBuilder.append("/forecast/q");
            }
            if (queryData.getDaysToDisplay()>=5){
                stringBuilder.append("/forecast10day/q");
            }
            stringBuilder.append("/"+queryData.getLocation()+".json");
            return Ion.with(context).load(stringBuilder.toString()).asJsonObject().get();
        } catch (Exception e) {
            return null;
        }
    }

    public static WeatherData[] parseWeatherData(JsonObject jsonObject, QueryData queryData) throws MalformedURLException {

        try{
            int n = queryData.getDaysToDisplay();
            String unit = queryData.getUnit();
            WeatherData[] result = new WeatherData[n];
            JsonArray forecastArray = jsonObject.get("forecast").getAsJsonObject().get("simpleforecast").getAsJsonObject().get("forecastday").getAsJsonArray();
            for (int i = 0; i < n; i++){
                JsonObject forecast = forecastArray.get(i).getAsJsonObject();
                result[i] = new WeatherData();
                result[i].setWeather(forecast.get("conditions").getAsString());
                result[i].setDate(forecast.get("date").getAsJsonObject().get("monthname_short").getAsString() + " " + forecast.get("date").getAsJsonObject().get("day").getAsString());
                result[i].setHighTemp(forecast.get("high").getAsJsonObject().get(unit.toLowerCase()).getAsString());
                result[i].setLowTemp(forecast.get("low").getAsJsonObject().get(unit.toLowerCase()).getAsString());
                result[i].setImageURL(new URL(forecast.get("icon_url").getAsString()));
                result[i].setUnit(queryData.getUnit());
            }
            return result;
        } catch (Exception e){

        }
        return null;
    }




}
