package com.example.quakereport;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.util.Log;
// by default this libraru was added but due to this after successfully installation app was crashing
// so that add the just above libray given in tutorial
//import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private static final String LOG_TAG=EarthquakeLoader.class.getName();
    private  String mUrl;
    public EarthquakeLoader(Context context,String url) {
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading(){
        Log.e(LOG_TAG,"calling the onStartLoading --> ");
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        Log.e(LOG_TAG,"calling the loadInBackground --> ");
        if(mUrl==null)
        {
            return null;
        }
        List<Earthquake> earthquakes=QueryUtils.extractEarthquakes(mUrl);
        return earthquakes;
    }
}
