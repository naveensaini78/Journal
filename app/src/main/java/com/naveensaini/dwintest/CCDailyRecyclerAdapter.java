package com.naveensaini.dwintest;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CCDailyRecyclerAdapter extends RecyclerView.Adapter<CCDailyRecyclerAdapter.ViewHolder> {


    private Context context;
    private ArrayList<Long> referenceNumberList;
    private ArrayList<String> titleList;
    private ArrayList<String> textList;
    private ArrayList<Bitmap> imageList;
    private ArrayList<String> mediaStringList;
    private ArrayList<String> tagsList;


    String dateOneString;
    String timeString;
    String dateTwoString;


    public CCDailyRecyclerAdapter(Context context, int i, ArrayList<Long> dailyReferenceNunberList,
                                  ArrayList<String> dailyTitleList, ArrayList<String> dailyTextList,
                                  ArrayList<Bitmap> dailyImageList, ArrayList<String> dailyMediaStringList,
                                  ArrayList<String> dailyTagStringList) {
        this.context = context;
        referenceNumberList = dailyReferenceNunberList;
        titleList = dailyTitleList;
        textList = dailyTextList;
        imageList = dailyImageList;
        mediaStringList = dailyMediaStringList;
        tagsList = dailyTagStringList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cd_daily_recycler_entry, parent, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.i("XJCCDR", "DailyAdapter");
        makeDatesFromLong(referenceNumberList.get(position));

        String title = titleList.get(position);
        try {
            String letter = title.substring(0, 1);
            holder.cAudioTileLetter.setText(letter);

        } catch (Exception e) {
            // To set logs
        }

        if (imageList.get(position) != null) {
            holder.cEntryImage.setImageBitmap(imageList.get(position));
        }
        holder.cDateOneText.setText(dateOneString);
        holder.cDateTwoText.setText(dateTwoString);
        holder.cTimeText.setText(timeString);
        holder.cTitleText.setText(title);
        holder.cEntryDemoText.setText(this.textList.get(position));

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, DAEntryViewActivity.class);
            intent.putExtra("cToD", "EntryView");
            intent.putExtra("entryReferenceNumber", referenceNumberList.get(position));
            intent.putExtra("entryTitle", titleList.get(position));
            intent.putExtra("entryText", textList.get(position));
            intent.putExtra("entryType", 0);
            intent.putExtra("entryMediaString", mediaStringList.get(position));
            intent.putExtra("entryTags", tagsList.get(position));
            intent.putExtra("adapterPosition", position);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);

        });

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getRootView().getContext()).setTitle("Delete Entry")
                    .setMessage("Do you want to delete this entry!")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        // Deleting Media to be incorporated
                        //  //
                        //
                        //  //
						/*
						String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AVLog/Recordings";
						String fileName = entryReferenceNumberList.get(position) + "AudioRecording.3gp";
						File newFile = new File(filePath, fileName);
						newFile.delete();
						 */

                        //Deleting Sql entry
                        // To be placed within SQLhelper Class
                        SQLiteDatabase sqlDataBase = AApplicationClass.zzUtilitiesClass.getWritableDatabase();
                        sqlDataBase.execSQL("DELETE FROM entriesTable WHERE entryReferenceNumber = " +
                                AApplicationClass.dailyReferenceNunberList.get(position));

                        AApplicationClass.dailyReferenceNunberList.remove(position);
                        AApplicationClass.dailyTitleList.remove(position);
                        AApplicationClass.dailyTextList.remove(position);
                        AApplicationClass.dailyImageList.remove(position);
                        AApplicationClass.dailyMediaStringList.remove(position);
                        AApplicationClass.dailyTagStringList.remove(position);

                        CBDailyFragment.CCDailyRecyclerAdapter.notifyDataSetChanged();

                    })
                    .setNegativeButton("No", null)
                    .show();


            return false;
        });

    }


    @Override
    public int getItemCount() {
        return AApplicationClass.dailyReferenceNunberList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView cTitleText;
        protected TextView cEntryDemoText;
        protected TextView cTimeText;
        protected TextView cDateOneText;
        protected TextView cDateTwoText;
        protected TextView cAudioTileLetter;
        protected ImageView cEntryImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cTitleText = itemView.findViewById(R.id.cTitleText);
            cEntryDemoText = itemView.findViewById(R.id.cEntryDemoText);
            cTimeText = itemView.findViewById(R.id.cTimeText);
            cDateOneText = itemView.findViewById(R.id.cDateOneText);
            cDateTwoText = itemView.findViewById(R.id.cDateTwoText);
            cAudioTileLetter = itemView.findViewById(R.id.cAudioTileLetter);
            cEntryImage = itemView.findViewById(R.id.cEntryImage);


        }
    }


    private void makeDatesFromLong(long timeInMillis) {

        Date date = new Date(timeInMillis);
        DateFormat todayDateExtractor = new SimpleDateFormat("dd");
        dateOneString = todayDateExtractor.format(date);

        DateFormat monthExtractor = new SimpleDateFormat("MM");
        int monthNumber = Integer.parseInt(monthExtractor.format(date));
        String bigMonthName = new DateFormatSymbols().getMonths()[monthNumber - 1];
        String monthName = bigMonthName.substring(0, 3);

        DateFormat yearExtractor = new SimpleDateFormat("yy");

        dateTwoString = monthName + ", " + yearExtractor.format(date);

        ////
        DateFormat timeExtractor = new SimpleDateFormat("hh:mm a");
        timeString = timeExtractor.format(date);

    }
}
















