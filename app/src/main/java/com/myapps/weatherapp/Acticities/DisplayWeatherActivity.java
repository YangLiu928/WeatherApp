package com.myapps.weatherapp.Acticities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.myapps.weatherapp.AsyncTasks.GetWeatherDataAsyncTask;
import com.myapps.weatherapp.Models.ListItem;
import com.myapps.weatherapp.Models.MyAdapter;
import com.myapps.weatherapp.Models.QueryData;
import com.myapps.weatherapp.Models.WeatherData;
import com.myapps.weatherapp.R;
import com.myapps.weatherapp.Utilities.LocationFinder;

import java.util.ArrayList;

public class DisplayWeatherActivity extends AppCompatActivity implements GetWeatherDataAsyncTask.LoadWeatherDataCompletionListener,LocationFinder.LocationDetector{
    private Button mSettingsButton;
    private Location mLocation;
    private LocationFinder mLocationFinder;
    private QueryData mQueryData;
    private SharedPreferences mSharedPreference;
    private final String TAG = "WeatherActivityTAG";
    private static final int LOCATION_ACCESS_REQUEST_CODE = 1;
    private boolean mShouldRefreshData = true;
    private ArrayList<WeatherData> mWeatherDataArrayList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);
        if (savedInstanceState!=null){
            this.mShouldRefreshData=savedInstanceState.getBoolean(getString(R.string.previous_should_refresh_data_key));
            this.mWeatherDataArrayList= savedInstanceState.getParcelableArrayList(getString(R.string.previous_weather_data_array_key));
            if (mWeatherDataArrayList.size()!=0){
                WeatherData[] temp = new WeatherData[mWeatherDataArrayList.size()];
                for (int i = 0; i <temp.length;i++){
                    temp[i] = mWeatherDataArrayList.get(i);
                }
                populateListView(temp);
            }
        }
        mSettingsButton = (Button) findViewById(R.id.settings_button);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayWeatherActivity.this, SettingsActivity.class);
                startActivity(intent);
                DisplayWeatherActivity.this.mShouldRefreshData = true;
                Log.d(TAG,"should refresh set to true due to click");
            }
        });
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.location_permission_title)
                    .setMessage(R.string.location_permission_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DisplayWeatherActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_ACCESS_REQUEST_CODE);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("previousShouldRefreshData", mShouldRefreshData);
        outState.putParcelableArrayList("previousWeatherDataArray", mWeatherDataArrayList);
    }

    @Override
    public void WeatherDataLoaded(WeatherData[] weatherDataArray) {
        populateListView(weatherDataArray);
        Toast myToast = Toast.makeText(this, R.string.weather_data_loaded_toast,Toast.LENGTH_SHORT);
        myToast.show();
    }

    @Override
    public void WeatherDataFailedToLoad() {
        Toast myToast = Toast.makeText(this, R.string.weather_data_failed_to_load_toast,Toast.LENGTH_LONG);
        myToast.show();
    }

    public void populateListView(WeatherData[] weatherDataArray){

        ListView weatherListing =
                (ListView) findViewById(R.id.weather_listing);
        ArrayList<ListItem> array = new ArrayList<>();
        this.mWeatherDataArrayList.clear();
        for (WeatherData aWeatherDataArray : weatherDataArray) {
            ListItem item = new ListItem();
            item.setWeather(aWeatherDataArray.getWeather());
            item.setTempRange(aWeatherDataArray.getLowTemp() + " ~ " + aWeatherDataArray.getHighTemp() + " " + aWeatherDataArray.getUnit());
            item.setDate(aWeatherDataArray.getDate());
            item.setWeatherImageURL(aWeatherDataArray.getImageURL());
            array.add(item);
            this.mWeatherDataArrayList.add(aWeatherDataArray);
        }
        ArrayAdapter<ListItem> adapter = new MyAdapter(DisplayWeatherActivity.this, R.layout.list_item,array);
        if (weatherListing!=null) {
            weatherListing.setAdapter(adapter);
        }
        //TODO: how do I want to handle the null pointer exception..? Toast with text unfriendly to users? ("weather listing is null!!")
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mShouldRefreshData){
            mShouldRefreshData=false;
            mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            mQueryData = new QueryData(Integer.parseInt(mSharedPreference.getString(this.getString(R.string.preference_key_number_of_days),this.getString(R.string.preference_number_of_days_default_value))),
                    mSharedPreference.getString(this.getString(R.string.preference_key_metric),this.getString(R.string.preference_metric_unit_default_fahrenheit_text)),
                    mSharedPreference.getString(this.getString(R.string.preference_key_location),this.getString(R.string.preference_location_default_value)));
            if (mQueryData.getLocation()==null||mQueryData.getLocation().trim().equals("")){
                LocationFinder mLocationFinder = new LocationFinder(this,this);
                mLocationFinder.detectLocation();
            } else {
                GetWeatherDataAsyncTask task = new GetWeatherDataAsyncTask(this,this);
                task.execute(mQueryData);
            }
        }
    }

    @Override
    public void locationFound(Location location) {
        String coordinate = location.getLatitude()+","+location.getLongitude();
        mQueryData = new QueryData(Integer.parseInt(mSharedPreference.getString(this.getString(R.string.preference_key_number_of_days),this.getString(R.string.preference_number_of_days_default_value))),
                mSharedPreference.getString(this.getString(R.string.preference_key_metric),this.getString(R.string.preference_metric_unit_default_fahrenheit_text)),coordinate);
        GetWeatherDataAsyncTask task = new GetWeatherDataAsyncTask(DisplayWeatherActivity.this,DisplayWeatherActivity.this);
        task.execute(mQueryData);
    }

    @Override
    public void locationNotFound(LocationFinder.FailureReason failureReason) {
        if (failureReason== LocationFinder.FailureReason.NO_PERMISSION){
            Toast.makeText(this, R.string.location_detection_no_permission_exception,Toast.LENGTH_LONG).show();
        } else if (failureReason== LocationFinder.FailureReason.TIMEOUT){
            Toast.makeText(this, R.string.location_not_found_time_out_toast,Toast.LENGTH_LONG).show();
        } else if (failureReason==LocationFinder.FailureReason.GPS_TURNED_OFF){
            Toast.makeText(this, R.string.location_not_found_gps_turned_off,Toast.LENGTH_LONG).show();
        }
    }
}
