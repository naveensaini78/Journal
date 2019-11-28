package com.naveensaini.dwintest;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.ankushgrover.hourglass.Hourglass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naveensaini.dwintest.z_other_classes.AudioData;

import java.io.IOException;


public class DBAudioFragment extends Fragment {


    boolean pauseModeEnabled;
    boolean recordingModeOn;
    MediaRecorder mediaRecorder;

    VideoView dVideoView;

    TextView dRecordingTimeText, dRecordingOrPlayingText;

    FloatingActionButton dPausePlayButton, dStopButton;

    Hourglass recordingHourglass;

    OtherAnimationHandler otherAnimationHandler;

    String audioFileName;


    ////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dAudioFragmentView = inflater.inflate(R.layout.db_fragment_audio, container, false);


        ///////////////////////// Defining Variables //////////
        dVideoView = dAudioFragmentView.findViewById(R.id.dVideoView_Audio);
        dVideoView.setVideoPath("android.resource://"
                + getActivity().getPackageName() + "/" + R.raw.recording_animation);
        dRecordingTimeText = dAudioFragmentView.findViewById(R.id.dTimeText_Audio);
        dRecordingOrPlayingText = dAudioFragmentView.findViewById(R.id.dRecordingOrPlayingText_Audio);
        dPausePlayButton = dAudioFragmentView.findViewById(R.id.dPausePlayButton_Audio);
        dStopButton = dAudioFragmentView.findViewById(R.id.dStopButton_Audio);
        otherAnimationHandler = new OtherAnimationHandler();


        ///////////////////////////////////////////////

        pauseModeEnabled = true;


        ////////////////// onClickListeners //////////////
        dPausePlayButton.setOnClickListener(v -> {
            if (pauseModeEnabled) {
                if (!recordingModeOn) {
                    /// Start Recording
                    prepareToStartRecording();

                    mediaRecorder.start();
                    // Setting max for 10 minutes
                    startHourglassTimer(600000);
                    otherAnimationHandler.setRecordingStartAnimations();

                    DAEntryViewActivity.dCancleButtonAB.setVisibility(View.GONE);

                    showAToast("Recording Started", getActivity());
                    recordingModeOn = true;

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mediaRecorder.resume();

                        otherAnimationHandler.setResumedAnimations("Recording");

                    }
                }
                pauseModeEnabled = false;


            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mediaRecorder.pause();

                    otherAnimationHandler.setPausedAnimations();
                    recordingHourglass.pauseTimer();

                    pauseModeEnabled = true;
                }

            }
        });


        dStopButton.setOnClickListener(v -> {
            // animations and other things will be handled in the onFinishedMethod of Timer
            recordingHourglass.stopTimer();
            getFragmentManager().beginTransaction().remove(this).commit();

        });

        return dAudioFragmentView;
    }

    private void addTheData(String audioFileName) {
        DAEntryViewActivity.dAudioDataList.add(new AudioData(
                DAEntryViewActivity.dMediaPath + audioFileName,
                "Audio Recording"));
        DAEntryViewActivity.DCAudioRecyclerAdapter.notifyDataSetChanged();

        AApplicationClass.zzUtilitiesClass.addMediaToDataBase(
                DAEntryViewActivity.dEntryReferenceNumber, audioFileName);


    }


    //////////////////////////////////////////////////////////


    public void prepareToStartRecording() {
        MediaRecorderReady();
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recordingModeOn = false;
    }


    private void showAToast(String toastText, Activity activity) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show();
    }


    public void startHourglassTimer(final long totalTimeInMillis) {

        recordingHourglass = new Hourglass(totalTimeInMillis, 1000) {
            @Override
            public void onTimerTick(long millisUntilFinished) {

                //Log.i(String.valueOf(millisUntilFinished), String.valueOf(totalTimeInMillis));
                long timeElapsed = (totalTimeInMillis - millisUntilFinished) / 1000;

                String minutes;
                String seconds;

                if (timeElapsed < 60) {
                    minutes = "00";

                    if (timeElapsed < 10) {
                        seconds = "0" + timeElapsed;
                        Log.i("minutes", minutes + seconds);
                    } else {
                        seconds = String.valueOf(timeElapsed);
                    }

                } else {
                    long minuteNumber = timeElapsed / 60;
                    minutes = "0" + minuteNumber;

                    long secondsNumber = (timeElapsed - minuteNumber * 60);
                    if (secondsNumber < 10) {
                        seconds = "0" + secondsNumber;
                    } else {
                        seconds = String.valueOf(secondsNumber);
                    }


                }

                dRecordingTimeText.setText(minutes + ":" + seconds);

            }

            @Override
            public void onTimerFinish() {
                Log.i("timer", "finished");

                mediaRecorder.stop();
                otherAnimationHandler.setStoppedAnimation();


            }
        };
        recordingHourglass.startTimer();

    }


    public void startBlinkingAnimation(View blinkingView) {
        blinkingView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.blinking_animation));
    }


    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        DAEntryViewActivity.dMediaCount++;
        Log.i("XJDAE-mediaCountAudio", String.valueOf(DAEntryViewActivity.dMediaCount));
        audioFileName = String.valueOf(DAEntryViewActivity.dEntryReferenceNumber) +
                DAEntryViewActivity.dMediaCount + ".3gp";
        String outputFile = DAEntryViewActivity.dMediaPath + audioFileName;
        mediaRecorder.setOutputFile(outputFile);
        addTheData(audioFileName);

    }


    public class OtherAnimationHandler {

        public void setRecordingStartAnimations() {
            dVideoView.start();
            dPausePlayButton.setImageResource(R.drawable.pause);
            dRecordingOrPlayingText.setText("Recording");
            startBlinkingAnimation(dRecordingOrPlayingText);
            startBlinkingAnimation(dRecordingTimeText);
            stopButtonShowHideAnimation(true);
        }


        public void setPausedAnimations() {
            dVideoView.pause();
            dRecordingOrPlayingText.clearAnimation();
            dRecordingTimeText.clearAnimation();
            dPausePlayButton.setImageResource(R.drawable.play);
        }


        public void setResumedAnimations(String recordingOrPlayingString) {

            dVideoView.start();

            dRecordingOrPlayingText.setText(recordingOrPlayingString);
            startBlinkingAnimation(dRecordingOrPlayingText);
            startBlinkingAnimation(dRecordingTimeText);

            // HourGlass Timer handled above

            dPausePlayButton.setImageResource(R.drawable.pause);


        }


        public void setStoppedAnimation() {
            dVideoView.pause();
            dRecordingOrPlayingText.setText("");
            dRecordingTimeText.setText("00:00");
            dRecordingOrPlayingText.clearAnimation();
            dRecordingTimeText.clearAnimation();
            dPausePlayButton.setImageResource(R.drawable.play);
            stopButtonShowHideAnimation(false);
        }


        public void stopButtonShowHideAnimation(boolean showStopButton) {
            if (showStopButton) {
                dPausePlayButton.animate()
                        .translationXBy(-150)
                        .setDuration(200)
                        .translationZBy(-100).z(-100).start();
                dPausePlayButton.bringToFront();
            } else {
                dPausePlayButton.animate()
                        .translationXBy(150)
                        .setDuration(200)
                        .translationZBy(100).z(100).start();
                dPausePlayButton.bringToFront();
            }
        }

    }


}
