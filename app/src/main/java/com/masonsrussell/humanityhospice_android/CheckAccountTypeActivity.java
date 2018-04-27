package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckAccountTypeActivity extends AppCompatActivity
{
	private ProgressBar progressBar;
	private FirebaseAuth mAuth;
	private FirebaseDatabase mDatabase;
	private static final String TAG = "CheckAccountType";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_account_type);
		progressBar = findViewById(R.id.progressBar);
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
					isReader();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	private void isReader()
	{
		Toast.makeText(getApplicationContext(), "You are a Reader", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
		startActivity(intent);
		finish();
	}

	private void isPatient()
	{
		Toast.makeText(getApplicationContext(), "You are a Patient", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
		startActivity(intent);
		finish();
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
					isPatient();
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

}
