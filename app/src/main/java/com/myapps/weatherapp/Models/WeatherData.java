package com.myapps.weatherapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by YangLiu on 10/24/2015.
 */
public class WeatherData implements Parcelable{
    private String mWeather;
    private String mHighTemp;
    private String mLowTemp;
    private URL mImageURL;
    private String mDate;
    private String mUnit;
    public WeatherData(String weather, String highTemp, String lowTemp, URL imageURL,String date,String unit) {
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

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    public WeatherData(Parcel in) throws MalformedURLException {
        this.mDate=in.readString();
        this.mHighTemp=in.readString();
        this.mLowTemp=in.readString();
        this.mUnit=in.readString();
        this.mWeather=in.readString();
        this.mImageURL=new URL(in.readString());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDate);
        dest.writeString(mHighTemp);
        dest.writeString(mLowTemp);
        dest.writeString(mUnit);
        dest.writeString(mWeather);
        dest.writeString(mImageURL.toString());
    }

    private static final Parcelable.Creator<WeatherData> CREATOR = new Parcelable.Creator<WeatherData>(){

        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
         *
         * @param source The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        @Override
        public WeatherData createFromParcel(Parcel source) {
            try {
                return new WeatherData(source);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Create a new array of the Parcelable class.
         *
         * @param size Size of the array.
         * @return Returns an array of the Parcelable class, with every entry
         * initialized to null.
         */
        @Override
        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };



}
