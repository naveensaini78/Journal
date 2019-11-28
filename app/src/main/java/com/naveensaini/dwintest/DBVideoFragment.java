
package com.naveensaini.dwintest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class DBVideoFragment extends ZZCameraVideoFragment {


    @BindView(R.id.dVideoRecordButton)
    CardView dVideoRecordButton;
    @BindView(R.id.dVideoStopButton)
    CardView dVideoStopButton;
    @BindView(R.id.dVideoPauseButton)
    ImageView dVideoPauseButton;
    @BindView(R.id.dVideoResumeButton)
    ImageView dVideoResumeButton;
    @BindView(R.id.dVideoRecordLayout)
    RelativeLayout dVideoRecordLayout;
    @BindView(R.id.dAutoFitTextureView)
    ZZAutoFitTextureView dZZAutoFitTextureView;
    @BindView(R.id.dPauseResumeLayout_Video)
    RelativeLayout dPauseResumeLayout;
    @BindView(R.id.dRotationButton)
    ImageView dRotationButton;
    @BindView(R.id.dRotationLayout)
    RelativeLayout dRotationLayout;
    @BindView(R.id.dFlashSwitch)
    Switch dFlashSwitch;

    Unbinder unbinder;
    String AudioSavePathInDevice;

    public static int dFlashMode;
    public static int dVideoQuality;


    public DBVideoFragment() {
        // Required empty public constructor
    }


    public static DBVideoFragment newInstance() {
        DBVideoFragment fragment = new DBVideoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    //////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.db_fragment_video, container, false);
        unbinder = ButterKnife.bind(this, view);

        //////////////////////////////////////////
        guideTheFragment();

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public int getTextureResource() {
        return R.id.dAutoFitTextureView;
    }


    @Override
    protected void setUp(View view) {

    }


    //////////////////////////////////////////////////////
    @OnClick({R.id.dVideoRecordButton, R.id.dVideoStopButton,
            R.id.dVideoPauseButton, R.id.dVideoResumeButton,
            R.id.dRotationButton})
    public void onViewClicked(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_animation));

        switch (view.getId()) {
            case R.id.dVideoRecordButton:
                if (!mIsRecordingVideo) {
                    // Flash
                    if (dFlashSwitch.isChecked()) {
                        dFlashMode = 2;
                    } else {
                        dFlashMode = 0;
                    }

                    startRecordingVideo();

                    DAEntryViewActivity.dCancleButtonAB.setVisibility(View.GONE);

                    dPauseResumeLayout.setVisibility(View.VISIBLE);
                    dVideoRecordButton.setVisibility(View.GONE);
                    dVideoStopButton.setVisibility(View.VISIBLE);
                    dRotationLayout.setVisibility(View.GONE);
                    dFlashSwitch.setVisibility(View.GONE);

                    showAToast("Video Recording Started", getActivity());

                }
                break;
            case R.id.dVideoStopButton:
                try {

                    stopRecordingVideo();

                    showAToast("Video Recording Stopped", getActivity());

                    getFragmentManager().beginTransaction()
                            .remove(this).commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.dVideoPauseButton:

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pauseVideoRecording();

                        dVideoPauseButton.setVisibility(View.GONE);
                        dVideoResumeButton.setVisibility(View.VISIBLE);

                        showAToast("Video Recording Paused", getActivity());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.dVideoResumeButton:

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        resumeRecordingVideo();

                        dVideoPauseButton.setVisibility(View.VISIBLE);
                        dVideoResumeButton.setVisibility(View.GONE);

                        showAToast("Video Recording Resumed", getActivity());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case R.id.dRotationButton:

                if (AApplicationClass.cameraId == 1) {
                    AApplicationClass.cameraId = 0;
                } else {
                    AApplicationClass.cameraId = 1;
                }

                getFragmentManager().beginTransaction().replace(R.id.dAudioVideoRecordFrame, new DBVideoFragment()).commit();
                break;
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("ConfigurationChanged", newConfig.toString());

    }


    ////////////////////////////////////////////////////////
    private void guideTheFragment() {
        // Setting VideoQuality
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (AApplicationClass.cameraId == 0) {
            dVideoQuality = Integer.parseInt(sharedPreferences.getString("videoBackQuality", "5"));
        } else {
            dVideoQuality = Integer.parseInt(sharedPreferences.getString("videoFrontQuality", "4"));
        }


        // Flash Switch management
        int hourOfTheDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        Log.i("CurrentTime", String.valueOf(hourOfTheDay));
        if (hourOfTheDay > 18 || hourOfTheDay < 6) {
            dFlashSwitch.setChecked(true);
            dFlashMode = 2;
        } else {
            dFlashSwitch.setChecked(false);
            dFlashMode = 0;
        }
    }


    private void showAToast(String toastText, Activity activity) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show();
    }

}