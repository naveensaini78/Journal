package com.example.avlogapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class CAllEntriesActivity extends AppCompatActivity {


	FloatingActionButton cPlusButton, cRecordAudioButton, cRecordVideoButton;
	boolean raiseTheButton;

	public static final int RequestPermissionCode = 1;
	static SQLiteDatabase entriesSql;

	static ArrayList<Long> entryReferenceNumberList = new ArrayList<>();
	static ArrayList<String> entryTitleList = new ArrayList<>();
	static ArrayList<String> entryTextList = new ArrayList<>();
	static CEntriesRecyclerAdapter cEntriesRecyclerAdapter;


	public void cPlusClick(View view) {
		Log.i("Plus Button", "Plus");
		toggleButtonRiseAnimations(cPlusButton, cRecordAudioButton, 175, cRecordVideoButton, 325, 200);

		exportDataBase();

	}


	public void cRecordAudioClick(View view) {
		Log.i("Plus Button", "Audio");

		Intent intent = new Intent(this, DEntryViewActivity.class);
		intent.putExtra("cToD", "startRecordingAudio");
		startActivity(intent);

		cRecordAudioButton.setEnabled(false);

		toggleButtonRiseAnimations(cPlusButton, cRecordAudioButton, 175, cRecordVideoButton, 325, 200);

	}


	public void cRecordVideoClick(View view) {
		Log.i("Plus Button", "Video");
		toggleButtonRiseAnimations(cPlusButton, cRecordAudioButton, 175, cRecordVideoButton, 325, 200);
	}


	public void toggleButtonRiseAnimations(FloatingActionButton plusButton, FloatingActionButton buttonOne,
										   int translationValue, FloatingActionButton buttonTwo,
										   int translationValueTwo, int duration) {

		if (raiseTheButton) {
			plusButton.animate().rotationBy(135).setDuration(duration).start();
			buttonOne.animate().translationYBy(-translationValue).setDuration(duration).start();
			buttonTwo.animate().translationYBy(-translationValueTwo).setDuration(duration).start();
			raiseTheButton = false;
		} else {
			plusButton.animate().rotationBy(-135).setDuration(duration).start();
			buttonOne.animate().translationYBy(translationValue).setDuration(duration).start();
			buttonTwo.animate().translationYBy(translationValueTwo).setDuration(duration).start();
			raiseTheButton = true;
		}
		cPlusButton.bringToFront();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.c_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.cMenuChangePasscode:
				finish();
				String action;
				Intent intent = new Intent(this, BPasscodeActivity.class);
				intent.putExtra("cToBPasscodeChange", true);
				startActivity(intent);

		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onResume() {
		super.onResume();
		cRecordAudioButton.setEnabled(true);
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

		switch (requestCode) {
			case RequestPermissionCode:
				if (grantResults.length > 0) {
					boolean StoragePermission = grantResults[0] ==
							PackageManager.PERMISSION_GRANTED;
					boolean RecordPermission = grantResults[1] ==
							PackageManager.PERMISSION_GRANTED;

					if (StoragePermission && RecordPermission) {
						Toast.makeText(CAllEntriesActivity.this, "Permission Granted",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(CAllEntriesActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
					}
				}
				break;
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_c_all_entries);


		/////// Defining Variables /////////////
		cPlusButton = findViewById(R.id.cPlusButton);
		cRecordAudioButton = findViewById(R.id.cAudioRecordButton);
		cRecordVideoButton = findViewById(R.id.cVideoRecordButton);
		raiseTheButton = true;
		entriesSql = this.openOrCreateDatabase("entriesDatabase", MODE_PRIVATE, null);
		entriesSql.execSQL("CREATE TABLE IF NOT EXISTS entriesTable(id INTEGER PRIMARY KEY, entryReferenceNumber INTEGER, entryTitle VARCHAR, entryText VARCHAR)");
		Log.i("dbPath", entriesSql.getPath());


		////////// Get Permissions and Make Directory ////////////
		if (!checkPermission()) {
			requestPermission();
		}
		createDirectoryIfNotExists();


		//////// Fetching Data from Sql ///////
		try {
			Cursor cursor = entriesSql.rawQuery("SELECT * FROM entriesTable", null);
			int entryReferenceNumberIndex = cursor.getColumnIndex("entryReferenceNumber");
			int entryHeading = cursor.getColumnIndex("entryTitle");
			int entryText = cursor.getColumnIndex("entryText");
			cursor.moveToFirst();

			do {
				entryReferenceNumberList.add(cursor.getLong(entryReferenceNumberIndex));
				entryTitleList.add(cursor.getString(entryHeading));
				entryTextList.add(cursor.getString(entryText));

				Log.i("SqlTest", cursor.getLong(entryReferenceNumberIndex) + "," + cursor.getString(entryHeading) + "," + cursor.getString(entryText));
			} while (cursor.moveToNext());

			Log.i("ListOne", entryReferenceNumberList.toString());
			Log.i("ListTwo", entryTitleList.toString());
			Log.i("ListThree", entryTextList.toString());


		} catch (Exception e) {
			Log.i("Fetching Data", e.toString());
		}


		//////// Setting Entries List //////////
		RecyclerView cEntriesRecyclerView = findViewById(R.id.cEntriesRecyclerView);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setReverseLayout(true);
		linearLayoutManager.setStackFromEnd(true);
		cEntriesRecyclerView.setLayoutManager(linearLayoutManager);
		cEntriesRecyclerAdapter = new CEntriesRecyclerAdapter(getApplicationContext(), entryReferenceNumberList, entryTitleList, entryTextList);
		cEntriesRecyclerView.setAdapter(cEntriesRecyclerAdapter);

	}


	public void createDirectoryIfNotExists() {

		File myDataFolder = new File(Environment.getExternalStorageDirectory(), "AVLog");
		if (!myDataFolder.exists()) {
			if (myDataFolder.mkdirs()) {
				Log.i("Create Directory", "Successful");

				File recordingsFolder = new File(myDataFolder, "Recordings");
				if (!recordingsFolder.exists()) {
					if (recordingsFolder.mkdirs()) {
						Log.i("Create Directory", "Successful");

					} else {
						Log.i("Create Directory", "Unsuccessful");

						createDirectoryIfNotExists();
					}
				} else {
					Log.i("path", recordingsFolder.getAbsolutePath());

				}
			} else {
				Log.i("Create Directory", "Unsuccessful");

				createDirectoryIfNotExists();

			}
		}
	}


	public boolean checkPermission() {
		int result = ContextCompat.checkSelfPermission(getApplicationContext(),
				WRITE_EXTERNAL_STORAGE);
		int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
				RECORD_AUDIO);
		return result == PackageManager.PERMISSION_GRANTED &&
				result1 == PackageManager.PERMISSION_GRANTED;

	}


	private void requestPermission() {
		ActivityCompat.requestPermissions(CAllEntriesActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);

	}


	public void exportDataBase() {
		File sd = Environment.getExternalStorageDirectory();
		File data = Environment.getDataDirectory();
		Log.i("database", Environment.getDataDirectory().getAbsolutePath());

		try {
			if (sd.canWrite()) {
				Log.i("database", "can Write");
				String currentDBPath = "/data/com.example.avlogs/databases/entriesDatabase.db";
				String copiedDBPath = "aaaa.db";
				File currentDb = new File(data, currentDBPath);
				File copiedDb = new File(sd, copiedDBPath);

				if (currentDb.exists()) {
					Log.i("database", "found");


					FileChannel src = new FileInputStream(currentDb).getChannel();

					FileChannel dst = new FileOutputStream(copiedDb).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}


			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
