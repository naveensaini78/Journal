package com.naveensaini.dwintest;

/*
public class ZZSqliteOpenHelper extends SQLiteOpenHelper {

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



    public ZZSqliteOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TABLE_ENTRIES_DATA
        // ReferenceNumber  |  Title  |  EntryText  |  EntryType(0,1,2,3 for d,w,m,y)  |  EntryMedia | EntryTags |
        String createTableOne = "CREATE TABLE IF NOT EXISTS " + TABLE_ENTRIES_DATA + "(" +
                ERN + " INTEGER, " +
                ENTRY_TITLE + " VARCHAR, " +
                ENTRY_TEXT + " VARCHAR, " +
                ENTRY_TYPE + " INTEGER, " +
                ENTRY_MEDIA + " VARCHAR, " +
                ENTRY_TAGS + " VARCHAR)";
        db.execSQL(createTableOne);


        // TABLE 2 - appInfoTable
        // rowNumber  |  mainTagsString |
        String createTableTwo = "CREATE TABLE IF NOT EXISTS " + TABLE_APP_INFO + "(" +
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
        SQLiteDatabase entriesSql = this.getReadableDatabase();

        clearExistingLists();

        //////// Fetching Data from Sql ///////
        /// TABLE 1
        try {
            Cursor cursor = entriesSql.rawQuery("SELECT * FROM " + TABLE_ENTRIES_DATA, null);
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
            Cursor cursor = entriesSql.rawQuery("SELECT mainTagsString FROM appInfoTable WHERE rowNumber = 1", null);
            int mainTagsIndex = cursor.getColumnIndex("mainTagsString");
            cursor.moveToFirst();

            String mainTagsString = cursor.getString(mainTagsIndex);

            Log.i("XJZZS-getCellForValue", mainTagsString);


            // Setting Values to MainTagsArrayList
            if (mainTagsString != null){
                AApplicationClass.mainTagsArrayList = (ArrayList<String>) ZZObjectSerializer.deserialize(mainTagsString);
            }

        } catch (Exception e){
            entriesSql.execSQL("UPDATE appInfoTable SET mainTagsString = null WHERE rowNumber = 1");

            Log.i("XJZZS-loadDataT2", e.toString());
        }

        entriesSql.close();
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

        SQLiteDatabase entriesSql = this.getWritableDatabase();

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
        entriesSql.close();

        Log.i("XJZZU-addRowToDataBase", addRowString);
    }


    public void addNewMainTagsToDataBase(){
        SQLiteDatabase entriesSql = this.getWritableDatabase();

        String mainTagsNewString = null;
        try {
            mainTagsNewString = ZZObjectSerializer.serialize(AApplicationClass.mainTagsArrayList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        entriesSql.execSQL("UPDATE appInfoTable SET mainTagsString = '"+ mainTagsNewString +"' WHERE rowNumber = 1");
        entriesSql.close();
    }


    public String getCellForValue(long dEntryReferenceNumber, String columnName) {
        SQLiteDatabase entriesSql = this.getReadableDatabase();

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

            entriesSql.close();
            return newString;
        } catch (Exception e) {
            Log.i("XJZZU-getCellForValue", e.toString());

            entriesSql.close();
            return "";
        }

    }


    public void addMediaToDataBase(long entryReferenceNumber, String mediaName) {
        SQLiteDatabase entriesSql = this.getWritableDatabase();

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

        entriesSql.close();
    }


    public void addNewTagsStringToDataBase (long entryReferenceNumber, String tagsString){
        SQLiteDatabase entriesSql = this.getWritableDatabase();

        entriesSql.execSQL("UPDATE entriesTable SET entryTags = '" + tagsString +
                "' WHERE entryReferenceNumber = " + entryReferenceNumber);

        Log.i("XJZZU-newTagsString", tagsString);

        entriesSql.close();
    }







}

 */



