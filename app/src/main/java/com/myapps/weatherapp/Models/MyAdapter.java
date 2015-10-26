package com.myapps.weatherapp.Models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapps.weatherapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by YangLiu on 10/25/2015.
 */
public class MyAdapter extends ArrayAdapter<ListItem> {
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    private static final String TAG = "MyAdapter";
    private ArrayList<ListItem> mItems;
    public MyAdapter(Context context, int resource, ArrayList<ListItem> items) {
        super(context, resource);
        this.mItems=items;
        Log.d(TAG,"My Adapter was called");
    }


    @Override
    public int getCount() {
        return mItems.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
        }

        ListItem item = mItems.get(position);


        if (item != null) {
            TextView date = (TextView) v.findViewById(R.id.list_item_date);
            TextView weather = (TextView) v.findViewById(R.id.list_item_weather);
            TextView tempRange = (TextView) v.findViewById(R.id.list_item_temp_range);
            ImageView weatherImage = (ImageView) v.findViewById(R.id.list_item_weather_image);

            if (date != null) {
                date.setText(item.getDate());
            }

            if(weather != null) {
                weather.setText(item.getWeather());
            }

            if(tempRange != null) {
                tempRange.setText(item.getTempRange());
            }

            if(weatherImage!=null){
                Picasso.with(this.getContext()).load(item.getWeatherImageURL().toString()).into(weatherImage);
            }
        }
        return v;
    }
}
