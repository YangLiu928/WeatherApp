package com.myapps.weatherapp.Models;

import java.net.URL;

/**
 * Created by YangLiu on 10/4/2015.
 */
public class ListItem {
    private String mDate;
    private String mTempRange;
    private String mWeather;
    private URL mWeatherImageURL;
    public String getDate(){
        return this.mDate;
    }
    public String getTempRange(){
        return this.mTempRange;
    }
    public String getWeather(){
        return this.mWeather;
    }
    public URL getWeatherImageURL(){return this.mWeatherImageURL;}
    public void setDate(String location){
        this.mDate = location;
    }
    public void setTempRange(String time){
        this.mTempRange = time;
    }
    public void setWeather(String subject){
        this.mWeather = subject;
    }
    public void setWeatherImageURL (URL weatherImageURL){this.mWeatherImageURL=weatherImageURL;}
    public ListItem(String date, String weather, String tempRange, URL weatherImageURL) {
        this.mDate = date;
        this.mTempRange = tempRange;
        this.mWeather = weather;
        this.mWeatherImageURL = weatherImageURL;
    }
    public ListItem(){}
}
