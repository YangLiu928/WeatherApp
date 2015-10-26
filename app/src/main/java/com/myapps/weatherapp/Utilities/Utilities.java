package com.myapps.weatherapp.Utilities;

import android.content.Context;

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
    public static final String FIVE_DAY_FORECAST = "/forecast/q";
    public static final String TEN_DAY_FORECAST = "/forecast10day/q";
    public static final String SLASH= "/";
    public static final String DOT_JSON =".json";
    public static final String FORECAST = "forecast";
    public static final String SIMPLE_FORECAST = "simpleforecast";
    public static final String FORECASTDAY = "forecastday";
    public static final String CONDITIONS = "conditions";
    public static final String DATE = "date";
    public static final String MONTHNAME_SHORT = "monthname_short";
    public static final String SPACE = " ";
    public static final String DAY = "day";
    public static final String HIGH = "high";
    public static final String LOW ="low";
    public static final String ICON_URL = "icon_url";

    public static JsonObject queryWeatherData(QueryData queryData, Context context) {
        try {
            StringBuilder stringBuilder = new StringBuilder(WEATHER_QUERY_URL);
            if (queryData.getDaysToDisplay()>=1 && queryData.getDaysToDisplay()<5) {
                stringBuilder.append(FIVE_DAY_FORECAST);
            }
            if (queryData.getDaysToDisplay()>=5){
                stringBuilder.append(TEN_DAY_FORECAST);
            }
            stringBuilder.append(SLASH).append(queryData.getLocation()).append(DOT_JSON);
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
            JsonArray forecastArray = jsonObject.get(FORECAST).getAsJsonObject().get(SIMPLE_FORECAST).getAsJsonObject().get(FORECASTDAY).getAsJsonArray();
            for (int i = 0; i < n; i++){
                JsonObject forecast = forecastArray.get(i).getAsJsonObject();
                result[i] = new WeatherData();
                result[i].setWeather(forecast.get(CONDITIONS).getAsString());
                result[i].setDate(forecast.get(DATE).getAsJsonObject().get(MONTHNAME_SHORT).getAsString() + SPACE + forecast.get(DATE).getAsJsonObject().get(DAY).getAsString());
                result[i].setHighTemp(forecast.get(HIGH).getAsJsonObject().get(unit.toLowerCase()).getAsString());
                result[i].setLowTemp(forecast.get(LOW).getAsJsonObject().get(unit.toLowerCase()).getAsString());
                result[i].setImageURL(new URL(forecast.get(ICON_URL).getAsString()));
                        result[i].setUnit(queryData.getUnit());
            }
            return result;
        } catch (Exception e){

        }
        return null;
    }




}
