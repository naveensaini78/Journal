package com.example.avlogapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ankushgrover.hourglass.Hourglass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DEntryViewActivity extends AppCompatActivity {


	String AudioSavePathInDevice = null, dDateOneString, dDateTwoString, dDayString, dTimeString;
	int passedPosition;
	long entryReferenceNumber;

	boolean entryAudioRecordingMode, pauseModeEnabled, startingThePlayback, mediaPlayerReady;

	MediaRecorder mediaRecorder;
	MediaPlayer dMediaPlayer;

	VideoView dVideoView;

	TextView dRecordingTimeText, dRecordingOrPlayingText;
	EditText dHeadingText, dEntryText;

	FloatingActionButton dPausePlayButton, dStopButton;

	Hourglass recordingHourglass;

	OtherAnimationHandler otherAnimationHandler;

	SeekBar dAudioSeekbar;

	CameraView dCameraView;


	@Override
	public boolean onSupportNavigateUp() {

		finish();

		return super.onSupportNavigateUp();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_d_entry_view);
		Log.i("Activity", "onCreate");


		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//////////////////


		dCameraView = findViewById(R.id.dCameraView);
		dCameraView.start();
		dCameraView.addCameraKitListener(new CameraKitEventListener() {
			@Override
			public void onEvent(CameraKitEvent cameraKitEvent) {
				Log.i("Camera", cameraKitEvent.toString());
			}

			@Override
			public void onError(CameraKitError cameraKitError) {
				Log.i("Camera", cameraKitError.getException().toString());
			}

			@Override
			public void onImage(CameraKitImage cameraKitImage) {
				if (cameraKitImage == null) {
					Log.i("Camera", "null Image");
				} else {
					Log.i("Camera", "Image");
				}
			}

			@Override
			public void onVideo(CameraKitVideo cameraKitVideo) {
				if (cameraKitVideo == null) {
					Log.i("Camera", "null Video");
				} else {
					Log.i("Camera", "Video");
				}
			}
		});
		dCameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
			@Override
			public void callback(CameraKitImage cameraKitImage) {
				if (cameraKitImage == null) {
					Log.i("Camera", "null Image");
				} else {
					Log.i("Camera", "Image");
				}
			}
		});
		dCameraView.captureVideo(new CameraKitEventCallback<CameraKitVideo>() {
			@Override
			public void callback(CameraKitVideo cameraKitVideo) {
				if (cameraKitVideo == null) {
					Log.i("Camera", "null Video");
				} else {
					Log.i("Camera", "Video");
				}
			}
		});


		//////// Defining Variables //////////
		dVideoView = findViewById(R.id.dVideoView);
		dVideoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.recording_animation);

		dRecordingTimeText = findViewById(R.id.dRecordingTimeText);
		dRecordingOrPlayingText = findViewById(R.id.dRecordingOrPlayingText);

		dHeadingText = findViewById(R.id.dHeadingText);
		dEntryText = findViewById(R.id.dEntryText);
		dHeadingText.addTextChangedListener(new TextWatcherClass(dHeadingText));
		dEntryText.addTextChangedListener(new TextWatcherClass(dEntryText));

		dPausePlayButton = findViewById(R.id.dPausePlayButton);
		dStopButton = findViewById(R.id.dStopButton);

		dAudioSeekbar = (SeekBar) findViewById(R.id.dAudioSeekBar);
		dAudioSeekbar.setEnabled(false);

		otherAnimationHandler = new OtherAnimationHandler();

		mediaPlayerReady = false;


		//////////////////////
		final Intent intent = getIntent();
		guideTheActivity(intent);


		///////////////////////////
		dPausePlayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String toastMessage = "";
				if (!pauseModeEnabled) {
					if (entryAudioRecordingMode) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
							mediaRecorder.pause();
							toastMessage = "Recording Paused";
						}
					} else {
						dMediaPlayer.pause();
						toastMessage = "PlayBack Paused";
					}

					otherAnimationHandler.setPausedAnimations();
					recordingHourglass.pauseTimer();

					pauseModeEnabled = true;

				} else {
					if (entryAudioRecordingMode) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
							mediaRecorder.resume();
							toastMessage = "Recording Resumed";
							otherAnimationHandler.setResumedAnimations("Recording");

							pauseModeEnabled = false;

						}
					} else {
						if (startingThePlayback) {
							if (mediaPlayerReady) {
								dMediaPlayer.start();
								startHourglassTimer(dMediaPlayer.getDuration());
								toastMessage = "PlayBack Started";

								otherAnimationHandler.setResumedAnimations("Playing");
								otherAnimationHandler.stopButtonShowHideAnimation(true);
								otherAnimationHandler.startTheSeekbar();

								startingThePlayback = false;
								pauseModeEnabled = false;

							} else {
								prepareMediaPlayer();
								toastMessage = "MediaPlayer not ready! Try Again!!";
							}
						} else {
							dMediaPlayer.start();
							recordingHourglass.resumeTimer();
							toastMessage = "Playback Resumed";

							otherAnimationHandler.setResumedAnimations("Playing");

							pauseModeEnabled = false;
						}

					}


				}
				Toast.makeText(DEntryViewActivity.this, toastMessage, Toast.LENGTH_LONG).show();

			}
		});


		dStopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// animations and other things will be handled in the onFinishedMethod of Timer
				recordingHourglass.stopTimer();
			}
		});
	}


	@Override
	protected void onPause() {
		super.onPause();


		// temporary
		dCameraView.stopVideo();


		String toastMessage = "";
		if (entryAudioRecordingMode) {
			mediaRecorder.stop();
			toastMessage = "Recording Stopped";
		} else {
			try {
				dMediaPlayer.stop();
				toastMessage = "Playback Stopped";
			} catch (Exception e) {
			}
		}
		Toast.makeText(DEntryViewActivity.this, toastMessage, Toast.LENGTH_LONG).show();
		finish();

		Log.i("Activity", "Paused");
	}


	@Override
	protected void onResume() {
		super.onResume();
		Log.i("Activity", "Resumed");
	}


	@Override
	protected void onStop() {
		super.onStop();
		Log.i("Activity", "Stopped");
	}


	public void startAudioRecordWithAnimation() {

		MediaRecorderReady();

		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mediaRecorder.start();
		// Setting max for 10 minutes
		startHourglassTimer(600000);
		otherAnimationHandler.setRecordingStartAnimations();

		Toast.makeText(DEntryViewActivity.this, "Recording started", Toast.LENGTH_LONG).show();

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
				String toastMessage = "";
				if (entryAudioRecordingMode) {
					mediaRecorder.stop();
					toastMessage = "Recording Stopped";

					entryAudioRecordingMode = false;

				} else {
					dMediaPlayer.stop();
					try {
						dMediaPlayer.prepare();
					} catch (IOException e) {
						e.printStackTrace();
					}
					toastMessage = "Playback Stopped";
					otherAnimationHandler.setSeekbarToZero();
				}

				otherAnimationHandler.setStoppedAnimation();
				Toast.makeText(DEntryViewActivity.this, toastMessage, Toast.LENGTH_SHORT).show();

				pauseModeEnabled = true;
				startingThePlayback = true;

			}
		};
		recordingHourglass.startTimer();

	}


	public void startBlinkingAnimation(View blinkingView) {
		blinkingView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.blinking_animation));
	}


	public void MediaRecorderReady() {
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		mediaRecorder.setOutputFile(AudioSavePathInDevice);

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

			String databaseColumnName = "";
			String text = editText.getText().toString();
			switch (editText.getId()) {
				case R.id.dHeadingText:
					databaseColumnName = "entryTitle";
					CAllEntriesActivity.entryTitleList.set(passedPosition, text);
					break;

				case R.id.dEntryText:
					databaseColumnName = "entryText";
					CAllEntriesActivity.entryTextList.set(passedPosition, text);
					break;
			}

			CAllEntriesActivity.entriesSql.execSQL("UPDATE entriesTable SET " + databaseColumnName + " = '" + text + "' WHERE entryReferenceNumber = " + entryReferenceNumber);
			CAllEntriesActivity.cEntriesRecyclerAdapter.notifyDataSetChanged();

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	}


	public void setTimeAndDate(long entryReferenceNumber, String titleText, String entryText) {
		TextView dDateOneText = findViewById(R.id.dDateOneText);
		TextView dDateTwoText = findViewById(R.id.dDateTwoText);
		TextView dDayText = findViewById(R.id.dDayText);
		TextView dTimeText = findViewById(R.id.dTimeText);

		makeDatesFromLong(entryReferenceNumber);

		dDateOneText.setText(dDateOneString);
		dDateTwoText.setText(dDateTwoString);
		dDayText.setText(dDayString);
		dTimeText.setText(dTimeString);

		dHeadingText.setText(titleText);
		dEntryText.setText(entryText);

	}


	public void makeDatesFromLong(long timeInMillis) {

		Date date = new Date(timeInMillis);

		/////
		DateFormat todayDateExtractor = new SimpleDateFormat("dd");
		dDateOneString = todayDateExtractor.format(date);

		/////
		DateFormat monthExtractor = new SimpleDateFormat("MM");
		int monthNumber = Integer.parseInt(monthExtractor.format(date));
		String bigMonthName = new DateFormatSymbols().getMonths()[monthNumber - 1];
		String monthName = bigMonthName.substring(0, 3);

		DateFormat yearExtractor = new SimpleDateFormat("yy");

		dDateTwoString = monthName + ", " + yearExtractor.format(date);

		////
		DateFormat dayOfWeekExtractor = new SimpleDateFormat("EEEE");
		dDayString = dayOfWeekExtractor.format(date);


		////
		DateFormat timeExtractor = new SimpleDateFormat("hh:mm a");
		dTimeString = timeExtractor.format(date);


	}


	public class OtherAnimationHandler {

		Handler handler;
		Runnable runnable;

		public void startTheSeekbar() {
			dAudioSeekbar.setEnabled(true);
			dAudioSeekbar.setMax(dMediaPlayer.getDuration());

			handler = new Handler();

			runnable = new Runnable() {
				@Override
				public void run() {
					if (dMediaPlayer != null) {
						dAudioSeekbar.setProgress(dMediaPlayer.getCurrentPosition());
						handler.postDelayed(this, 1000);

					}

				}
			};
			DEntryViewActivity.this.runOnUiThread(runnable);

			// for Disabling touch
			dAudioSeekbar.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return false;
				}
			});

