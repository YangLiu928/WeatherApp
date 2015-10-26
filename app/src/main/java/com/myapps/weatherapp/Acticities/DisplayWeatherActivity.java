package com.myapps.weatherapp.Acticities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private final String TAG = "DisplayWeatherActivity";
    private static final int LOCATION_ACCESS_REQUEST_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);
        mSettingsButton = (Button) findViewById(R.id.settings_button);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayWeatherActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //show dialog
            new AlertDialog.Builder(this)
                    .setTitle(R.string.location_permission_title)
                    .setMessage(R.string.location_permission_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //prompt user with system dialog for location permission upon user clicking okay dialog button
                            ActivityCompat.requestPermissions(DisplayWeatherActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_ACCESS_REQUEST_CODE);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }





//        final ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage(getString(R.string.progress_loading_location_data));
//        dialog.show();
//        mLocationFinder = new LocationFinder(this,this);
//        mLocationFinder.detectLocation();
//        dialog.dismiss();

//       Ion.with(this).load("http://api.wunderground.com/api/249b924676fc85f7/forecast/q/37.8,-122.4.json")
//       .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
//           @Override
//           public void onCompleted(Exception e, JsonObject result) {
//               if (e!=null){
//
////                   Toast.makeText(DisplayWeatherActivity.this, result.getAsJsonObject("current_observation").getAsJsonObject("image").get("url").getAsString(), Toast.LENGTH_LONG).show();
//               } else {
////                   result.getAsJsonObject("forecast").getAsJsonObject("simpleforecase").getAsJsonObject("forecastday");
////                   Toast.makeText(DisplayWeatherActivity.this, "loadinged", Toast.LENGTH_LONG).show();
//                       Toast.makeText(DisplayWeatherActivity.this, result.get("forecast").getAsJsonObject().get("simpleforecast").getAsJsonObject().get("forecastday").getAsJsonArray().get(0).getAsJsonObject().get("high").getAsJsonObject().get("fahrenheit").getAsString(), Toast.LENGTH_LONG).show();
////                   Toast.makeText(DisplayWeatherActivity.this, result.get("current_observation").getAsJsonObject().get("image").getAsJsonObject().get("url").getAsString(), Toast.LENGTH_LONG).show();
//               }
//           }
//       });
//        String display = result.getAsJsonObject("response").get("version").toString();
//        Toast myToast = Toast.makeText(this,display,Toast.LENGTH_LONG);











    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        ListView weatherListing = (ListView) findViewById(R.id.weather_listing);
        ArrayList<ListItem> array = new ArrayList<>();
        for (WeatherData weatherData: weatherDataArray){
            ListItem item = new ListItem();
            item.setWeather(weatherData.getWeather());
            item.setTempRange(weatherData.getLowTemp() + " ~ " + weatherData.getHighTemp() + " " + weatherData.getUnit());
            item.setDate(weatherData.getDate());
            item.setWeatherImageURL(weatherData.getImageURL());
            array.add(item);
        }
        ArrayAdapter<ListItem> adapter = new MyAdapter(DisplayWeatherActivity.this, R.layout.list_item,array);
        weatherListing.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mQueryData = new QueryData(Integer.parseInt(mSharedPreference.getString("numberOfDays","3")),mSharedPreference.getString("metricUnit","Celsius"),mSharedPreference.getString("location","22304"));
        if (mQueryData.getLocation()==null||mQueryData.getLocation().trim().equals("")){
            LocationFinder mLocationFinder = new LocationFinder(this,this);
            mLocationFinder.detectLocation();
            Log.d(TAG,"LocationFinderCalled called");
            //TODO get current location


        } else {
            GetWeatherDataAsyncTask task = new GetWeatherDataAsyncTask(this,this);
            task.execute(mQueryData);
            Log.d(TAG, "GetWeatherAsyncTask called");
        }
    }

    @Override
    public void locationFound(Location location) {
        String coordinate = location.getLatitude()+","+location.getLongitude();
        Log.d(TAG, "LocationFound called");
        mQueryData = new QueryData(Integer.parseInt(mSharedPreference.getString("numberOfDays", "3")),mSharedPreference.getString("metricUnit","Celsius"),coordinate);
        GetWeatherDataAsyncTask task = new GetWeatherDataAsyncTask(DisplayWeatherActivity.this,DisplayWeatherActivity.this);
        task.execute(mQueryData);
    }

    @Override
    public void locationNotFound(LocationFinder.FailureReason failureReason) {
        if (failureReason== LocationFinder.FailureReason.NO_PERMISSION){
            Toast.makeText(this, R.string.location_detection_no_permission_exception,Toast.LENGTH_LONG);
        } else if (failureReason== LocationFinder.FailureReason.TIMEOUT){
            Toast.makeText(this, "Connection timed out.",Toast.LENGTH_LONG);
        }
        Log.d(TAG, "LocationNotFound called");
    }
}
