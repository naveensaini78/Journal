package com.naveensaini.dwintest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.naveensaini.dwintest.z_other_classes.AudioData;
import com.naveensaini.dwintest.z_other_classes.VideoData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DAEntryViewActivity extends AppCompatActivity {


    EditText dHeadingText, dEntryText;
    static TextView dCancleButtonAB;
    TextView dTagsButton;


    public static long dEntryReferenceNumber;
    public static String dMediaPath;
    public static int dMediaCount;
    int dPassedPosition;
    public int dEntryType;
    static String dMediaString;
    static String dTagsString;

    String photoName;
    Uri capturedImageUri;

    RecyclerView dEntryImagesRecyclerView;


    Runnable onActivityResultRunnable;

    public static VideoView dVideoPlayerView;


    ArrayList<Bitmap> dImagesList = new ArrayList();
    public static ArrayList<VideoData> dVideoDataList = new ArrayList();
    public static ArrayList<AudioData> dAudioDataList = new ArrayList();
    static DCImagesRecyclerAdapter DCImagesRecyclerAdapter;
    public static DCVideosRecyclerAdapter DCVideosRecyclerAdapter;
    public static DCAudioRecyclerAdapter DCAudioRecyclerAdapter;


    ///////////////////////////////////////////////////////////
    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return super.onSupportNavigateUp();
    }


    ///////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.da_activity_entry_view);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout dCollapsingToolbarLayout = findViewById(R.id.dCollapsingToolbarLayout);
        dCollapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ///////////////////////// Defining Variables //////////

        dHeadingText = findViewById(R.id.dHeadingText);
        dEntryText = findViewById(R.id.dEntryText);
        dTagsButton = findViewById(R.id.dTagsButton);

        ImageView dPhotosButtonAB = findViewById(R.id.dPhotosButtonAB);
        ImageView dAudioButtonAB = findViewById(R.id.dAudioButtonAB);
        ImageView dVideoButtonAB = findViewById(R.id.dVideoButtonAB);

        FrameLayout dRecordingLayoutFrame = findViewById(R.id.dAudioVideoRecordFrame);
        dCancleButtonAB = findViewById(R.id.dCancleButtonAB);

        InputMethodManager dInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        ////////////////////////////////////////////////////////
        Intent cToDIntent = getIntent();
        guideTheActivity(cToDIntent);

        ignoreUriExposure();


        // WARNING!!!-  These should be after the heading and entry has been set
        dHeadingText.addTextChangedListener(new TextWatcherClass(dHeadingText));
        dEntryText.addTextChangedListener(new TextWatcherClass(dEntryText));


        // Tags Task
        // WARNING!!! - Should be after guideTheActivity()
        setTagsText(dTagsString);
        dTagsButton.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.dd_tags_popup, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT, true);

            // Setting TagsList Ready
            RecyclerView tagsRecyclerView = popupView.findViewById(R.id.ddTagsRecyclerView);
            tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            DDTagsRecyclerAdapter ddTagsRecyclerAdapter = new DDTagsRecyclerAdapter
                    (this, AApplicationClass.mainTagsArrayList, dTagsString, dEntryType, dPassedPosition);
            tagsRecyclerView.setAdapter(ddTagsRecyclerAdapter);

            // Notifying adapter of changes
            popupWindow.setOnDismissListener(() -> {
                Log.i("XJDAE-tagsPopup", "onDismiss");
                ddTagsRecyclerAdapter.notifyDataSetChanged();
            });

            // Show
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


            ////// ADD TAGS FUNCTIONS
            ImageView addTagsButton = popupView.findViewById(R.id.ddPopupAddTagButton);
            addTagsButton.setOnClickListener(v12 -> {
                LayoutInflater newTagInflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View newTagPopupView = newTagInflator.inflate(R.layout.de_new_tag_popup, null);
                PopupWindow newTagPopupWindow = new PopupWindow(newTagPopupView,
                        CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT, true);

                newTagPopupWindow.showAtLocation(newTagPopupView, Gravity.CENTER, 0, 0);

                // Dimming Background
                View containerView = newTagPopupWindow.getContentView().getRootView();
                if (containerView != null) {
                    WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) containerView.getLayoutParams();
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    layoutParams.dimAmount = 0.3f;
                    if (windowManager != null) {
                        windowManager.updateViewLayout(containerView, layoutParams);
                    }

                }

                // Hiding keyboard on Dismiss
                newTagPopupWindow.setOnDismissListener(() -> {
                    Log.i("XJDAE-addtagsPopup", "onDismiss");
                    dInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                });


                // Getting Focus on EditText
                EditText newTagText = newTagPopupView.findViewById(R.id.deNewTagEditText);
                newTagText.requestFocus();
                dInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


                // DoneButton
                TextView doneButton = newTagPopupView.findViewById(R.id.deNewTagDoneButton);
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newTag = newTagText.getText().toString();

                        AApplicationClass.mainTagsArrayList.add(newTag);
                        ddTagsRecyclerAdapter.notifyDataSetChanged();

                        AApplicationClass.zzUtilitiesClass.addNewMainTagsToDataBase();

                        newTagPopupWindow.dismiss();


                    }
                });

                //
                TextView cancleButton = newTagPopupView.findViewById(R.id.deNewTagCancleButton);
                cancleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newTagPopupWindow.dismiss();
                    }
                });
            });


            // Dismiss Window
            TextView doneButton = popupView.findViewById(R.id.ddPopupDoneButton);
            doneButton.setOnClickListener(v1 -> popupWindow.dismiss());

        });


        ////////////////////// Photos Task /////////////////
        String[] photosOptions = {"Take a new Photo", "Select Photos from Gallery"};
        dPhotosButtonAB.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select an Option");
            builder.setItems(photosOptions, (dialog, item) -> {
                switch (item) {
                    case 0:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        dMediaCount++;
                        photoName = String.valueOf(dEntryReferenceNumber) + dMediaCount + ".jpg";

                        File imageFile = new File(dMediaPath, photoName);
                        capturedImageUri = Uri.fromFile(imageFile);

                        Log.i("XJDAE-captureIntent", imageFile.getAbsolutePath().toString());

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                        intent.putExtra("photoName", photoName);

                        startActivityForResult(intent, 0);
                        break;
                    case 1:
                        Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent1, 1);
                        break;
                }
            }).setNegativeButton("Cancle", (dialog, which)
                    -> dialog.dismiss()).show();
        });

        ////////////////////////////AudioRecordTask///////
        dAudioButtonAB.setOnClickListener(v -> {
            try {
                if (getFragmentManager().findFragmentById(R.id.dAudioVideoRecordFrame) != null) {
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.dAudioVideoRecordFrame)).commit();
                }

                dEntryImagesRecyclerView.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dAudioVideoRecordFrame, new DBAudioFragment()).commit();

            dVideoButtonAB.setVisibility(View.VISIBLE);
            dAudioButtonAB.setVisibility(View.GONE);
            dCancleButtonAB.setVisibility(View.VISIBLE);

        });

        //////////////////////////////VideoRecordTask//////
        dVideoButtonAB.setOnClickListener(v -> {
            try {
                getSupportFragmentManager().beginTransaction()
                        .remove(getSupportFragmentManager().findFragmentById(R.id.dAudioVideoRecordFrame)).commit();

                dEntryImagesRecyclerView.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }


            getFragmentManager().beginTransaction()
                    .replace(R.id.dAudioVideoRecordFrame, new DBVideoFragment()).commit();

            dAudioButtonAB.setVisibility(View.VISIBLE);
            dVideoButtonAB.setVisibility(View.GONE);
            dCancleButtonAB.setVisibility(View.VISIBLE);

        });

        dCancleButtonAB.setOnClickListener(v -> {
            try {

                // AUDIO
                getSupportFragmentManager().beginTransaction()
                        .remove(getSupportFragmentManager()
                                .findFragmentById(R.id.dAudioVideoRecordFrame)).commit();

                dAudioButtonAB.setVisibility(View.VISIBLE);
                v.setVisibility(View.GONE);

            } catch (Exception e) {
                try {
                    // VIDEO
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager()
                                    .findFragmentById(R.id.dAudioVideoRecordFrame)).commit();

                    dVideoButtonAB.setVisibility(View.VISIBLE);
                    v.setVisibility(View.GONE);
                } catch (Exception ex) {
                    Log.i("XJDAE-cancleButton", ex.toString());
                }
                Log.i("XJDAE-cancleButton", e.toString());
            }

        });

    }


    public void setTagsText(String dTagsString) {
        String tagsString = ": ";

        Log.i("XJDAE-setTagsText", "tagsString - " + dTagsString);

        if (dTagsString != null) {
            ArrayList<String> tagsArrayList = new ArrayList<>();
            try {
                tagsArrayList = (ArrayList<String>) ZZObjectSerializer.deserialize(dTagsString);
                if (tagsArrayList != null && !tagsArrayList.isEmpty()) {
                    for (String tag : tagsArrayList) {
                        tagsString = tagsString + tag + ", ";
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dTagsButton.setText("TAGS " + tagsString);
    }


    private void ignoreUriExposure() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Activity", "Paused");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Activity", "Resumed");
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (onActivityResultRunnable != null) {
            onActivityResultRunnable.run();
            onActivityResultRunnable = null;
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Activity", "Stopped");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        onActivityResultRunnable = () -> {

            String photoName = null;
            Bitmap bitmap = null;

            if (requestCode == 0 && resultCode == RESULT_OK) { //Camera
                try {
                    Bitmap capturedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), capturedImageUri);
                    bitmap = compressBitmap(capturedImage);
                    capturedImage.recycle();

                    photoName = this.photoName;

                    // Deleting Large Catured Image
                    File toBeDeletedFile = new File(dMediaPath, photoName);
                    toBeDeletedFile.delete();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 1 && resultCode == RESULT_OK) { // Gallery
                if (data != null) {
                    Uri photoUri = data.getData();
                    try {
                        Bitmap bitmapFromGallery = MediaStore.Images.Media.getBitmap(
                                getApplicationContext().getContentResolver(), photoUri);
                        bitmap = compressBitmap(bitmapFromGallery);
                        bitmapFromGallery.recycle();

                        dMediaCount++;
                        photoName = dEntryReferenceNumber + String.valueOf(dMediaCount) + ".jpg";

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }


            // Common functions for Gallery and Capture Intent
            if (bitmap != null | photoName != null) {
                // Saving images to media folder
                saveImageFromGallery(bitmap, photoName);

                // Adding to RecyclerView
                dImagesList.add(bitmap);
                DCImagesRecyclerAdapter.notifyDataSetChanged();
                Log.i("XJDAE-ImagesListSize", String.valueOf(dImagesList.size()));

                // Adding to Database
                AApplicationClass.zzUtilitiesClass.addMediaToDataBase(dEntryReferenceNumber, photoName);

            }
        };
    }


    private void saveImageFromGallery(Bitmap recievedBitmap, String photoName) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        recievedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        File imageFile = new File(dMediaPath, photoName);
        try {
            imageFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{imageFile.getPath()},
                    new String[]{"image/jpeg"}, null);
            fileOutputStream.close();
            Log.i("XJDAE-saveGalleryBitmap", "Successful");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Bitmap compressBitmap(Bitmap recievedBitmap) {
        if (recievedBitmap.getWidth() <= 1080) {

            Log.i("XJDAE-compressBitmap",
                    "original-" + recievedBitmap.getWidth() + ", " + recievedBitmap.getHeight() + "; ");

            return recievedBitmap;
        } else {
            int width = 1080;
            double height = (width * recievedBitmap.getHeight()) / recievedBitmap.getWidth();

            Log.i("XJDAE-compressBitmap",
                    "original-" + recievedBitmap.getWidth() + ", " + recievedBitmap.getHeight() + "; "
                            + width + ", " + height);

            Bitmap newBitmap = Bitmap.createScaledBitmap(recievedBitmap, width, (int) height, true);

            return newBitmap;
        }

    }


    ////////////////////////////////////////////////////////////
    public void guideTheActivity(Intent intent) {
        dMediaCount = 0;
        dMediaPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + getResources().getString(R.string.storage_location);


        if (intent.getStringExtra("cToD").equals("EntryRecord")) {
            // Basic Variables
            dEntryReferenceNumber = System.currentTimeMillis();
            dEntryType = intent.getExtras().getInt("entryType");
            dPassedPosition = getPositionForNewEntry(dEntryType);
            dMediaString = "";
            dTagsString = null;

            // Add row to DB
            AApplicationClass.zzUtilitiesClass.addRowToDatabase(this, dEntryReferenceNumber, dEntryType);

            scrollRecyclerViewsToTop(dEntryType, dPassedPosition);

            Log.i("XJDAE-guideActivityRe", "EntryRecord - " +
                    dEntryReferenceNumber + ", " + dEntryType + ", " + dPassedPosition + ", " + dMediaString + ", " + dTagsString);


        } else if (intent.getStringExtra("cToD").equals("EntryView")) {
            // Basic Variables
            dEntryReferenceNumber = intent.getExtras().getLong("entryReferenceNumber");
            setHeadingAndEntryText(intent.getExtras().getString("entryTitle"),
                    intent.getExtras().getString("entryText"));
            dEntryType = intent.getExtras().getInt("entryType");
            dPassedPosition = intent.getExtras().getInt("adapterPosition");
            dMediaString = intent.getExtras().getString("entryMediaString");
            dTagsString = intent.getExtras().getString("entryTags");

            // Set Media
            makeMediaArrayLists(dMediaString);
            makeRecyclerViewsReady();

            Log.i("XJDAE-guideActivityRe", "EntryView - " +
                    dEntryReferenceNumber + ", " + dEntryType + ", " + dPassedPosition + ", " + dMediaString + ", " + dTagsString);

        }


        setTimeDateAndERN(dEntryReferenceNumber, dEntryType);


        // Logs
        if (dMediaString != null) {
            Log.i("XJDAE-mediaString", dMediaString.toString());
            Log.i("XJDAE-ERN", String.valueOf(dEntryReferenceNumber));
        }
    }


    private void scrollRecyclerViewsToTop(int entryType, int passedPosition) {
        Log.i("XJDAE-scrollToTop()", String.valueOf(entryType));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (entryType) {
                    case 0:
                        CBDailyFragment.cDailyRecyclerView.scrollToPosition(passedPosition);
                        break;
                    case 1:
                        CBWeeklyFragment.cRecyclerView.scrollToPosition(passedPosition);
                        break;
                    case 2:
                        CBMonthlyFragment.cRecyclerView.scrollToPosition(passedPosition);
                        break;
                    case 3:
                        CBYearlyFragment.cRecyclerView.scrollToPosition(passedPosition);
                        break;
                }
            }
        }, 500);


    }


    private void makeMediaArrayLists(String entryMedia) {
        ArrayList<String> arrayListOfMedia = new ArrayList<>();

        try {
            Log.i("XJDAE-mStringList()", entryMedia);

            arrayListOfMedia = (ArrayList<String>)
                    ZZObjectSerializer.deserialize(entryMedia);

            Log.i("XJDAE-mediaArrayList", arrayListOfMedia.toString());

        } catch (Exception e) {
            Log.i("XJDAE-makeArrayLists", e.toString());
        }


        //////////////////////////////////
        if (arrayListOfMedia != null) {

            for (String value : arrayListOfMedia) {
                Log.i("XJDAE-singleMedias", value.toString());

                String valueExtension = value.substring(value.lastIndexOf("."));
                Log.i("XJDAE-valueExtension", valueExtension);
                String mediaPath = dMediaPath + value;

                dMediaCount++;

                switch (valueExtension) {
                    case ".jpg":
                        File imageFile = new File(mediaPath);
                        if (imageFile.exists()) {
                            Log.i("XJDAE-makeArrayList", "image");
                            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            dImagesList.add(imageBitmap);
                        }

                        break;

                    case ".mp4":
                        Log.i("XJDAE-makeArrayList", "video");
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

                        mediaMetadataRetriever.setDataSource(mediaPath);

                        Log.i("XJDAE-makeArrayList", "video1");

                        Bitmap entryImageFromVideoLarge = mediaMetadataRetriever
                                .getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
                        Log.i("XJDAE-makeArrayList", "video2");
                        /*
                        // WIP - For storage of images - Compressing Bitmap - Also I have to crop bitmap not compress it
                        Bitmap entryImageFromVideo = Bitmap.createScaledBitmap
                                (entryImageFromVideoLarge, 100, 100, true);
                        Log.i("BitmapSizes", "Old - " + entryImageFromVideoLarge.getWidth() + ", " + entryImageFromVideoLarge.getHeight()
                                + "; New Image - " + entryImageFromVideo.getWidth() + ", " + entryImageFromVideo.getHeight());
                         */
                        dVideoDataList.add(
                                new VideoData(mediaPath, entryImageFromVideoLarge));

                        Log.i("XJDAE-makeArrayList", "video3");
                        break;

                    case ".3gp":
                        Log.i("XJDAE-makeArrayList", "audio");
                        dAudioDataList.add(new AudioData(mediaPath, "Audio Entry"));

                        break;
                }
            }
        }

        Log.i("XJDAE-FinalArrayLists", "Image-" + dImagesList.toString() +
                "Video-" + dAudioDataList.toString() +
                "Audio-" + dVideoDataList.toString());
    }


    private void makeRecyclerViewsReady() {
        ////////////////////////// Initialising images Recycler View ///
        dEntryImagesRecyclerView = findViewById(R.id.dEntryImagesRecyclerView);

        dEntryImagesRecyclerView.setLayoutManager(new LinearLayoutManager
                (this, LinearLayoutManager.HORIZONTAL, false));

        SnapHelper dSnapHelper = new PagerSnapHelper();
        dSnapHelper.attachToRecyclerView(dEntryImagesRecyclerView);

        DCImagesRecyclerAdapter =
                new DCImagesRecyclerAdapter(this, dImagesList);
        dEntryImagesRecyclerView.setAdapter(DCImagesRecyclerAdapter);


        /////////////////////// VideoList/////////////////////////////
        dVideoPlayerView = findViewById(R.id.dMainVideoView);

        RecyclerView dEntryVideosRecyclerView = findViewById(R.id.dVideosRecyclerView);
        dEntryVideosRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        DCVideosRecyclerAdapter =
                new DCVideosRecyclerAdapter(this, dVideoDataList, dVideoPlayerView);
        dEntryVideosRecyclerView.setAdapter(DCVideosRecyclerAdapter);


        /////////////////////// AudioList/////////////////////////////
        RecyclerView dEntryAudiosRecyclerView = findViewById(R.id.dAudiosRecyclerView);
        dEntryAudiosRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        DCAudioRecyclerAdapter =
                new DCAudioRecyclerAdapter(this, dAudioDataList);
        dEntryAudiosRecyclerView.setAdapter(DCAudioRecyclerAdapter);

    }


    private int getPositionForNewEntry(int entryType) {
        switch (entryType) {
            case 0:
                return AApplicationClass.dailyReferenceNunberList.size();
            case 1:
                return AApplicationClass.weeklyReferenceNunberList.size();
            case 2:
                return AApplicationClass.monthlyReferenceNunberList.size();
            case 3:
                return AApplicationClass.yearlyReferenceNunberList.size();
            default:
                return 4;
        }

    }


    public class TextWatcherClass implements TextWatcher {

        private EditText editText;

        public TextWatcherClass(EditText editText) {
            this.editText = editText;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Log.i("XJDAE-onTextChanged", String.valueOf(s) + dEntryType + String.valueOf(dPassedPosition));

            String databaseColumnName = "";
            String text = editText.getText().toString();

            switch (editText.getId()) {
                case R.id.dHeadingText:
                    databaseColumnName = "entryTitle";
                    addHeadingToList(text);
                    break;

                case R.id.dEntryText:
                    databaseColumnName = "entryText";
                    addTextToList(text);
                    break;
            }

            text = text.replace("'", "");

            // To be placed within SQLhelper Class
            AApplicationClass.zzUtilitiesClass.getWritableDatabase().execSQL("UPDATE entriesTable SET " +
                    databaseColumnName + " = '" + text + "' " +
                    "WHERE entryReferenceNumber = " + dEntryReferenceNumber);

        }


        @Override
        public void afterTextChanged(Editable s) {

        }


        private void addTextToList(String text) {
            Log.i("XJDAE-addTexttoList", text + ", " + dEntryType + ", " + dPassedPosition);

            switch (dEntryType) {
                case 0:
                    AApplicationClass.dailyTextList.set(dPassedPosition, text);
                    CBDailyFragment.CCDailyRecyclerAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    AApplicationClass.weeklyTextList.set(dPassedPosition, text);
                    CBWeeklyFragment.cbWeeklyAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    AApplicationClass.monthlyTextList.set(dPassedPosition, text);
                    CBMonthlyFragment.cbMonthlyAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    AApplicationClass.yearlyTextList.set(dPassedPosition, text);
                    CBYearlyFragment.cbYearlyAdapter.notifyDataSetChanged();
                    break;

            }

        }


        private void addHeadingToList(String text) {

            switch (dEntryType) {
                case 0:
                    AApplicationClass.dailyTitleList.set(dPassedPosition, text);
                    CBDailyFragment.CCDailyRecyclerAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    AApplicationClass.weeklyTitleList.set(dPassedPosition, text);
                    CBWeeklyFragment.cbWeeklyAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    AApplicationClass.monthlyTitleList.set(dPassedPosition, text);
                    CBMonthlyFragment.cbMonthlyAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    AApplicationClass.yearlyTitleList.set(dPassedPosition, text);
                    CBYearlyFragment.cbYearlyAdapter.notifyDataSetChanged();
                    break;

            }
        }

    }


    public void setTimeDateAndERN(long entryReferenceNumber, int entryType) {
        Log.i("XJDAE-setTimeDateAndERN", entryReferenceNumber + ", " + entryType);

        // ERN
        TextView eRNText = findViewById(R.id.dEntryReferenceNumberText);
        eRNText.setText("ERN: " + String.valueOf(entryReferenceNumber));


        // Time and Date
        TextView dDateOneText = findViewById(R.id.dDateOneText);
        TextView dDateTwoText = findViewById(R.id.dDateTwoText);
        TextView dDayText = findViewById(R.id.dDayText);
        TextView dTimeText = findViewById(R.id.dTimeText);


        Date date = new Date(entryReferenceNumber);

        if (entryType == 0) {// Daily
            /////
            DateFormat todayDateExtractor = new SimpleDateFormat("dd");
            String dateOneString = todayDateExtractor.format(date);

            dDateOneText.setText(dateOneString);

            /////
            DateFormat monthExtractor = new SimpleDateFormat("MM");
            int monthNumber = Integer.parseInt(monthExtractor.format(date));
            String bigMonthName = new DateFormatSymbols().getMonths()[monthNumber - 1];
            String monthName = bigMonthName.substring(0, 3);

            DateFormat yearExtractor = new SimpleDateFormat("yy");

            String dateTwoString = monthName + ", " + yearExtractor.format(date);

            dDateTwoText.setText(dateTwoString);

            ////
            DateFormat dayOfWeekExtractor = new SimpleDateFormat("EEEE");
            String dayString = dayOfWeekExtractor.format(date);

            dDayText.setText(dayString);

            ////
            DateFormat timeExtractor = new SimpleDateFormat("hh:mm a");
            String timeString = timeExtractor.format(date);

            dTimeText.setText(timeString);


        } else {// Others
            dDateOneText.setVisibility(View.INVISIBLE);
            dDateTwoText.setVisibility(View.INVISIBLE);
            dTimeText.setVisibility(View.INVISIBLE);

            String calendarString = "";

            if (entryType == 1) {// Weekly
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                calendarString = "Week : " + calendar.get(Calendar.WEEK_OF_YEAR);

            } else if (entryType == 2) {// Monthly

                DateFormat monthExtractor = new SimpleDateFormat("MM");
                int monthNumber = Integer.parseInt(monthExtractor.format(date));

                calendarString = new DateFormatSymbols().getMonths()[monthNumber - 1];


            } else if (entryType == 3) {// Yearly
                DateFormat yearExtractor = new SimpleDateFormat("yyyy");

                calendarString = yearExtractor.format(date);

            }

            Log.i("XJDAE-otherEntriesDate", calendarString);

            dDayText.setText(calendarString);

        }
    }


    public void setHeadingAndEntryText(String titleText, String entryText) {
        dHeadingText.setText(titleText);
        dEntryText.setText(entryText);
    }


}