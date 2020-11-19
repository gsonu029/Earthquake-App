package com.example.quakereport;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

// To make Loader we need to implents LoaderCallbacks with generic parameter which is Earthquake here
// and we need to have 3 methods named as onStartLoading(), Onloadingfinished(),onLoadreset() Now do it
//public class EarthquakeActivity extends AppCompatActivity
public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>> {
    // string variable which contaions URL from where we will fetch the infromation
    private static final String SAMPLE_JSON_RESPONSE= "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=5";
    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private EarthquakeAdapter madapter;
    private static final int Earthloader_id=1;

    private TextView mEmptyStateTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initially set the View with blank screen here as Empty View
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView=(TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);


        madapter=new EarthquakeAdapter(this,new ArrayList<Earthquake>());


        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(madapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake currentEarthquake= madapter.getItem(position);
                Uri earthquakeUri= Uri.parse(currentEarthquake.getUrl());

                // create Intent object and pass it into Intent method as following
                Intent websiteIntent=new Intent(Intent.ACTION_VIEW,earthquakeUri);
                startActivity(websiteIntent);

            }
        });
        // We are using Loader so no need to call the AsynTask class


       // now we will call Loaders for that we must have referrence of that so make reference then

        // go ahead and check wheather Internate is active or not if not then show the No Internate Connection
        // otherwise do the call the Loader to do the fetch the data and display that.
        ConnectivityManager check_Internate= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = check_Internate.getActiveNetworkInfo();
        if(network_info!=null && network_info.isConnected()){
            // if network is connected then start fetching data by calling the Loader
            LoaderManager loaderManager= getLoaderManager();

            // Now we created above the reference now start calling the Loaders
            Log.e(LOG_TAG,"calling the Loader called  --> ");
            loaderManager.initLoader(Earthloader_id,null,this);

        }
        else  // if Network is not connected then show No Internate Connection like below
        {
                mEmptyStateTextView.setText(R.string.no_internate);
                View indicator= (View) findViewById(R.id.progress_bar);
                indicator.setVisibility(View.GONE);
        }
    }

    // We are using Loader now so remove AsyncTask class similar job will be done in EarthLoadere class
     /* //We will start calling the AsynTask method here and will fetch the data and update the Main UI
        EarthquakeAsync object= new EarthquakeAsync();
        object.execute(SAMPLE_JSON_RESPONSE);*/
/*
    private class EarthquakeAsync extends AsyncTask<String,Void,List<Earthquake>>
    {

        @Override
        protected List<Earthquake> doInBackground(String... strings)
        {
            if(strings[0]==null && strings.length<1)
                return null;
            List<Earthquake> result = QueryUtils. extractEarthquakes(strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Earthquake> result) {
            if(result==null)
                return;
            madapter.addAll(result);
        }
    }
*/


    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {

        // Create a new loader for the given URL
        Log.e(LOG_TAG,"calling the onCreateLoader --> ");
        return new EarthquakeLoader(this, SAMPLE_JSON_RESPONSE);
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        // Clear the adapter of previous earthquake data
        madapter.clear();
        Log.e(LOG_TAG,"calling the onLoadFinished --> ");

        // when data from earthquakes parameter is empty then then set the following text
        mEmptyStateTextView.setText(R.string.no_earthquake);
        // Hide loading indicator because the data has been loaded then we need to remove progressbar
        View loadingIndicator = (View) findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            madapter.addAll(earthquakes);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        // Loader reset, so we can clear out our existing data.
        madapter.clear();
        Log.e(LOG_TAG,"calling the LoaderReset --> ");

    }
}
