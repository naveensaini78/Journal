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
import java.util.Calendar;
import java.util.Date;

public class CCOtherEntriesRecyclerAdapter extends RecyclerView.Adapter<CCOtherEntriesRecyclerAdapter.ViewHolder> {


    private ArrayList<Long> referenceNumberList;
    private ArrayList<String> titleList;
    private ArrayList<String> textList;
    private ArrayList<Bitmap> imageList;
    private ArrayList<String> mediaStringList;
    private ArrayList<String> tagsList;
    private int entryType;

    private Context context;

    String dateString;

    public CCOtherEntriesRecyclerAdapter(Context context, int entryType,
                                         ArrayList<Long> referenceNumberList,
                                         ArrayList<String> titleList, ArrayList<String> textList,
                                         ArrayList<Bitmap> imageList, ArrayList<String> mediaStringList,
                                         ArrayList<String> tagStringList) {
        this.context = context;
        this.referenceNumberList = referenceNumberList;
        this.titleList = titleList;
        this.textList = textList;
        this.imageList = imageList;
        this.entryType = entryType;
        this.mediaStringList = mediaStringList;
        this.tagsList = tagStringList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cd_other_recycler_entry, parent, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.i("Nnsi", "DailyAdapter");
        makeDatesFromLong(entryType, referenceNumberList.get(position));

        String title = titleList.get(position);
        try {
            String letter = title.substring(0, 1);
            holder.cAudioTileLetter.setText(letter);

        } catch (Exception e) {
            // To set logs
        }

        holder.cDateOneText.setText(dateString);
        holder.cTitleText.setText(title);
        holder.cEntryDemoText.setText(textList.get(position));
        if (imageList.get(position) != null) {
            holder.cEntryImage.setImageBitmap(imageList.get(position));
        }


        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, DAEntryViewActivity.class);
            intent.putExtra("cToD", "EntryView");
            intent.putExtra("entryReferenceNumber", referenceNumberList.get(position));
            intent.putExtra("entryTitle", titleList.get(position));
            intent.putExtra("entryText", textList.get(position));
            intent.putExtra("entryType", entryType);
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

                        //Deleting Sql entry
                        deleteEntry(entryType, position);


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


                    })
                    .setNegativeButton("No", null)
                    .show();


            return false;
        });

    }


    @Override
    public int getItemCount() {
        return referenceNumberList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView cTitleText;
        protected TextView cEntryDemoText;
        protected TextView cDateOneText;
        protected TextView cAudioTileLetter;
        protected ImageView cEntryImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cTitleText = itemView.findViewById(R.id.cTitleText);
            cEntryDemoText = itemView.findViewById(R.id.cEntryDemoText);
            cDateOneText = itemView.findViewById(R.id.cDateText);
            cAudioTileLetter = itemView.findViewById(R.id.cAudioTileLetter);
            cEntryImage = itemView.findViewById(R.id.cEntryImage);


        }
    }


    private void makeDatesFromLong(int entryType, long timeInMillis) {

        Date date = new Date(timeInMillis);

        switch (entryType) {
            case 1:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                dateString = "Week : " + calendar.get(Calendar.WEEK_OF_YEAR);
                break;
            case 2:
                DateFormat monthExtractor = new SimpleDateFormat("MM");
                int monthNumber = Integer.parseInt(monthExtractor.format(date));

                dateString = new DateFormatSymbols().getMonths()[monthNumber - 1];
                break;
            case 3:
                DateFormat yearExtractor = new SimpleDateFormat("yy");

                dateString = yearExtractor.format(date);
                break;

        }
    }


    private void deleteEntry(int entryType, int position) {

        // To be placed within SQLhelper Class
        SQLiteDatabase sqlDataBase = AApplicationClass.zzUtilitiesClass.getWritableDatabase();
        sqlDataBase.execSQL("DELETE FROM entriesTable WHERE entryReferenceNumber = " +
                referenceNumberList.get(position));


        switch (entryType) {
            case 1:
                AApplicationClass.weeklyReferenceNunberList.remove(position);
                AApplicationClass.weeklyTitleList.remove(position);
                AApplicationClass.weeklyTextList.remove(position);
                AApplicationClass.weeklyImageList.remove(position);
                AApplicationClass.weeklyMediaStringList.remove(position);
                AApplicationClass.weeklyTagStringList.remove(position);

                CBWeeklyFragment.cbWeeklyAdapter.notifyDataSetChanged();
                break;

            case 2:
                AApplicationClass.monthlyReferenceNunberList.remove(position);
                AApplicationClass.monthlyTitleList.remove(position);
                AApplicationClass.monthlyTextList.remove(position);
                AApplicationClass.monthlyImageList.remove(position);
                AApplicationClass.monthlyMediaStringList.remove(position);
                AApplicationClass.monthlyTagStringList.remove(position);

                CBMonthlyFragment.cbMonthlyAdapter.notifyDataSetChanged();
                break;
            case 3:
                AApplicationClass.yearlyReferenceNunberList.remove(position);
                AApplicationClass.yearlyTitleList.remove(position);
                AApplicationClass.yearlyTextList.remove(position);
                AApplicationClass.yearlyImageList.remove(position);
                AApplicationClass.yearlyMediaStringList.remove(position);
                AApplicationClass.yearlyTagStringList.remove(position);

                CBYearlyFragment.cbYearlyAdapter.notifyDataSetChanged();
                break;
        }

    }

}
















