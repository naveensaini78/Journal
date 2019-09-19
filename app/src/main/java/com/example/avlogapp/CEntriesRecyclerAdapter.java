package com.example.avlogapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CEntriesRecyclerAdapter extends RecyclerView.Adapter<CEntriesRecyclerAdapter.ViewHolder> {


	private ArrayList<Long> entryReferenceNumberList;
	private ArrayList<String> entryTitleList;
	private ArrayList<String> entryTextList;
	private Context context;

	String dateOneString;
	String timeString;
	String dateTwoString;


	public CEntriesRecyclerAdapter(Context context, ArrayList<Long> entryReferenceNumberList, ArrayList<String> entryTitleList, ArrayList<String> entryTextList) {
		this.entryReferenceNumberList = entryReferenceNumberList;
		this.entryTitleList = entryTitleList;
		this.entryTextList = entryTextList;
		this.context = context;

	}


	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.activity_c_recycler_entry, parent, false);
		return new ViewHolder(view);

	}


	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

		makeDatesFromLong(entryReferenceNumberList.get(position));
		holder.cDateOneText.setText(dateOneString);
		holder.cDateTwoText.setText(dateTwoString);
		holder.cTimeText.setText(timeString);

		String title = entryTitleList.get(position);
		try {
			String letter = title.substring(0, 1);
			holder.cAudioTileLetter.setText(letter);

		} catch (Exception e) {

			// To set logs
		}

		holder.cTitleText.setText(title);
		holder.cEntryDemoText.setText(entryTextList.get(position));


		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(context, DEntryViewActivity.class);
				intent.putExtra("cToD", "displayTheEntry");
				intent.putExtra("cToDReferenceNumber", entryReferenceNumberList.get(position));
				intent.putExtra("cToDTitle", entryTitleList.get(position));
				intent.putExtra("cToDText", entryTextList.get(position));
				intent.putExtra("cToDAdapterPosition", position);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);

			}
		});

		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new AlertDialog.Builder(holder.itemView.getRootView().getContext()).setTitle("Delete Entry")
						.setMessage("Do you want to delete this entry!")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								// Deleting Recording
								String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AVLog/Recordings";
								String fileName = entryReferenceNumberList.get(position) + "AudioRecording.3gp";
								File newFile = new File(filePath, fileName);
								newFile.delete();

								//Sql
								CAllEntriesActivity.entriesSql.execSQL
										("DELETE FROM entriesTable WHERE entryReferenceNumber = " + entryReferenceNumberList.get(position));

								CAllEntriesActivity.entryReferenceNumberList.remove(position);
								CAllEntriesActivity.entryTitleList.remove(position);
								CAllEntriesActivity.entryTextList.remove(position);
								CAllEntriesActivity.cEntriesRecyclerAdapter.notifyDataSetChanged();


							}
						})
						.setNegativeButton("No", null)
						.show();


				return false;
			}
		});

	}


	@Override
	public int getItemCount() {
		return entryReferenceNumberList.size();
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
















