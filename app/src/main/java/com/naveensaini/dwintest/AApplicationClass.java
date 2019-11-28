package com.naveensaini.dwintest;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

public class AApplicationClass extends Application {

    //////////////////////
    ///////////////////// Video
    /////////////////////
    public static int cameraId = 1;


    //////////////////////
    ///////////////////// Database
    /////////////////////
    public static ArrayList<Long> dailyReferenceNunberList = new ArrayList<>();
    public static ArrayList<String> dailyTitleList = new ArrayList<>();
    public static ArrayList<String> dailyTextList = new ArrayList<>();
    public static ArrayList<Bitmap> dailyImageList = new ArrayList<>();
    public static ArrayList<String> dailyMediaStringList = new ArrayList<>();
    public static ArrayList<String> dailyTagStringList = new ArrayList<>();

    public static ArrayList<Long> weeklyReferenceNunberList = new ArrayList<>();
    public static ArrayList<String> weeklyTitleList = new ArrayList<>();
    public static ArrayList<String> weeklyTextList = new ArrayList<>();
    public static ArrayList<Bitmap> weeklyImageList = new ArrayList<>();
    public static ArrayList<String> weeklyMediaStringList = new ArrayList<>();
    public static ArrayList<String> weeklyTagStringList = new ArrayList<>();

    public static ArrayList<Long> monthlyReferenceNunberList = new ArrayList<>();
    public static ArrayList<String> monthlyTitleList = new ArrayList<>();
    public static ArrayList<String> monthlyTextList = new ArrayList<>();
    public static ArrayList<Bitmap> monthlyImageList = new ArrayList<>();
    public static ArrayList<String> monthlyMediaStringList = new ArrayList<>();
    public static ArrayList<String> monthlyTagStringList = new ArrayList<>();

    public static ArrayList<Long> yearlyReferenceNunberList = new ArrayList<>();
    public static ArrayList<String> yearlyTitleList = new ArrayList<>();
    public static ArrayList<String> yearlyTextList = new ArrayList<>();
    public static ArrayList<Bitmap> yearlyImageList = new ArrayList<>();
    public static ArrayList<String> yearlyMediaStringList = new ArrayList<>();
    public static ArrayList<String> yearlyTagStringList = new ArrayList<>();

    public static ArrayList<String> mainTagsArrayList = new ArrayList<>();

    public static ZZUtilitiesClass zzUtilitiesClass;


    @Override
    public void onCreate() {
        super.onCreate();


        // Initiating DataBase Retrieval
        DataBaseAsyncTask dataBaseAsyncTask = new DataBaseAsyncTask();
        dataBaseAsyncTask.execute();


        // Checking if Updated
        SharedPreferences defaultSP = PreferenceManager.getDefaultSharedPreferences(this);
        String XJ_VERSION_CODE = defaultSP.getString("XJ_VERSION_CODE", " ");
        if (!XJ_VERSION_CODE.equals("2.0")) {
            defaultSP.edit().putString("XJ_VERSION_CODE", String.valueOf(BuildConfig.VERSION_CODE)).apply();
            Log.i("XJAAP-oCreateVersion.", "Not Exists, BuildConfigCode = " + BuildConfig.VERSION_CODE);
        }
    }


    public class DataBaseAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            zzUtilitiesClass = new ZZUtilitiesClass(getApplicationContext());
            zzUtilitiesClass.loadDataBaseLists();

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }


    public static void logDataBase() {
        Log.i("XJAAP-FullDatabase", "Daily - " + dailyReferenceNunberList.toString());
        Log.i("XJAAP-FullDatabase", "Daily - " + dailyTextList.toString());
        Log.i("XJAAP-FullDatabase", "Daily - " + dailyTitleList.toString());
        Log.i("XJAAP-FullDatabase", "Daily - " + dailyImageList.toArray().toString());
        Log.i("XJAAP-FullDatabase", "Daily - " + dailyMediaStringList.toString());
        Log.i("XJAAP-FullDatabase", "Daily - " + dailyTagStringList.toString());


        Log.i("XJAAP-FullDatabase", "Weekly - " + weeklyReferenceNunberList.toString());
        Log.i("XJAAP-FullDatabase", "Weekly - " + weeklyTitleList.toString());
        Log.i("XJAAP-FullDatabase", "Weekly - " + weeklyTextList.toString());
        Log.i("XJAAP-FullDatabase", "Weekly - " + weeklyImageList.toString());
        Log.i("XJAAP-FullDatabase", "Daily - " + weeklyMediaStringList.toString());
        Log.i("XJAAP-FullDatabase", "Daily - " + weeklyTagStringList.toString());

        Log.i("XJAAP-FullDatabase", "Monthly - " + monthlyReferenceNunberList.toString());
        Log.i("XJAAP-FullDatabase", "Monthly - " + monthlyTitleList.toString());
        Log.i("XJAAP-FullDatabase", "Monthly - " + monthlyTextList.toString());
        Log.i("XJAAP-FullDatabase", "Monthly - " + monthlyImageList.toString());
        Log.i("XJAAP-FullDatabase", "Daily - " + monthlyMediaStringList.toString());
        Log.i("XJAAP-FullDatabase", "Daily - " + monthlyTagStringList.toString());

        Log.i("XJAAP-FullDatabase", "Yearly - " + yearlyReferenceNunberList.toString());
        Log.i("XJAAP-FullDatabase", "Yearly - " + yearlyTitleList.toString());
        Log.i("XJAAP-FullDatabase", "Yearly - " + yearlyTextList.toString());
        Log.i("XJAAP-FullDatabase", "Yearly - " + yearlyImageList.toString());
        Log.i("XJAAP-FullDatabase", "Daily - " + yearlyMediaStringList.toString());
        Log.i("XJAAP-FullDatabase", "Daily - " + yearlyTagStringList.toString());

        Log.i("XJAAP-FullDatabase", "mainTagsList " + mainTagsArrayList.toString());
    }
}
















