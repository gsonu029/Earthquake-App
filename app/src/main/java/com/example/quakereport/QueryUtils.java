package com.example.quakereport;

import android.media.SoundPool;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    // Instead of hardcoding to get JSON response we will get get via making online Http request.
    private static final String SAMPLE_JSON_RESPONSE ="https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Earthquake> extractEarthquakes(String requesturl) {
        // make the thread 2 second slow show that we can see the ProgressBar clearly
        // time is given in Milisecond
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Log.e(LOG_TAG,"calling the the method to fetch data from server --> ");
        URL url=createURL(requesturl);
        // To store JSON response create variable and initialize with null
        String jsonResponse=null;

        // To get the Json response we need to establish the Network connection
        // There may be error while getting Json response during  USGS API call so better to handle by exceptiom
        try {

            jsonResponse=makeHttpcoonection(url);

        } catch (IOException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        List<Earthquake> earthquakes= extractFeatureFromJsonResponse(jsonResponse);
        return earthquakes;

    }

    public static URL createURL (String requestUrl)
    {
        URL url =null;
        try{
            url=new URL (requestUrl);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"Getting error while creating URL ",e);
        }
        return url;
    }

    private static String makeHttpcoonection(URL url) throws IOException
    {
        String jsonResponse="";
        InputStream inputstream=null;
        HttpURLConnection urlConnection =null;

        //if url is itself null then return null as a result so return jsonResponse which is null yet
        if(url==null) {
            return jsonResponse;
        }
        // while creating Network connection there may be exception which will also be
        // suggested by Android Studio so handle that Exception too.

        try{
            urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000); // time is in miliSecond
            urlConnection.setConnectTimeout(15000); // time is in milisecond
            urlConnection.connect();

            if(urlConnection.getResponseCode()==200) {
                inputstream=urlConnection.getInputStream();
                jsonResponse=readFromStreamReader(inputstream);
            }
            else{
                Log.e(LOG_TAG,"respose code is not 200  :( it is " + urlConnection.getResponseCode());
            }
        }
        catch (IOException e) {
            Log.e(LOG_TAG,"there is error in server side ");
        }
        finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(inputstream!=null){
                inputstream.close();
            }
        }
        return jsonResponse;

    } // end of MakeHttpconnection method

    private static String readFromStreamReader(InputStream inputStream) throws IOException {
        StringBuilder output= new StringBuilder();
        if(inputStream!=null)
        {
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream,Charset.forName("UTF-8"));
            BufferedReader reader =new BufferedReader(inputStreamReader);
            String line=reader.readLine();
            while(line!=null){
                output.append(line);
                line=reader.readLine();
            }
        }
        return output.toString();
    } // end of readFromStreamReader method

    private static List<Earthquake> extractFeatureFromJsonResponse(String earhtquakeJson){
        // if jsonResponse is itself is null then we need to return list of null value that is basically null here
        if(TextUtils.isEmpty(earhtquakeJson)) {
            return null;
        }

        List<Earthquake> earthquakes=new ArrayList<>();

        // there may be error while parsing the Json so handle it via IOException
        try{

            JSONObject basejsonobject=new JSONObject(earhtquakeJson);
            JSONArray featurArray= basejsonobject.getJSONArray("features");

            for(int i=0;i<featurArray.length();i++)
            {
                JSONObject currentEarthquakedata= featurArray.getJSONObject(i);
                JSONObject properties= currentEarthquakedata.getJSONObject("properties");
                double magnitude=properties.getDouble("mag");
                String location =properties.getString("place");
                long time=properties.getLong("time");
                String url =properties.getString("url");

                // create a object of Earthquake and add all the data and add to earthquakes and return it.
                Earthquake earthquake=new Earthquake(magnitude,location,time,url);
                earthquakes.add(earthquake);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return earthquakes;


    } // end of extractFeatureFromJsonResponse method



}