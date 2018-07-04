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
		getProfilePictures();
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

	private void isReader(String patientID)
	{
		if (mAuth.getCurrentUser().getPhotoUrl() != null)
		{
			AccountInformation.setAccountInfo("Reader", mAuth.getCurrentUser().getDisplayName(), patientID, mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getPhotoUrl().toString());
			Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
			startActivity(intent);
			finish();
		}
		else
		{
			AccountInformation.setAccountInfo("Reader", mAuth.getCurrentUser().getDisplayName(), patientID, mAuth.getCurrentUser().getEmail(), null);
			Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void isPatient()
	{
		if (mAuth.getCurrentUser().getPhotoUrl() != null)
		{
			AccountInformation.setAccountInfo(FirebaseCalls.Patient, mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getPhotoUrl().toString());
			Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
			startActivity(intent);
			finish();
		}
		else
		{
			AccountInformation.setAccountInfo(FirebaseCalls.Patient, mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getEmail(), null);
			Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void isFamily(String patientID, String profilePictureURL)
	{
		if (mAuth.getCurrentUser().getDisplayName() == null)
		{
			getFamilyName(patientID, profilePictureURL);
		}
		else if (profilePictureURL != null)
		{
			AccountInformation.setAccountInfo(FirebaseCalls.Family, mAuth.getCurrentUser().getDisplayName(), patientID, mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getPhotoUrl().toString());
			Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
			startActivity(intent);
			finish();
		}
		else
		{
			AccountInformation.setAccountInfo(FirebaseCalls.Family, mAuth.getCurrentUser().getDisplayName(), patientID, mAuth.getCurrentUser().getEmail(), null);
			Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void getFamilyName(final String patientID, final String profilePictureURL)
	{
		DatabaseReference familyRef = mDatabase.getReference(FirebaseCalls.Family);
		familyRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> metaDataValues = (HashMap) dataSnapshot.getValue();
				String firstName = metaDataValues.get(FirebaseCalls.FirstName).toString();
				String lastName = metaDataValues.get(FirebaseCalls.LastName).toString();
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
		DatabaseReference patientsRef = mDatabase.getReference(FirebaseCalls.Patients);
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
		DatabaseReference familyRef = mDatabase.getReference(FirebaseCalls.Family);
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
		DatabaseReference familyRef = mDatabase.getReference(FirebaseCalls.Family);
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
		DatabaseReference patientref = mDatabase.getReference(FirebaseCalls.Patients).child(mAuth.getCurrentUser().getUid());
		patientref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> patientInfo = (HashMap) dataSnapshot.getValue();
				isPatient();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{

			}
		});
	}

	private void getReaderPatientID()
	{
		DatabaseReference readerRef = mDatabase.getReference(FirebaseCalls.Readers);
		DatabaseReference individualReaderRef = readerRef.child(mAuth.getCurrentUser().getUid());
		individualReaderRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> readerInfo = (HashMap) dataSnapshot.getValue();
				isReader(readerInfo.get(FirebaseCalls.ReadingFrom).toString());
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	private void getProfilePictures()
	{
		DatabaseReference profilePictures = mDatabase.getReference(FirebaseCalls.ProfilePictures);
		profilePictures.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> profilePicrtures = (HashMap) dataSnapshot.getValue();
                AccountInformation.profilePictures = profilePicrtures;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
