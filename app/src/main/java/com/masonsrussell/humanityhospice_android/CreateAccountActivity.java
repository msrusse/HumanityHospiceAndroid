package com.masonsrussell.humanityhospice_android;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Random;

public class CreateAccountActivity extends AppCompatActivity
{
	Button createAccountButton;
	EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, verifyPasswordEditText;
	TextView loginView;
	private FirebaseAuth mAuth;
	String password, email, firstName, lastName;
	private FirebaseUser user;
	private static final String TAG = "CreateAccountActivity";
	RadioGroup accountTypeSelector;
	RadioButton selectedAccountType;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		firstNameEditText = findViewById(R.id.firstNameEditText);
		lastNameEditText = findViewById(R.id.lastNameEditText);
		emailEditText = findViewById(R.id.emailEditText);
		accountTypeSelector = findViewById(R.id.accountTypeSelector);
		passwordEditText = findViewById(R.id.passwordEditText);
		verifyPasswordEditText = findViewById(R.id.reenterPasswordEditText);
		loginView = findViewById(R.id.loginView);
		createAccountButton = findViewById(R.id.createAccountButton);
		mAuth = FirebaseAuth.getInstance();

		accountTypeSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				selectedAccountType = findViewById(checkedId);
				if (selectedAccountType.getText().equals("Friend"))
				{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateAccountActivity.this);

					// set dialog message
					alertDialogBuilder.setView(R.layout.dialog_enter_patient_code);


					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}
			}
		});

		createAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (!TextUtils.isEmpty(emailEditText.getText()) && !TextUtils.isEmpty(firstNameEditText.getText()) && !TextUtils.isEmpty(lastNameEditText.getText()) && !TextUtils.isEmpty(passwordEditText.getText()) && !TextUtils.isEmpty(verifyPasswordEditText.getText()))
				{
					email = emailEditText.getText().toString();
					firstName = firstNameEditText.getText().toString();
					lastName = lastNameEditText.getText().toString();
					if(passwordEditText.getText().toString().equals(verifyPasswordEditText.getText().toString()))
					{
						password = passwordEditText.getText().toString();
						createAccount();
					}
					else
						{
						Toast.makeText(CreateAccountActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
					}

				}
				else
				{
					Toast.makeText(CreateAccountActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
				}
			}
		});

		loginView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}



	private void createAccount()
	{
		mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "createUserWithEmail:success");
							user = mAuth.getCurrentUser();
							createAccountInDtabase();
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "createUserWithEmail:failure", task.getException());
							Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private void createAccountInDtabase()
	{
		int accountTypeSelected = accountTypeSelector.getCheckedRadioButtonId();
		selectedAccountType = findViewById(accountTypeSelected);
		if (selectedAccountType.getText().equals("Patient"))
		{
			FirebaseCalls.createPatient(generateRandom(), firstName, lastName);
		}
		else
		{
			HashMap<String, String> usersAndCodes = FirebaseCalls.getPatientInviteCodes();
			for (String user : usersAndCodes.keySet())
			{
				continue;
			}
		}
		addPersonalData();
	}

	private void addPersonalData()
	{
		UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
				.setDisplayName(firstName + " " + lastName)
				.build();

		user.updateProfile(profileUpdates)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							Log.d(TAG, "User profile updated.");
							Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
							startActivity(intent);
							finish();
						}
					}
				});
	}

	static String generateString(Random rng, String characters)
	{
		char[] text = new char[6];
		for (int i = 0; i < 6; i++)
		{
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}

	String generateRandom()
	{
		Random random = new Random();
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		return generateString(random, chars);
	}


}
