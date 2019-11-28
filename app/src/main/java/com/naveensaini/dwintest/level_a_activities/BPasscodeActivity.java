package com.naveensaini.dwintest.level_a_activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.naveensaini.dwintest.CAAllEntriesActivity;
import com.naveensaini.dwintest.R;

public class BPasscodeActivity extends AppCompatActivity {


	TextView bPasscodeTextView;
	TextView bActionTextView;
	SharedPreferences bSP;
	boolean passcodeIsPresent;
	private static String bNewPasscode;
	private static boolean bCreatePasscodeReentry;


	public void bNumberClick(View view) {
		bPasscodeTextView.setText(bPasscodeTextView.getText() + ((Button) view).getText().toString());

		if (passcodeIsPresent) {

			if (bPasscodeTextView.getText().toString().equals(bSP.getString("passcode", ""))) {
				bPasscodeTextView.setText("");

				finish();
                startActivity(new Intent(this, CAAllEntriesActivity.class));

			}

		}

		Log.i("PasswordNumber", bPasscodeTextView.getText().toString());

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.b_activity_passcode);


		/////// Defining Variables /////////
		bPasscodeTextView = findViewById(R.id.bPasscodeTextView);
		bActionTextView = findViewById(R.id.bActionTextView);
		ImageView bBackSpaceButton = findViewById(R.id.bBackSpaceButton);
		final Button bDoneButton = findViewById(R.id.bDoneButton);
        bSP = this.getSharedPreferences("com.naveensaini.dwintest", Context.MODE_PRIVATE);


		//////// BackSpaceButton //////////
		bBackSpaceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!bPasscodeTextView.getText().toString().equals("")) {
					String passcodeAfterBackspace = bPasscodeTextView.getText().toString();
					passcodeAfterBackspace = passcodeAfterBackspace.substring(0, passcodeAfterBackspace.length() - 1);

					bPasscodeTextView.setText(passcodeAfterBackspace);

					Log.i("PasswordNumber", bPasscodeTextView.getText().toString());

				}

			}
		});

		bBackSpaceButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {

				bPasscodeTextView.setText("");

				return false;
			}
		});


		///////// If started from ANOTHER ACTIVITY /////////////

		try {
			if (getIntent().getExtras().getBoolean("cToBPasscodeChange")) {
				passcodeIsPresent = false;

				bCreatePasscodeReentry = false;

				bDoneButton.setVisibility(View.VISIBLE);
				bActionTextView.setText("Create a new Passcode");
				Log.i("passcode", "phase one");
            }

        } catch (Exception e) {
            Log.i("passcode", e.toString());

            if (bSP.contains("passcode")) {
				passcodeIsPresent = true;

				Log.i("passcode", bSP.getString("passcode", ""));
				Log.i("passcode", "phase two");
			} else {
				passcodeIsPresent = false;

				bCreatePasscodeReentry = false;

				bDoneButton.setVisibility(View.VISIBLE);
				bActionTextView.setText("Create a new Passcode");
				Log.i("passcode", "phase three");
			}
		}


		/////////// Creating a PASSCODE ///////////
		bDoneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

                if (!(bPasscodeTextView.getText().toString().length() < 1)) {

					if (bCreatePasscodeReentry) {

						if (bPasscodeTextView.getText().toString().equals(bNewPasscode)) {
							Log.i("passcode saving", "successful");

							bSP.edit().putString("passcode", bNewPasscode).apply();
							bDoneButton.setVisibility(View.GONE);

							finish();
                            startActivity(new Intent(BPasscodeActivity.this, CAAllEntriesActivity.class));

						} else {

							Log.i("passcode saving", "unsuccessful");

							bActionTextView.setText("Create a new Passcode");
							bPasscodeTextView.setText("");
							bNewPasscode = "";
							bCreatePasscodeReentry = false;

						}


					} else {

						bNewPasscode = bPasscodeTextView.getText().toString();
						bPasscodeTextView.setText("");
						bActionTextView.setText("Re-enter passcode to create one!");
						bCreatePasscodeReentry = true;

					}


				} else {
					Toast.makeText(BPasscodeActivity.this, "Passcode should be atleast of 4 digits!!", Toast.LENGTH_SHORT).show();

				}


			}
		});

	}


}

