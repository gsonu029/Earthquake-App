package com.example.quakereport;

import android.view.View;

import java.security.PublicKey;

public class Earthquake  {
    private double mMagnitude;
    private String mLocation;
    private long mTimeInMilliseconds;
    private String mUrl;

   public Earthquake (double mag , String locate, long date,String url){
       mMagnitude=mag;
       mLocation=locate;
       mTimeInMilliseconds=date;
       mUrl=url;
   }

   public String getLocation (){
       return mLocation;
       }
       public double getMagnitude(){
       return mMagnitude;
       }
       public long getTimeInMilliseconds(){
       return mTimeInMilliseconds;
       }
       public String  getUrl()
       {
           return mUrl;
       }

}
