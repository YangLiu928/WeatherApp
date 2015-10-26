package com.myapps.weatherapp.Models;

/**
 * Created by YangLiu on 10/24/2015.
 */
public class QueryData {
    private int mDaysToDisplay;
    private String mUnit;
    private String mLocation;
    public int getDaysToDisplay(){
        return this.mDaysToDisplay;
    }
    public String getUnit(){
        return this.mUnit;
    }
    public String getLocation(){
        return this.mLocation;
    }
    public void setDaysToDisplay (int daysToDisplay){
        this.mDaysToDisplay = daysToDisplay;
    }
    public void setUnit (String unit){
        this.mUnit=unit;
    }
    public void setLocation(String location){
        this.mLocation=location;
    }
    public QueryData(int daysToDisplay, String unit, String location){
        this.mDaysToDisplay=daysToDisplay;
        this.mLocation=location;
        this.mUnit=unit;
    }
    public QueryData(){}
}
