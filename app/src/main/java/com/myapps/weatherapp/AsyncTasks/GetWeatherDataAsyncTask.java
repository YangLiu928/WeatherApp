package com.myapps.weatherapp.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.myapps.weatherapp.Acticities.DisplayWeatherActivity;
import com.myapps.weatherapp.Models.QueryData;
import com.myapps.weatherapp.Models.WeatherData;
import com.myapps.weatherapp.R;
import com.myapps.weatherapp.Utilities.Utilities;

import java.net.MalformedURLException;

/**
 * Created by YangLiu on 10/23/2015.
 */
public class GetWeatherDataAsyncTask extends AsyncTask<QueryData, Integer, WeatherData[]>{
    private Context mContext;
    private LoadWeatherDataCompletionListener mLoadWeatherDataCompletionListener;
    private ProgressDialog mProgressDialog;
    public interface LoadWeatherDataCompletionListener{
        void WeatherDataLoaded(WeatherData[] weatherDataArray);
        void WeatherDataFailedToLoad();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage(mContext.getString(R.string.progress_loading_weather_data));
        mProgressDialog.show();
    }

    @Override
    protected WeatherData[] doInBackground(QueryData... params) {
        JsonObject result;
        QueryData queryData = params[0];
        result = Utilities.queryWeatherData(queryData, mContext);


        try {
            return Utilities.parseWeatherData(result,queryData);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //need a constructor to bring in the context
    public GetWeatherDataAsyncTask(Context context, LoadWeatherDataCompletionListener loadWeatherDataCompletionListener){
        mContext=context;
        mLoadWeatherDataCompletionListener = loadWeatherDataCompletionListener;
        mProgressDialog=new ProgressDialog(context);
    }

    @Override
    protected void onPostExecute(WeatherData[] weatherDataArray) {
        super.onPostExecute(weatherDataArray);
        mProgressDialog.dismiss();
        if (mLoadWeatherDataCompletionListener !=null){
            if(weatherDataArray==null){
                mLoadWeatherDataCompletionListener.WeatherDataFailedToLoad();
            }else{
                mLoadWeatherDataCompletionListener.WeatherDataLoaded(weatherDataArray);
            }
        }
    }
}
