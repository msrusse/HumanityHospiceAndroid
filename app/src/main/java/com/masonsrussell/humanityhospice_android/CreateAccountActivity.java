package com.masonsrussell.humanityhospice_android;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CreateAccountActivity extends AppCompatActivity
{
	Button createAccountButton;
	EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, verifyPasswordEditText;
	TextView loginView;
	private FirebaseAuth mAuth;
	String password, email, firstName, lastName, patientAccessCode;
	private FirebaseUser user;
	private static final String TAG = "CreateAccountActivity";
	RadioGroup accountTypeSelector;
	RadioButton selectedAccountType;
	Boolean verifiedAccess = true;
	static ArrayList<String> patientIds = new ArrayList<>();
	static HashMap<String, String> patientInviteCodes = new HashMap<>();

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
		FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
		final DatabaseReference patients = mDatabase.getReference(FirebaseCalls.Patients);
		patients.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				try
				{
					HashMap<String, Object> allPatients = (HashMap) dataSnapshot.getValue();
					patientIds.addAll(allPatients.keySet());
					getInviteCodes(patients);
				}
				catch(Exception ex)
				{
					Log.d("CreateAccount", ex.getMessage());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});

		accountTypeSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, final int checkedId)
			{
				selectedAccountType = findViewById(checkedId);
				if (selectedAccountType.getText().equals("Friend"))
				{
					displayDialog();
				} else
				{
					verifiedAccess = true;
				}
			}
		});

		createAccountButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!TextUtils.isEmpty(emailEditText.getText()) && !TextUtils.isEmpty(firstNameEditText.getText()) && !TextUtils.isEmpty(lastNameEditText.getText()) && !TextUtils.isEmpty(passwordEditText.getText()) && !TextUtils.isEmpty(verifyPasswordEditText.getText()) && verifiedAccess)
				{
					email = emailEditText.getText().toString();
					firstName = firstNameEditText.getText().toString();
					lastName = lastNameEditText.getText().toString();
					if (passwordEditText.getText().toString().equals(verifyPasswordEditText.getText().toString()))
					{
						password = passwordEditText.getText().toString();
						createAccount();
					} else
					{
						Toast.makeText(CreateAccountActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
					}

				} else if (!verifiedAccess)
				{
					accountTypeSelector.check(R.id.friendButton);
					Toast.makeText(getApplicationContext(), "Please enter a verified patient code", Toast.LENGTH_SHORT).show();
					displayDialog();
				} else
				{
					Toast.makeText(CreateAccountActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
				}
			}
		});

		loginView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private void displayDialog()
	{
		final Dialog dialog = new Dialog(CreateAccountActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_enter_patient_code);
		Button enter = dialog.findViewById(R.id.enterButton);
		Button cancel = dialog.findViewById(R.id.cancelButton);
		final EditText accessCode = dialog.findViewById(R.id.accessCode);
		dialog.show();

		enter.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!TextUtils.isEmpty(accessCode.getText()))
				{
					String patientCode = accessCode.getText().toString();
					if (checkForPatientCode(patientCode))
					{
						patientAccessCode = patientCode;
						verifiedAccess = true;
						dialog.hide();
					} else
					{
						verifiedAccess = false;
						Toast.makeText(getApplicationContext(), "No matching Patient for entered code", Toast.LENGTH_SHORT).show();
					}
				} else
				{
					verifiedAccess = false;
					Toast.makeText(getApplicationContext(), "Please enter an access code", Toast.LENGTH_SHORT).show();
				}
			}
		});

		cancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				verifiedAccess = false;
				dialog.hide();
			}
		});
	}

	private void createAccount()
	{
		mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							Log.d(TAG, "createUserWithEmail:success");
							user = mAuth.getCurrentUser();
							createAccountInDatabase();
						} else
						{
							Log.w(TAG, "createUserWithEmail:failure", task.getException());
							Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private void createAccountInDatabase()
	{
		int accountTypeSelected = accountTypeSelector.getCheckedRadioButtonId();
		selectedAccountType = findViewById(accountTypeSelected);
		if (selectedAccountType.getText().equals(FirebaseCalls.Patient))
		{
			AccountInformation.patientID = mAuth.getCurrentUser().getUid();
			FirebaseCalls.createPatient(generateRandom(), firstName, lastName);
		} else
		{
			for (String PID : patientInviteCodes.keySet())
			{
				if (patientInviteCodes.get(PID).equals(patientAccessCode))
				{
					FirebaseCalls.createReader(firstName, lastName, PID);
				}
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
				.addOnCompleteListener(new OnCompleteListener<Void>()
				{
					@Override
					public void onComplete(@NonNull Task<Void> task)
					{
						if (task.isSuccessful())
						{
							Log.d(TAG, "User profile updated.");
							Intent intent = new Intent(getApplicationContext(), CheckAccountTypeActivity.class);
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

	static boolean checkForPatientCode(String enteredCode)
	{
		for (String user : patientInviteCodes.keySet())
		{
			if (patientInviteCodes.get(user).equals(enteredCode)) return true;
		}
		return false;
	}

	private static void getInviteCodes(DatabaseReference patients)
	{
		for (String id : patientIds)
		{
			final String finalId = id;
			patients.child(id).child(FirebaseCalls.InviteCode).addListenerForSingleValueEvent(new ValueEventListener()
			{
				@Override
				public void onDataChange(DataSnapshot dataSnapshot)
				{
					patientInviteCodes.put(finalId, dataSnapshot.getValue().toString());
				}

				@Override
				public void onCancelled(DatabaseError databaseError)
				{

				}
			});
		}
	}

}
