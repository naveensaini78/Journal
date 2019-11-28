package com.naveensaini.dwintest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ZZUtilitiesClass extends SQLiteOpenHelper {


    private Context context;

    public static final String DATABASE_NAME = "DwinDatabase.db";
    public static final int DATABASE_VERSION = 1;

    // TABLE 1
    public static final String TABLE_ENTRIES_DATA = "entriesTable";

    public static final String ERN = "entryReferenceNumber";
    public static final String ENTRY_TITLE = "entryTitle";
    public static final String ENTRY_TEXT = "entryText";
    public static final String ENTRY_TYPE = "entryType";
    public static final String ENTRY_MEDIA = "entryMedia";
    public static final String ENTRY_TAGS = "entryTags";

    // TABLE 2
    public static final String TABLE_APP_INFO = "appInfoTable";

    public static final String ROW_NUMBER = "rowNumber";
    public static final String MAIN_TAGS_STRING = "mainTagsString";


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ZZUtilitiesClass(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("XJZZS-onCreate", "Done-2");

        // TABLE_ENTRIES_DATA
        // ReferenceNumber  |  Title  |  EntryText  |  EntryType(0,1,2,3 for d,w,m,y)  |  EntryMedia | EntryTags |
        String createTableOne = "CREATE TABLE " + TABLE_ENTRIES_DATA + "(" +
                ERN + " INTEGER, " +
                ENTRY_TITLE + " VARCHAR, " +
                ENTRY_TEXT + " VARCHAR, " +
                ENTRY_TYPE + " INTEGER, " +
                ENTRY_MEDIA + " VARCHAR, " +
                ENTRY_TAGS + " VARCHAR)";
        db.execSQL(createTableOne);

        Log.i("XJZZS-onCreate", "Done-1");

        // TABLE 2 - appInfoTable
        // rowNumber  |  mainTagsString |
        String createTableTwo = "CREATE TABLE " + TABLE_APP_INFO + "(" +
                ROW_NUMBER + " INTEGER PRIMARY KEY, " +
                MAIN_TAGS_STRING + " VARCHAR) ";
        db.execSQL(createTableTwo);

        Log.i("XJZZS-onCreate", "Done");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /////////////////
    ///////////////// Other  methods
    //////////////////
    public void loadDataBaseLists() {
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        SQLiteDatabase writableDatabase = this.getWritableDatabase();

        clearExistingLists();

        //////// Fetching Data from Sql ///////
        /// TABLE 1
        try {
            Cursor cursor = readableDatabase.rawQuery("SELECT * FROM " + TABLE_ENTRIES_DATA, null);
            int entryReferenceNumberIndex = cursor.getColumnIndex(ERN);
            int entryHeading = cursor.getColumnIndex(ENTRY_TITLE);
            int entryText = cursor.getColumnIndex(ENTRY_TEXT);
            int entryType = cursor.getColumnIndex(ENTRY_TYPE);
            int entryMedia = cursor.getColumnIndex(ENTRY_MEDIA);
            int entryTag = cursor.getColumnIndex(ENTRY_TAGS);

            Log.i("XJZZS-loadDataLists", "Here" + entryReferenceNumberIndex + ","
                    + entryHeading + "," + entryText + "," + entryType + "," + entryMedia);

            cursor.moveToFirst();

            do {
                int entryTypeInt = cursor.getInt(entryType);

                switch (entryTypeInt) {
                    case 0://Daily

                        addRowDailyList(context, cursor.getLong(entryReferenceNumberIndex),
                                cursor.getString(entryHeading),
                                cursor.getString(entryText),
                                cursor.getString(entryMedia),
                                cursor.getString(entryTag));
                        break;
                    case 1://Weekly
                        addRowWeeklyList(context, cursor.getLong(entryReferenceNumberIndex),
                                cursor.getString(entryHeading),
                                cursor.getString(entryText),
                                cursor.getString(entryMedia),
                                cursor.getString(entryTag));

                        break;
                    case 2://Monthly
                        addRowMonthlyList(context, cursor.getLong(entryReferenceNumberIndex),
                                cursor.getString(entryHeading),
                                cursor.getString(entryText),
                                cursor.getString(entryMedia),
                                cursor.getString(entryTag));
                        break;
                    case 3://Yearly
                        addRowYearlyList(context, cursor.getLong(entryReferenceNumberIndex),
                                cursor.getString(entryHeading),
                                cursor.getString(entryText),
                                cursor.getString(entryMedia),
                                cursor.getString(entryTag));
                        break;

                }

                Log.i("XJZZS-SqlTest", cursor.getLong(entryReferenceNumberIndex)
                        + "," + cursor.getString(entryHeading) + "," + cursor.getString(entryText)
                        + "," + cursor.getInt(entryType));

            } while (cursor.moveToNext());

        } catch (Exception e) {
            Log.i("XJZZS-loadDataExp1", e.toString());
        }


        // TABLE 2
        try {
            Cursor cursor = readableDatabase.rawQuery("SELECT mainTagsString FROM appInfoTable WHERE rowNumber = 1", null);
            int mainTagsIndex = cursor.getColumnIndex("mainTagsString");
            cursor.moveToFirst();

            String mainTagsString = cursor.getString(mainTagsIndex);

            Log.i("XJZZS-getCellForValue", mainTagsString);


            // Setting Values to MainTagsArrayList
            if (mainTagsString != null) {
                AApplicationClass.mainTagsArrayList = (ArrayList<String>) ZZObjectSerializer.deserialize(mainTagsString);
            }

        } catch (Exception e) {
            try {
                String firstLineString = "INSERT INTO " + TABLE_APP_INFO + "(" + MAIN_TAGS_STRING + ") VALUES(null)";
                writableDatabase.execSQL(firstLineString);
                writableDatabase.execSQL("UPDATE appInfoTable SET mainTagsString = null WHERE rowNumber = 1");

            } catch (Exception e1) {
                Log.i("XJZZS-loadDataT2", e.toString());
            }

        }

    }


    private void clearExistingLists() {
        AApplicationClass.dailyReferenceNunberList.clear();
        AApplicationClass.dailyTitleList.clear();
        AApplicationClass.dailyTextList.clear();
        AApplicationClass.dailyImageList.clear();
        AApplicationClass.dailyMediaStringList.clear();
        AApplicationClass.dailyTagStringList.clear();

        AApplicationClass.weeklyReferenceNunberList.clear();
        AApplicationClass.weeklyTitleList.clear();
        AApplicationClass.weeklyTextList.clear();
        AApplicationClass.weeklyImageList.clear();
        AApplicationClass.weeklyMediaStringList.clear();
        AApplicationClass.weeklyTagStringList.clear();

        AApplicationClass.monthlyReferenceNunberList.clear();
        AApplicationClass.monthlyTitleList.clear();
        AApplicationClass.monthlyTextList.clear();
        AApplicationClass.monthlyImageList.clear();
        AApplicationClass.monthlyMediaStringList.clear();
        AApplicationClass.monthlyTagStringList.clear();

        AApplicationClass.yearlyReferenceNunberList.clear();
        AApplicationClass.yearlyTitleList.clear();
        AApplicationClass.yearlyTextList.clear();
        AApplicationClass.yearlyImageList.clear();
        AApplicationClass.yearlyMediaStringList.clear();
        AApplicationClass.yearlyTagStringList.clear();

        AApplicationClass.mainTagsArrayList.clear();
    }


    private void addRowDailyList(Context context, Long l, String title, String text, String media, String tags) {
        Log.i("XJZZU", "addRowDailyLIst");
        AApplicationClass.dailyReferenceNunberList.add(l);
        AApplicationClass.dailyTitleList.add(title);
        AApplicationClass.dailyTextList.add(text);
        AApplicationClass.dailyImageList.add(ZZUtilitiesClass.getPhotoFromString(context, media));
        AApplicationClass.dailyMediaStringList.add(media);
        AApplicationClass.dailyTagStringList.add(tags);

    }


    private void addRowWeeklyList(Context context, Long l, String title, String text, String media, String tags) {
        AApplicationClass.weeklyReferenceNunberList.add(l);
        AApplicationClass.weeklyTitleList.add(title);
        AApplicationClass.weeklyTextList.add(text);
        AApplicationClass.weeklyImageList.add(ZZUtilitiesClass.getPhotoFromString(context, media));
        AApplicationClass.weeklyMediaStringList.add(media);
        AApplicationClass.weeklyTagStringList.add(tags);
    }


    private void addRowMonthlyList(Context context, Long l, String title, String text, String media, String tags) {
        AApplicationClass.monthlyReferenceNunberList.add(l);
        AApplicationClass.monthlyTitleList.add(title);
        AApplicationClass.monthlyTextList.add(text);
        AApplicationClass.monthlyImageList.add(ZZUtilitiesClass.getPhotoFromString(context, media));
        AApplicationClass.monthlyMediaStringList.add(media);
        AApplicationClass.monthlyTagStringList.add(tags);

    }


    private void addRowYearlyList(Context context, long l, String title, String text, String media, String tags) {
        AApplicationClass.yearlyReferenceNunberList.add(l);
        AApplicationClass.yearlyTitleList.add(title);
        AApplicationClass.yearlyTextList.add(text);
        AApplicationClass.yearlyImageList.add(ZZUtilitiesClass.getPhotoFromString(context, media));
        AApplicationClass.yearlyMediaStringList.add(media);
        AApplicationClass.yearlyTagStringList.add(tags);
    }


    public void addRowToDatabase(Context context, long entryReferenceNumber, int entryTypeInt) {

        SQLiteDatabase entriesSql = AApplicationClass.zzUtilitiesClass.getWritableDatabase();

        Log.i("XJZZU-addrowToData", String.valueOf(entryReferenceNumber) + ", " + String.valueOf(entryTypeInt));

        switch (entryTypeInt) {
            case 0://Daily
                addRowDailyList(context, entryReferenceNumber, "", "", null, null);
                CBDailyFragment.CCDailyRecyclerAdapter.notifyDataSetChanged();
                break;
            case 1://Weekly
                addRowWeeklyList(context, entryReferenceNumber, "", "", null, null);
                CBWeeklyFragment.cbWeeklyAdapter.notifyDataSetChanged();
                break;
            case 2://Monthly
                addRowMonthlyList(context, entryReferenceNumber, "", "", null, null);
                CBMonthlyFragment.cbMonthlyAdapter.notifyDataSetChanged();
                break;
            case 3://Yearly
                addRowYearlyList(context, entryReferenceNumber, "", "", null, null);
                CBYearlyFragment.cbYearlyAdapter.notifyDataSetChanged();
                break;
        }


        String addRowString = "INSERT INTO entriesTable(entryReferenceNumber, entryType) " +
                "VALUES(" + entryReferenceNumber + ", " + entryTypeInt + ")";
        entriesSql.execSQL(addRowString);

        Log.i("XJZZU-addRowToDataBase", addRowString);
    }


    public void addNewMainTagsToDataBase() {
        SQLiteDatabase entriesSql = AApplicationClass.zzUtilitiesClass.getWritableDatabase();

        String mainTagsNewString = null;
        try {
            mainTagsNewString = ZZObjectSerializer.serialize(AApplicationClass.mainTagsArrayList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        entriesSql.execSQL("UPDATE appInfoTable SET mainTagsString = '" + mainTagsNewString + "' WHERE rowNumber = 1");
    }


    public String getCellForValue(long dEntryReferenceNumber, String columnName) {
        SQLiteDatabase entriesSql = AApplicationClass.zzUtilitiesClass.getReadableDatabase();

        Log.i("XJZZU-getCellForValue", "Start");

        try {
            String sqlString = "SELECT entryMedia FROM entriesTable " +
                    "WHERE entryReferenceNumber = " + dEntryReferenceNumber;
            Log.i("XJZZU-getCellForValue", sqlString);
            Cursor cursor = entriesSql.rawQuery(sqlString, null);

            int columnIndex = cursor.getColumnIndex(columnName);
            cursor.moveToFirst();

            String newString = cursor.getString(columnIndex);
            Log.i("XJZZU-getCellForValue", newString);

            return newString;
        } catch (Exception e) {
            Log.i("XJZZU-getCellForValue", e.toString());

            return "";
        }

    }


    public void addMediaToDataBase(long entryReferenceNumber, String mediaName) {
        SQLiteDatabase entriesSql = AApplicationClass.zzUtilitiesClass.getReadableDatabase();

        String mediaString = getCellForValue(entryReferenceNumber, "entryMedia");
        Log.i("XJZZU-addMedia-input", entryReferenceNumber + ","
                + mediaName + "," + mediaString);

        try {
            ArrayList<String> oldArrayList = (ArrayList<String>) ZZObjectSerializer.deserialize(mediaString);

            if (oldArrayList == null) {
                oldArrayList = new ArrayList<>();
            }

            oldArrayList.add(mediaName);
            String newString = ZZObjectSerializer.serialize(oldArrayList);

            Log.i("XJZZU-addMediaToDB", "oldAL-" + oldArrayList.toString() +
                    ", new String-" + newString);

            entriesSql.execSQL("UPDATE entriesTable SET entryMedia = '" + newString +
                    "' WHERE entryReferenceNumber = " + entryReferenceNumber);

            Log.i("XJZZU-newMediaString", newString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void addNewTagsStringToDataBase(long entryReferenceNumber, String tagsString) {
        SQLiteDatabase entriesSql = AApplicationClass.zzUtilitiesClass.getWritableDatabase();

        // Two different method because of the difference of '' (i.e. 'null' is not valid)
        if (tagsString == null) {
            entriesSql.execSQL("UPDATE entriesTable SET entryTags = " + tagsString +
                    " WHERE entryReferenceNumber = " + entryReferenceNumber);
        } else {
            entriesSql.execSQL("UPDATE entriesTable SET entryTags = '" + tagsString +
                    "' WHERE entryReferenceNumber = " + entryReferenceNumber);
        }


        Log.i("XJZZU-newTagsString", "new Tags String - " + tagsString);

    }


    public static Bitmap getPhotoFromString(Context context, String mediaString) {
        Bitmap bitmap = null;

        if (mediaString != null) {
            ArrayList<String> arrayListOfMedia = new ArrayList<>();

            try {
                arrayListOfMedia = (ArrayList<String>)
                        ZZObjectSerializer.deserialize(mediaString);

                Log.i("XJZZU-mediaArrayList", arrayListOfMedia.toString());

            } catch (Exception e) {
                Log.i("XJZZU-makeArrayLists", e.toString());
            }


            //////////////////////////////////
            if (arrayListOfMedia != null) {
                for (String value : arrayListOfMedia) {
                    String valueExtension = value.substring(value.lastIndexOf("."));

                    Log.i("XJZZU-singleMedias", value.toString());
                    Log.i("XJZZU-valueExtension", valueExtension);


                    switch (valueExtension) {
                        case ".jpg":
                            String mediaPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                                    + context.getResources().getString(R.string.storage_location) + value;

                            File imageFile = new File(mediaPath);

                            if (imageFile.exists()) {
                                Log.i("XJZZU-makeArrayList", "image");

                                // Create and compress bitmap
                                Bitmap originalBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                Bitmap compressedBitmap;

                                if (originalBitmap.getWidth() <= 100) {
                                    compressedBitmap = originalBitmap;

                                    Log.i("XJZZU-compressBitmap",
                                            "original-" + originalBitmap.getWidth() + ", " + originalBitmap.getHeight() + "; ");

                                } else {
                                    int width = 100;
                                    double height = (width * originalBitmap.getHeight()) / originalBitmap.getWidth();

                                    Log.i("XJZZU-compressBitmap",
                                            "original-" + originalBitmap.getWidth() + ", " + originalBitmap.getHeight() + "; "
                                                    + width + ", " + height);

                                    compressedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, (int) height, true);

                                }

                                bitmap = compressedBitmap;

                            }
                            break;
                    }
                }
            }
        }

        return bitmap;

    }
}
/*
    //////////////////////
    ///////////////////// Video
    /////////////////////
    public static int cameraId = 1;


    //////////////////////
    //////////////////// Others
    /////////////////////
    public static class VideoData {
        String videoPath;
        Bitmap videoBitmap;

        public VideoData(String videoPath, Bitmap videoBitmap) {
            this.videoPath = videoPath;
            this.videoBitmap = videoBitmap;
        }
    }


    public static class AudioData {
        String audioPath;
        String audioName;

        public AudioData(String audioPath, String audioName) {
            this.audioPath = audioPath;
            this.audioName = audioName;
        }
    }


    //////////////////////
    ///////////////////// Database
    /////////////////////
    public static SQLiteDatabase entriesSql;

    static ArrayList<Long> dailyReferenceNunberList = new ArrayList<>();
    static ArrayList<String> dailyTitleList = new ArrayList<>();
    static ArrayList<String> dailyTextList = new ArrayList<>();
    static ArrayList<Bitmap> dailyImageList = new ArrayList<>();
    static ArrayList<String> dailyMediaStringList = new ArrayList<>();
    static ArrayList<String> dailyTagStringList = new ArrayList<>();

    static ArrayList<Long> weeklyReferenceNunberList = new ArrayList<>();
    static ArrayList<String> weeklyTitleList = new ArrayList<>();
    static ArrayList<String> weeklyTextList = new ArrayList<>();
    static ArrayList<Bitmap> weeklyImageList = new ArrayList<>();
    static ArrayList<String> weeklyMediaStringList = new ArrayList<>();
    static ArrayList<String> weeklyTagStringList = new ArrayList<>();

    static ArrayList<Long> monthlyReferenceNunberList = new ArrayList<>();
    static ArrayList<String> monthlyTitleList = new ArrayList<>();
    static ArrayList<String> monthlyTextList = new ArrayList<>();
    static ArrayList<Bitmap> monthlyImageList = new ArrayList<>();
    static ArrayList<String> monthlyMediaStringList = new ArrayList<>();
    static ArrayList<String> monthlyTagStringList = new ArrayList<>();

    static ArrayList<Long> yearlyReferenceNunberList = new ArrayList<>();
    static ArrayList<String> yearlyTitleList = new ArrayList<>();
    static ArrayList<String> yearlyTextList = new ArrayList<>();
    static ArrayList<Bitmap> yearlyImageList = new ArrayList<>();
    static ArrayList<String> yearlyMediaStringList = new ArrayList<>();
    static ArrayList<String> yearlyTagStringList = new ArrayList<>();

    static ArrayList<String> mainTagsArrayList = new ArrayList<>();


    ///////////////////
    /////////////////// Basic functions while starting activity
    //////////////////
    public static void openOrCreateDatabase(Context context) {

        Log.i("XJZZU-OpenOrCreateDB", "1");
        entriesSql = context.openOrCreateDatabase("entriesDatabase", MODE_PRIVATE, null);

        // TABLE 1 - entriesTable
        // ReferenceNumber  |  Title  |  EntryText  |  EntryType(0,1,2,3 for d,w,m,y)  |  EntryMedia | EntryTags |
        entriesSql.execSQL("CREATE TABLE IF NOT EXISTS entriesTable" +
                "(entryReferenceNumber INTEGER, " +
                "entryTitle VARCHAR, entryText VARCHAR, entryType INTEGER, entryMedia VARCHAR, entryTags VARCHAR)");

        Log.i("XJZZU-OpenOrCreateDB", "2");

        // TABLE 2 - appInfoTable
        // rowNumber  |  mainTagsString |
        entriesSql.execSQL("CREATE TABLE IF NOT EXISTS appInfoTable" +
                "(rowNumber INTEGER PRIMARY KEY, mainTagsString VARCHAR)");

        Log.i("XJZZU-OpenOrCreateDB", entriesSql.getPath());

    }


    public static void loadDataBaseLists(Context context) {
        clearExistingLists();

        //////// Fetching Data from Sql ///////

        /// TABLE 1
        try {
            Cursor cursor = entriesSql.rawQuery("SELECT * FROM entriesTable", null);
            int entryReferenceNumberIndex = cursor.getColumnIndex("entryReferenceNumber");
            int entryHeading = cursor.getColumnIndex("entryTitle");
            int entryText = cursor.getColumnIndex("entryText");
            int entryType = cursor.getColumnIndex("entryType");
            int entryMedia = cursor.getColumnIndex("entryMedia");
            int entryTag = cursor.getColumnIndex("entryTags");
            Log.i("XJZZU-loadDataLists", "Here" + entryReferenceNumberIndex + ","
                    + entryHeading + "," + entryText + "," + entryType + "," + entryMedia);

            cursor.moveToFirst();

            int i = 0;

            do {
                int entryTypeInt = cursor.getInt(entryType);
                Log.i("XJZZU-loadDataLists", "Fetch Data" + i);
                i++;


                switch (entryTypeInt) {
                    case 0://Daily

                        String tag = null;
                        try {tag = cursor.getString(entryTag);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        addRowDailyList(context, cursor.getLong(entryReferenceNumberIndex),
                                cursor.getString(entryHeading),
                                cursor.getString(entryText),
                                cursor.getString(entryMedia),
                                tag);
                        break;
                    case 1://Weekly
                        addRowWeeklyList(context, cursor.getLong(entryReferenceNumberIndex),
                                cursor.getString(entryHeading),
                                cursor.getString(entryText),
                                cursor.getString(entryMedia),
                                cursor.getString(entryTag));

                        break;
                    case 2://Monthly
                        addRowMonthlyList(context, cursor.getLong(entryReferenceNumberIndex),
                                cursor.getString(entryHeading),
                                cursor.getString(entryText),
                                cursor.getString(entryMedia),
                                cursor.getString(entryTag));
                        break;
                    case 3://Yearly
                        addRowYearlyList(context, cursor.getLong(entryReferenceNumberIndex),
                                cursor.getString(entryHeading),
                                cursor.getString(entryText),
                                cursor.getString(entryMedia),
                                cursor.getString(entryTag));
                        break;

                }

                Log.i("SqlTest", cursor.getLong(entryReferenceNumberIndex)
                        + "," + cursor.getString(entryHeading) + "," + cursor.getString(entryText)
                        + "," + cursor.getInt(entryType));

            } while (cursor.moveToNext());

        } catch (Exception e) {
            Log.i("XJZZU-loadDataException", e.toString());
        }


        // TABLE 2
        try {
            Cursor cursor = entriesSql.rawQuery("SELECT mainTagsString FROM appInfoTable WHERE rowNumber = 1", null);
            int mainTagsIndex = cursor.getColumnIndex("mainTagsString");
            cursor.moveToFirst();

            String mainTagsString = cursor.getString(mainTagsIndex);

            Log.i("XJZZU-getCellForValue", mainTagsString);


            // Setting Values to MainTagsArrayList
            if (mainTagsString != null){
                mainTagsArrayList = (ArrayList<String>) ZZObjectSerializer.deserialize(mainTagsString);
            }

        } catch (Exception e){
            entriesSql.execSQL("UPDATE appInfoTable SET mainTagsString = null WHERE rowNumber = 1");

            Log.i("XJZZU-loadDataT2", e.toString());
        }
    }


    private static void clearExistingLists() {
        dailyReferenceNunberList.clear();
        dailyTitleList.clear();
        dailyTextList.clear();
        dailyImageList.clear();
        dailyMediaStringList.clear();
        dailyTagStringList.clear();

        weeklyReferenceNunberList.clear();
        weeklyTitleList.clear();
        weeklyTextList.clear();
        weeklyImageList.clear();
        weeklyMediaStringList.clear();
        weeklyTagStringList.clear();

        monthlyReferenceNunberList.clear();
        monthlyTitleList.clear();
        monthlyTextList.clear();
        monthlyImageList.clear();
        monthlyMediaStringList.clear();
        monthlyTagStringList.clear();

        yearlyReferenceNunberList.clear();
        yearlyTitleList.clear();
        yearlyTextList.clear();
        yearlyImageList.clear();
        yearlyMediaStringList.clear();
        yearlyTagStringList.clear();

        mainTagsArrayList.clear();
    }


    private static void addRowDailyList(Context context, Long l, String title, String text, String media, String tags) {
        Log.i("XJZZU", "addRowDailyLIst");
        dailyReferenceNunberList.add(l);
        dailyTitleList.add(title);
        dailyTextList.add(text);
        dailyImageList.add(getPhotoFromString(context, media));
        dailyMediaStringList.add(media);
        dailyTagStringList.add(tags);

    }


    private static void addRowWeeklyList(Context context, Long l, String title, String text, String media, String tags) {
        weeklyReferenceNunberList.add(l);
        weeklyTitleList.add(title);
        weeklyTextList.add(text);
        weeklyImageList.add(getPhotoFromString(context, media));
        weeklyMediaStringList.add(media);
        weeklyTagStringList.add(tags);
    }


    private static void addRowMonthlyList(Context context, Long l, String title, String text, String media, String tags) {
        monthlyReferenceNunberList.add(l);
        monthlyTitleList.add(title);
        monthlyTextList.add(text);
        monthlyImageList.add(getPhotoFromString(context, media));
        monthlyMediaStringList.add(media);
        monthlyTagStringList.add(tags);

    }


    private static void addRowYearlyList(Context context, long l, String title, String text, String media, String tags) {
        yearlyReferenceNunberList.add(l);
        yearlyTitleList.add(title);
        yearlyTextList.add(text);
        yearlyImageList.add(getPhotoFromString(context, media));
        yearlyMediaStringList.add(media);
        yearlyTagStringList.add(tags);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void addRowToDatabase(Context context, long entryReferenceNumber, int entryTypeInt) {

        Log.i("XJZZU-addrowToData", String.valueOf(entryReferenceNumber) +", "+ String.valueOf(entryTypeInt));

        switch (entryTypeInt) {
            case 0://Daily
                addRowDailyList(context, entryReferenceNumber, "", "", null, null);
                CBDailyFragment.CCDailyRecyclerAdapter.notifyDataSetChanged();
                break;
            case 1://Weekly
                addRowWeeklyList(context, entryReferenceNumber, "", "", null, null);
                CBWeeklyFragment.cbWeeklyAdapter.notifyDataSetChanged();
                break;
            case 2://Monthly
                addRowMonthlyList(context, entryReferenceNumber, "", "", null, null);
                CBMonthlyFragment.cbMonthlyAdapter.notifyDataSetChanged();
                break;
            case 3://Yearly
                addRowYearlyList(context, entryReferenceNumber, "", "", null, null);
                CBYearlyFragment.cbYearlyAdapter.notifyDataSetChanged();
                break;
        }


        String addRowString ="INSERT INTO entriesTable(entryReferenceNumber, entryType) " +
                "VALUES(" + entryReferenceNumber + ", " + entryTypeInt + ")";
        entriesSql.execSQL(addRowString);

        Log.i("XJZZU-addRowToDataBase", addRowString);
    }


    public static void addNewMainTagsToDataBase(){
        String mainTagsNewString = null;
        try {
            mainTagsNewString = ZZObjectSerializer.serialize(mainTagsArrayList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        entriesSql.execSQL("UPDATE appInfoTable SET mainTagsString = '"+ mainTagsNewString +"' WHERE rowNumber = 1");
    }


    ///////////////////
    ////////////////// Additional functions
    ///////////////////








    // Keep it
    public static Bitmap getPhotoFromString(Context context, String mediaString) {
        Bitmap bitmap = null;

        if (mediaString != null){
            ArrayList<String> arrayListOfMedia = new ArrayList<>();

            try {
                arrayListOfMedia = (ArrayList<String>)
                        ZZObjectSerializer.deserialize(mediaString);

                Log.i("XJZZU-mediaArrayList", arrayListOfMedia.toString());

            } catch (Exception e) {
                Log.i("XJZZU-makeArrayLists", e.toString());
            }


            //////////////////////////////////
            if (arrayListOfMedia != null){
                for (String value : arrayListOfMedia) {
                    String valueExtension = value.substring(value.lastIndexOf("."));

                    Log.i("XJZZU-singleMedias", value.toString());
                    Log.i("XJZZU-valueExtension", valueExtension);


                    switch (valueExtension) {
                        case ".jpg":
                            String mediaPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                                    + context.getResources().getString(R.string.storage_location) + value;

                            File imageFile = new File(mediaPath);

                            if (imageFile.exists()) {
                                Log.i("XJZZU-makeArrayList", "image");

                                // Create and compress bitmap
                                Bitmap originalBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                Bitmap compressedBitmap;

                                if (originalBitmap.getWidth() <= 100){
                                    compressedBitmap = originalBitmap;

                                    Log.i("XJZZU-compressBitmap",
                                            "original-"+originalBitmap.getWidth() + ", " + originalBitmap.getHeight() + "; ");

                                } else {
                                    int width = 100;
                                    double height = (width * originalBitmap.getHeight()) / originalBitmap.getWidth();

                                    Log.i("XJZZU-compressBitmap",
                                            "original-"+originalBitmap.getWidth() + ", " + originalBitmap.getHeight() + "; "
                                                    +width + ", "+ height);

                                    compressedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, (int) height, true);

                                }

                                bitmap = compressedBitmap;

                            }
                            break;
                    }
                }
            }
        }

        return bitmap;

    }





















    public static String getCellForValue(long dEntryReferenceNumber, String columnName) {
        Log.i("XJZZU-getCellForValue", "Start");


        try {
            String sqlString = "SELECT entryMedia FROM entriesTable " +
                    "WHERE entryReferenceNumber = " + dEntryReferenceNumber;
            Log.i("XJZZU-getCellForValue", sqlString);
            Cursor cursor = entriesSql.rawQuery(sqlString, null);

            int columnIndex = cursor.getColumnIndex(columnName);
            cursor.moveToFirst();

            String newString = cursor.getString(columnIndex);
            Log.i("XJZZU-getCellForValue", newString);

            return newString;
        } catch (Exception e) {
            Log.i("XJZZU-getCellForValue", e.toString());
            return "";
        }


    }


    public static void addMediaToDataBase(long entryReferenceNumber, String mediaName) {
        String mediaString = getCellForValue(entryReferenceNumber, "entryMedia");
        Log.i("XJZZU-addMedia-input", entryReferenceNumber + ","
                + mediaName + "," + mediaString);

        try {
            ArrayList<String> oldArrayList = (ArrayList<String>) ZZObjectSerializer.deserialize(mediaString);

            if (oldArrayList == null){oldArrayList = new ArrayList<>();}

            oldArrayList.add(mediaName);
            String newString = ZZObjectSerializer.serialize(oldArrayList);

            Log.i("XJZZU-addMediaToDB", "oldAL-" + oldArrayList.toString() +
                    ", new String-" + newString);

            entriesSql.execSQL("UPDATE entriesTable SET entryMedia = '" + newString +
                    "' WHERE entryReferenceNumber = " + entryReferenceNumber);

            Log.i("XJZZU-newMediaString", newString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void addNewTagsStringToDataBase (long entryReferenceNumber, String tagsString){
        entriesSql.execSQL("UPDATE entriesTable SET entryTags = '" + tagsString +
                "' WHERE entryReferenceNumber = " + entryReferenceNumber);

        Log.i("XJZZU-newTagsString", tagsString);

    }


    public static void logDataBase() {
        Log.i("XJZZU-FullDatabase", "Daily - " + dailyReferenceNunberList.toString());
        Log.i("XJZZU-FullDatabase", "Daily - " + dailyTextList.toString());
        Log.i("XJZZU-FullDatabase", "Daily - " + dailyTitleList.toString());
        Log.i("XJZZU-FullDatabase", "Daily - " + dailyImageList.toArray().toString());
        Log.i("XJZZU-FullDatabase", "Daily - " + dailyMediaStringList.toString());
        Log.i("XJZZU-FullDatabase", "Daily - " + dailyTagStringList.toString());


        Log.i("XJZZU-FullDatabase", "Weekly - " + weeklyReferenceNunberList.toString());
        Log.i("XJZZU-FullDatabase", "Weekly - " + weeklyTitleList.toString());
        Log.i("XJZZU-FullDatabase", "Weekly - " + weeklyTextList.toString());
        Log.i("XJZZU-FullDatabase", "Weekly - " + weeklyImageList.toString());
        Log.i("XJZZU-FullDatabase", "Daily - " + weeklyMediaStringList.toString());
        Log.i("XJZZU-FullDatabase", "Daily - " + weeklyTagStringList.toString());

        Log.i("XJZZU-FullDatabase", "Monthly - " + monthlyReferenceNunberList.toString());
        Log.i("XJZZU-FullDatabase", "Monthly - " + monthlyTitleList.toString());
        Log.i("XJZZU-FullDatabase", "Monthly - " + monthlyTextList.toString());
        Log.i("XJZZU-FullDatabase", "Monthly - " + monthlyImageList.toString());
        Log.i("XJZZU-FullDatabase", "Daily - " + monthlyMediaStringList.toString());
        Log.i("XJZZU-FullDatabase", "Daily - " + monthlyTagStringList.toString());

        Log.i("XJZZU-FullDatabase", "Yearly - " + yearlyReferenceNunberList.toString());
        Log.i("XJZZU-FullDatabase", "Yearly - " + yearlyTitleList.toString());
        Log.i("XJZZU-FullDatabase", "Yearly - " + yearlyTextList.toString());
        Log.i("XJZZU-FullDatabase", "Yearly - " + yearlyImageList.toString());
        Log.i("XJZZU-FullDatabase", "Daily - " + yearlyMediaStringList.toString());
        Log.i("XJZZU-FullDatabase", "Daily - " + yearlyTagStringList.toString());

        Log.i("XJZZU-FullDatabase", "mainTagsList " + mainTagsArrayList.toString());
    }
}

 */























