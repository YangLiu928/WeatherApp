package com.myapps.weatherapp.Models;

import java.net.URL;

/**
 * Created by YangLiu on 10/24/2015.
 */
public class WeatherData {
    private String mWeather;
    private String mHighTemp;
    private String mLowTemp;
    private URL mImageURL;
    private String mDate;
    private String mUnit;
    public WeatherData(String weather, String highTemp, String lowTemp, URL imageURL,String date,String unit){
        mWeather=weather;
        mHighTemp = highTemp;
        mLowTemp = lowTemp;
        mImageURL = imageURL;
        mDate = date;
        mUnit = unit;
    }
    public WeatherData(){}
    public String getUnit(){return this.mUnit;}
    public String getDate(){return this.mDate;}
    public String getWeather(){
        return this.mWeather;
    }
    public String getHighTemp(){
        return this.mHighTemp;
    }
    public String getLowTemp(){
        return this.mLowTemp;
    }
    public URL getImageURL(){
        return this.mImageURL;
    }
    public void setUnit(String unit){this.mUnit=unit;}
    public void setWeather(String weather){this.mWeather = weather;}
    public void setHighTemp(String highTemp) {this.mHighTemp = highTemp;}
    public void setLowTemp(String lowTemp){this.mLowTemp=lowTemp;}
    public void setImageURL(URL imageURL){this.mImageURL=imageURL;}
    public void setDate(String date){this.mDate=date;}
}
