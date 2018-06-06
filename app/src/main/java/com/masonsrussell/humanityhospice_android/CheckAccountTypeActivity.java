package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class CheckAccountTypeActivity extends AppCompatActivity
{
	private FirebaseAuth mAuth;
	private FirebaseDatabase mDatabase;
	private FirebaseUser user;
	private static final String TAG = "CheckAccountType";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_account_type);
		ProgressBar progressBar = findViewById(R.id.progressBar);
		progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.primaryPurple), PorterDuff.Mode.MULTIPLY);
		mDatabase = FirebaseDatabase.getInstance();
		mAuth = FirebaseAuth.getInstance();

		getPatients();
	}

	private void getReaders()
	{
		DatabaseReference readersRef = mDatabase.getReference("Readers");
		readersRef.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> allReaders = (HashMap) dataSnapshot.getValue();
				if (allReaders.containsKey(mAuth.getCurrentUser().getUid()))
				{
					getReaderPatientID();
				}
				else
				{
					getFamily();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	private void isReader(String patientID, String profilePictureURL)
	{
		AccountInformation.setAccountInfo("Reader", mAuth.getCurrentUser().getDisplayName(), patientID, mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getPhotoUrl().toString());
		Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
		startActivity(intent);
		finish();
	}

	private void isPatient(String profilePictureURL)
	{
		AccountInformation.setAccountInfo("Patient", mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getPhotoUrl().toString());
		Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
		startActivity(intent);
		finish();
	}

	private void isFamily(String patientID, String profilePictureURL)
	{
		if (mAuth.getCurrentUser().getDisplayName() == null)
		{
			getFamilyName(patientID, profilePictureURL);
		}
		else
		{
			AccountInformation.setAccountInfo("Family", mAuth.getCurrentUser().getDisplayName(), patientID, mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getPhotoUrl().toString());
			Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void getFamilyName(final String patientID, final String profilePictureURL)
	{
		DatabaseReference familyRef = mDatabase.getReference("Family");
		familyRef.child(mAuth.getCurrentUser().getUid()).child("MetaData").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> metaDataValues = (HashMap) dataSnapshot.getValue();
				String firstName = metaDataValues.get("firstName").toString();
				String lastName = metaDataValues.get("lastName").toString();
				user = mAuth.getCurrentUser();
				addPersonalData(firstName, lastName, patientID, profilePictureURL);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	private void addPersonalData(String firstName, String lastName, final String patientID, final String profilePictureURL)
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
						{isFamily(patientID, profilePictureURL);
						}
					}
				});
	}

	private void getPatients()
	{
		DatabaseReference patientsRef = mDatabase.getReference("Patients");
		patientsRef.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> allPatients = (HashMap) dataSnapshot.getValue();
				if (allPatients.containsKey(mAuth.getCurrentUser().getUid()))
				{
					getPatientProfilePic();
				}
				else
				{
					getReaders();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	private void getFamily()
	{
		DatabaseReference familyRef = mDatabase.getReference("Family");
		familyRef.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> allFamily = (HashMap) dataSnapshot.getValue();
				if (allFamily.containsKey(mAuth.getCurrentUser().getUid()))
				{
					getFamilyPatientID();
				}
				else
				{
					getReaders();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	private void getFamilyPatientID()
	{
		DatabaseReference familyRef = mDatabase.getReference("Family");
		DatabaseReference individualFamilyRef = familyRef.child(mAuth.getCurrentUser().getUid());
		individualFamilyRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> familyInfo = (HashMap) dataSnapshot.getValue();
				if (familyInfo.keySet().contains("profilePictureURL")) isFamily(familyInfo.get("PatientID").toString(), familyInfo.get("profilePictureURL").toString());
				else isFamily(familyInfo.get("PatientID").toString(), null);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	private void getPatientProfilePic()
	{
		DatabaseReference patientref = mDatabase.getReference("Patients").child(mAuth.getCurrentUser().getUid());
		patientref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> patientInfo = (HashMap) dataSnapshot.getValue();
				if (patientInfo.keySet().contains("profilePictureURL")) isPatient(patientInfo.get("profilePictureURL").toString());
				else isPatient(null);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{

			}
		});
	}

	private void getReaderPatientID()
	{
		DatabaseReference readerRef = mDatabase.getReference("Readers");
		DatabaseReference individualReaderRef = readerRef.child(mAuth.getCurrentUser().getUid());
		individualReaderRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> readerInfo = (HashMap) dataSnapshot.getValue();
				if (readerInfo.keySet().contains("profilePictureURL")) isReader(readerInfo.get("ReadingFrom").toString(), readerInfo.get("profilePictureURL").toString());
				else isReader(readerInfo.get("ReadingFrom").toString(), null);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	@Override
	public void onBackPressed()
	{
		mAuth.signOut();
		finish();
	}

}
