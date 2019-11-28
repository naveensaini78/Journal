package com.naveensaini.dwintest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naveensaini.dwintest.level_a_activities.BPasscodeActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class CAAllEntriesActivity extends AppCompatActivity {


    FloatingActionButton cPlusButton;
    RelativeLayout cAddEntryFABLayout;

    public static final int RequestPermissionCode = 1;


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean WriteStoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean CameraPermission = grantResults[2] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[3] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (WriteStoragePermission && RecordPermission && CameraPermission && ReadStoragePermission) {
                        Toast.makeText(CAAllEntriesActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(CAAllEntriesActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.c_top_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.cMenuChangePasscode:
                finish();
                Intent intent = new Intent(this, BPasscodeActivity.class);
                intent.putExtra("cToBPasscodeChange", true);
                startActivity(intent);
                break;
            case R.id.cMenuTestingActivity:
                finish();
                startActivity(new Intent(this, ZOTestingActivity.class));
                break;
            case R.id.cMenuLogDataBase:
                AApplicationClass.logDataBase();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_all_entries);
        Log.i("naveenSaini", "CAAllEntriesActivity - onCreate()");


        /////// Defining Variables /////////////
        cPlusButton = findViewById(R.id.cPlusButton);
        cAddEntryFABLayout = findViewById(R.id.cAddEntryFABLayout);


        Toolbar toolbar = findViewById(R.id.cToolbar);
        setSupportActionBar(toolbar);


        ///////////////////////////////////////////////////////
        checkPermissionsAndCreateDirectory();


        ////////// Setting the Duo Drawer menu //////////////////
        DuoDrawerLayout duoDrawerLayout = findViewById(R.id.cDuoDrawer);
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle
                (this, duoDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        duoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

        Button cLogoutButton = findViewById(R.id.cDuoDrawerLogoutButton);
        cLogoutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Nothing here yet", Toast.LENGTH_SHORT).show();
        });

        DuoMenuView duoMenuView = findViewById(R.id.cDuoDrawerMenuView);
        ArrayList<String> bOptionViewArrayList = new ArrayList<>();
        bOptionViewArrayList.add("Settings");

        CDDuoMenuAdapter cBDuoMenuAdapter = new CDDuoMenuAdapter(this, bOptionViewArrayList);
        duoMenuView.setAdapter(cBDuoMenuAdapter);

        duoMenuView.setOnMenuClickListener(new DuoMenuView.OnMenuClickListener() {
            @Override
            public void onFooterClicked() {

            }

            @Override
            public void onHeaderClicked() {

            }

            @Override
            public void onOptionClicked(int position, Object objectClicked) {
                switch (position) {
                    case 0:
                        Log.i("MenuItemClicked", "Settings Activity");
                        startActivity(new Intent(CAAllEntriesActivity.this, ESettingsActivity.class));
                        break;
                }
            }
        });


        ////////// BottomNavigationBar//////////////////////////
        BottomNavigationView cBottomNavigationView = findViewById(R.id.cBottomNavBar);
        NavHostFragment cNavHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.cNavHostFragment);
        NavController navController = cNavHostFragment.getNavController();
        NavigationUI.setupWithNavController(cBottomNavigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
                Log.i("XJDAE-NaviDestination", destination.getLabel() + ", " + destination.getId()));


        /////////// New Entry /////////////////
        cPlusButton.setOnClickListener(v -> {
            Log.i("Plus Button", "Plus");


            Intent intent = new Intent(this, DAEntryViewActivity.class);
            intent.putExtra("cToD", "EntryRecord");


            intent.putExtra("entryType", getEntryType(navController));
            startActivity(intent);


        });
    }


    ////////////////////////////////////////////
    public void checkPermissionsAndCreateDirectory() {

        //// Permissions
        if (!checkPermission()) {
            requestPermission();
        }


        ////// Directory
        File myDataFolder = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.main_folder_name));
        if (!myDataFolder.exists()) {
            if (myDataFolder.mkdirs()) {
                Log.i("Create Directory", "Successful");

                File mediaFolder = new File(myDataFolder, getResources().getString(R.string.media_folder_name));
                if (!mediaFolder.exists()) {
                    if (mediaFolder.mkdirs()) {
                        Log.i("Create Directory", "Successful");

                    } else {
                        Log.i("Create Directory", "Unsuccessful");

                        checkPermissionsAndCreateDirectory();
                    }
                } else {
                    Log.i("path", mediaFolder.getAbsolutePath());

                }
            } else {
                Log.i("Create Directory", "Unsuccessful");

                checkPermissionsAndCreateDirectory();

            }
        }
    }


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),
                CAMERA);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED;

    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(CAAllEntriesActivity.this,
                new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, CAMERA, READ_EXTERNAL_STORAGE},
                RequestPermissionCode);

    }


    private int getEntryType(NavController navController) {
        String navDestination = (String) navController.getCurrentDestination().getLabel();

        Log.i("XJDAE-getEntryType", navDestination);

        if (navDestination.contains("daily")) {
            return 0;
        } else if (navDestination.contains("weekly")) {
            return 1;
        } else if (navDestination.contains("monthly")) {
            return 2;
        } else if (navDestination.contains("yearly")) {
            return 3;
        } else {
            return 4;
        }

    }


    public void exportDataBase() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        Log.i("database", Environment.getDataDirectory().getAbsolutePath());

        try {
            if (sd.canWrite()) {
                Log.i("database", "can Write");
                String currentDBPath = "/data/com.naveensaini.avlogs/databases/entriesDatabase.db";
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
/* Other old codes
public void toggleButtonRiseAnimations(FloatingActionButton plusButton, FloatingActionButton buttonOne,
										   int translationValue, FloatingActionButton buttonTwo,
										   int translationValueTwo, int duration) {

		if (raiseTheButton) {
			plusButton.animate().rotationBy(135).setDuration(duration).translationZBy(-100).start();
			buttonOne.animate().translationYBy(-translationValue).setDuration(duration).start();
			buttonTwo.animate().translationYBy(-translationValueTwo).setDuration(duration).start();
			ObjectAnimator.ofObject(cAddEntryFABLayout, "backgroundColor", new ArgbEvaluator(), Color.TRANSPARENT, ContextCompat.getColor(this, R.color.colorPrimaryFaded))
					.setDuration(300)
					.start();
			raiseTheButton = false;
		} else {
			plusButton.animate().rotationBy(-135).setDuration(duration).translationZBy(100).start();
			buttonOne.animate().translationYBy(translationValue).setDuration(duration).start();
			buttonTwo.animate().translationYBy(translationValueTwo).setDuration(duration).start();
			ObjectAnimator.ofObject(cAddEntryFABLayout, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.colorPrimaryFaded), Color.TRANSPARENT)
					.setDuration(300)
					.start();
			raiseTheButton = true;
		}
	}




	public void cPlusClick(View view) {
		Log.i("Plus Button", "Plus");
		toggleButtonRiseAnimations(cPlusButton, cRecordAudioButton, 175, cRecordVideoButton, 325, 200);

		exportDataBase();

	}


	public void cRecordAudioClick(View view) {
		Log.i("Plus Button", "Audio");

		Intent intent = new Intent(this, DAEntryViewActivity.class);
		intent.putExtra("cToD", "AudioRecord");
		startActivity(intent);

		cRecordAudioButton.setEnabled(false);

		toggleButtonRiseAnimations(cPlusButton, cRecordAudioButton, 175, cRecordVideoButton, 325, 200);

	}


	public void cRecordVideoClick(View view) {
		Log.i("Plus Button", "Audio");

		Intent intent = new Intent(this, DAEntryViewActivity.class);
		intent.putExtra("cToD", "VideoRecord");
		startActivity(intent);

		cRecordAudioButton.setEnabled(false);

		toggleButtonRiseAnimations(cPlusButton, cRecordAudioButton, 175, cRecordVideoButton, 325, 200);
	}







 */