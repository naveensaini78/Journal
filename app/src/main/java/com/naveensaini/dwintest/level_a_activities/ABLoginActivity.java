package com.naveensaini.dwintest.level_a_activities;

import androidx.appcompat.app.AppCompatActivity;

public class ABLoginActivity extends AppCompatActivity {
/*

	boolean buttonAsLogin;
	Button aLoginButton;
	TextView aToggleText;
	EditText usernameText;
	EditText passwordText;
	EditText displayNameText;
	CardView dpCardView;
	ImageView displayPicture;
	TextView errorText;

	byte[] byteArray;
	boolean byteArrayIsHere;

	LinearLayout aLinearLayout;
	static InputMethodManager inputMethodManager;


	public void aClickLogin(View view) {

		final String username = usernameText.getText().toString();
		final String password = passwordText.getText().toString();
		final String displayName = displayNameText.getText().toString();

		if (!TextUtils.isEmpty(username) | !TextUtils.isEmpty(password) | !TextUtils.isEmpty(displayName)) {

			errorText.setVisibility(view.GONE);

			final ParseUser user = new ParseUser();

			// Signup
			if (!buttonAsLogin) {
				user.setUsername(username);
				user.setPassword(password);
				user.put("DisplayName", displayName);

				user.signUpInBackground(new SignUpCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {

							if (byteArrayIsHere) {

								try {
									// Saving File
									file = new ParseFile(username + ".png", byteArray);
									file.save();

									// Attaching it to username
									ParseUser.getCurrentUser().put("DisplayPicture", file);
									ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
										@Override
										public void done(ParseException e) {
											if (e != null) {
												Log.i("save", e.toString());

											}
										}
									});

								} catch (ParseException e1) {
									e1.printStackTrace();
								}

							}

							startActivity(new Intent(getApplicationContext(), BUsersListActivity.class));
							Toast.makeText(ALoginActivity.this, "Signup Successful! Welcome " + displayName, Toast.LENGTH_SHORT).show();
							finish();

						} else {
							Toast.makeText(ALoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

						}
					}
				});

				//Login
			} else {
				ParseUser.logInInBackground(username, password, new LogInCallback() {
					@Override
					public void done(ParseUser user, ParseException e) {
						if (user != null) {
							Toast.makeText(ALoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

							startActivity(new Intent(getApplicationContext(), BUsersListActivity.class));
							finish();

						} else {
							Toast.makeText(ALoginActivity.this, "Login Unuccessful - " + e.toString(), Toast.LENGTH_SHORT).show();

						}

					}
				});
			}

		} else {
			errorText.setVisibility(view.VISIBLE);

		}
	}


	public void aClickToggle(View view) {

		aLoginButton = (Button) findViewById(R.id.aLoginButton);
		aToggleText = (TextView) findViewById(R.id.aToggleText);

		if (!buttonAsLogin) {
			aToggleText.setText("Don't have an account! Create one!");
			aLoginButton.setText("LOGIN");
			Toast.makeText(ALoginActivity.this, "Enter your username and password", Toast.LENGTH_SHORT).show();
			displayNameText.setVisibility(View.GONE);
			dpCardView.setVisibility(View.GONE);

			buttonAsLogin = true;

		} else {
			aToggleText.setText("Aleardy have an account! Login Instead!");
			aLoginButton.setText("SIGNUP");
			Toast.makeText(ALoginActivity.this, "Create a username and a password", Toast.LENGTH_SHORT).show();
			displayNameText.setVisibility(View.VISIBLE);
			dpCardView.setVisibility(View.VISIBLE);

			buttonAsLogin = false;

		}

	}


	/////////////////////////////// ONCREATE ////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ab_activity_login);


		// Initiating Variables
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		usernameText = (EditText) findViewById(R.id.aUserNameEditText);
		displayNameText = findViewById(R.id.aDisplayNameEditText);
		dpCardView = findViewById(R.id.aDPCardView);
		displayPicture = findViewById(R.id.aDPImageView);
		errorText = findViewById(R.id.aErrorText);
		passwordText = (EditText) findViewById(R.id.aPasswordEditText);
		passwordText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int i, KeyEvent keyEvent) {
				if (i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
					aClickLogin(view);

				}

				return false;
			}
		});

		buttonAsLogin = true;

		aLinearLayout = findViewById(R.id.aLinearLayout);
		// Click to Exit Keyboard
		aLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				inputMethodManager.hideSoftInputFromWindow(aLinearLayout.getWindowToken(), 0);

			}
		});

		byteArrayIsHere = false;


	}


	// Setting DP
	public void aClickChangeDp(View view) {
		ZZUtilitiesClass.initiatePicChangeProcedure(ALoginActivity.this);

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

			Uri tempUri = data.getData();
			ZZUtilitiesClass.performCrop(tempUri);

		} else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

			Uri uriFromGalley = data.getData();

			displayPicture.setImageURI(uriFromGalley);

			try {
				Bitmap tempBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFromGalley);
				Bitmap bitmap = Bitmap.createScaledBitmap(tempBitmap, 200, 200, true);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byteArray = stream.toByteArray();

				byteArrayIsHere = true;

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

*/
}