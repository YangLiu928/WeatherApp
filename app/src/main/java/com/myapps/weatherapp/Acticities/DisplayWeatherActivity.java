package com.myapps.weatherapp.Acticities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        Log.d(TAG,"on create, now should referesh is"+mShouldRefreshData);
        setContentView(R.layout.activity_display_weather);
        if (savedInstanceState!=null){
            this.mShouldRefreshData=savedInstanceState.getBoolean("previousShouldRefreshData");
            this.mWeatherDataArrayList= savedInstanceState.getParcelableArrayList("previousWeatherDataArray");
            Log.d(TAG, "loadsavedinstance"+String.valueOf(mWeatherDataArrayList.size()));
            WeatherData[] temp = new WeatherData[mWeatherDataArrayList.size()];
            for (int i = 0; i <temp.length;i++){
                temp[i] = mWeatherDataArrayList.get(i);
            }
            Log.d(TAG, "temp is in size of " + temp.length);
            populateListView(temp);
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
        Log.d(TAG, "onsavecalled1");
        super.onSaveInstanceState(outState);
        outState.putBoolean("previousShouldRefreshData", mShouldRefreshData);
        outState.putParcelableArrayList("previousWeatherDataArray", mWeatherDataArrayList);
        Log.d(TAG,"onsavecalled"+String.valueOf(mWeatherDataArrayList.size()));
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
        for (int i = 0; i < weatherDataArray.length;i++){
            ListItem item = new ListItem();
            item.setWeather(weatherDataArray[i].getWeather());
            item.setTempRange(weatherDataArray[i].getLowTemp() + " ~ " + weatherDataArray[i].getHighTemp() + " " + weatherDataArray[i].getUnit());
            item.setDate(weatherDataArray[i].getDate());
            item.setWeatherImageURL(weatherDataArray[i].getImageURL());
            array.add(item);
            this.mWeatherDataArrayList.add(weatherDataArray[i]);
        }
        ArrayAdapter<ListItem> adapter = new MyAdapter(DisplayWeatherActivity.this, R.layout.list_item,array);
        Log.d(TAG,"adapter = null "+ String.valueOf(adapter==null) + "weatherlisting==null"+String.valueOf(weatherListing==null));
        weatherListing.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mShouldRefreshData){
            mShouldRefreshData=false;
            mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            mQueryData = new QueryData(Integer.parseInt(mSharedPreference.getString("numberOfDays","3")),mSharedPreference.getString("metricUnit","Celsius"),mSharedPreference.getString("location","22304"));
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
        mQueryData = new QueryData(Integer.parseInt(mSharedPreference.getString("numberOfDays", "3")),mSharedPreference.getString("metricUnit","Celsius"),coordinate);
        GetWeatherDataAsyncTask task = new GetWeatherDataAsyncTask(DisplayWeatherActivity.this,DisplayWeatherActivity.this);
        task.execute(mQueryData);
    }

    @Override
    public void locationNotFound(LocationFinder.FailureReason failureReason) {
        if (failureReason== LocationFinder.FailureReason.NO_PERMISSION){
            Toast.makeText(this, R.string.location_detection_no_permission_exception,Toast.LENGTH_LONG).show();
        } else if (failureReason== LocationFinder.FailureReason.TIMEOUT){
            Toast.makeText(this, R.string.location_not_found_time_out_toast,Toast.LENGTH_LONG).show();
        }
    }
}