/*
			dAudioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					Log.i("Progress change", Integer.toString(progress));
					dMediaPlayer.seekTo(progress);

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});
*/
		}


		public void setSeekbarToZero() {
			dAudioSeekbar.setProgress(0);
			handler.removeCallbacks(runnable);
			dAudioSeekbar.setEnabled(false);

		}


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
				dPausePlayButton.animate().translationXBy(-150).setDuration(200).start();
				dPausePlayButton.bringToFront();
			} else {
				dPausePlayButton.animate().translationXBy(150).setDuration(200).start();
				dPausePlayButton.bringToFront();
			}
		}

	}


	public void guideTheActivity(Intent intent) {

		if (intent.getStringExtra("cToD").equals("startRecordingAudio")) {

			entryReferenceNumber = System.currentTimeMillis();
			AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AVLog/Recordings/"
					+ entryReferenceNumber + "AudioRecording.3gp";
			Log.i("AudioPath", AudioSavePathInDevice);

			CAllEntriesActivity.entryTitleList.add("");
			CAllEntriesActivity.entryTextList.add("");
			CAllEntriesActivity.entryReferenceNumberList.add(entryReferenceNumber);
			CAllEntriesActivity.cEntriesRecyclerAdapter.notifyDataSetChanged();
			passedPosition = CAllEntriesActivity.entryReferenceNumberList.size() - 1;
			// SQL
			CAllEntriesActivity.entriesSql.execSQL("INSERT INTO entriesTable(entryReferenceNumber) VALUES(" + entryReferenceNumber + ")");

			startAudioRecordWithAnimation();
			setTimeAndDate(entryReferenceNumber, "", "");

			entryAudioRecordingMode = true;
			pauseModeEnabled = false;


		} else if (intent.getStringExtra("cToD").equals("displayTheEntry")) {

			Long passedReferenceNumber = getIntent().getLongExtra("cToDReferenceNumber", 00);
			entryReferenceNumber = passedReferenceNumber;
			AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AVLog/Recordings/"
					+ entryReferenceNumber + "AudioRecording.3gp";
			Log.i("AudioPath", AudioSavePathInDevice);

			String passedEntryTitle = getIntent().getExtras().getString("cToDTitle", "");
			String passedEntryText = getIntent().getExtras().getString("cToDText", "");
			passedPosition = getIntent().getExtras().getInt("cToDAdapterPosition");
			Log.i("passed", passedEntryText + passedEntryTitle + passedReferenceNumber + passedPosition);

			setTimeAndDate(passedReferenceNumber, passedEntryTitle, passedEntryText);

			pauseModeEnabled = true;
			entryAudioRecordingMode = false;
			startingThePlayback = true;


		}


	}


	public void prepareMediaPlayer() {

		dMediaPlayer = new MediaPlayer();
		try {
			dMediaPlayer.setDataSource(AudioSavePathInDevice);
			Log.i("Temporary", AudioSavePathInDevice);

			dMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					Log.i("MediaPlayer", "Ready");
					mediaPlayerReady = true;

				}
			});
			dMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					//mediaPlayerReady = false;
				}
			});

			dMediaPlayer.prepareAsync();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}












