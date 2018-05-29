package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPatientActivity extends AppCompatActivity
{

	static ArrayList<String> patientIds = new ArrayList<>();
	static HashMap<String, String> patientInviteCodes = new HashMap<>();
	private DrawerLayout mDrawerLayout;
	TextView navHeaderName, navHeaderEmail;
	String patientID;
	FirebaseDatabase mDatabase;
	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_patient);
		mDatabase = FirebaseDatabase.getInstance();
		mDrawerLayout = findViewById(R.id.drawer_layout);
		Button enter = findViewById(R.id.enterButton);
		final EditText accessCode = findViewById(R.id.accessCode);
		mAuth = FirebaseAuth.getInstance();
		final DatabaseReference patients = mDatabase.getReference("Patients");
		patients.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> allPatients = (HashMap) dataSnapshot.getValue();
				patientIds.addAll(allPatients.keySet());
				getInviteCodes(patients);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionbar = getSupportActionBar();
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
		setReaderNavMenu();

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
						FirebaseCalls.addAdditionalPatientForReader(patientID);
						Toast.makeText(getApplicationContext(),"Additional Patient Added", Toast.LENGTH_LONG).show();
						Intent intent = new Intent(getApplicationContext(), AddPatientActivity.class);
						startActivity(intent);
						finish();
					} else
					{
						Toast.makeText(getApplicationContext(), "No matching Patient for entered code", Toast.LENGTH_SHORT).show();
					}
				} else
				{
					Toast.makeText(getApplicationContext(), "Please enter an access code", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private boolean checkForPatientCode(String enteredCode)
	{
		for (String user : patientInviteCodes.keySet())
		{
			if (patientInviteCodes.get(user).equals(enteredCode))
			{
				patientID = user;
				return true;
			}
		}
		return false;
	}

	private static void getInviteCodes(DatabaseReference patients)
	{
		for (String id : patientIds)
		{
			final String finalId = id;
			patients.child(id).child("InviteCode").addListenerForSingleValueEvent(new ValueEventListener()
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

	private void setReaderNavMenu()
	{
		NavigationView navigationView = findViewById(R.id.nav_view);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener()
				{
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem)
					{

						// set item as selected to persist highlight
						switch(menuItem.toString())
						{
							case "Journal":
								Intent intent0 = new Intent(getApplicationContext(), JournalActivity.class);
								startActivity(intent0);
								finish();
								break;
							case "Encouragement Board":
								Intent intent = new Intent(getApplicationContext(), EncouragementBoardActivity.class);
								startActivity(intent);
								finish();
								break;
							case "Photo Album":

								break;
							case "Sign Out":
								mAuth.signOut();
								finish();
								break;
							case "About Humanity Hospice":
								Intent intent2 = new Intent(getApplicationContext(), AboutHumanityHospiceActivity.class);
								startActivity(intent2);
								finish();
								break;
							case "Add Patient":
								mDrawerLayout.closeDrawers();
								break;
							case "Change Patient":
								Intent intent3 = new Intent(getApplicationContext(), ChangePatientActivity.class);
								startActivity(intent3);
								finish();
								break;
						}
						// close drawer when item is tapped
						mDrawerLayout.closeDrawers();

						// Add code here to update the UI based on the item selected
						// For example, swap UI fragments here

						return true;
					}
				});

		mDrawerLayout.addDrawerListener(
				new DrawerLayout.DrawerListener() {
					@Override
					public void onDrawerSlide(View drawerView, float slideOffset) {
						// Respond when the drawer's position changes
					}

					@Override
					public void onDrawerOpened(View drawerView) {
						// Respond when the drawer is opened

					}

					@Override
					public void onDrawerClosed(View drawerView) {
						// Respond when the drawer is closed
					}

					@Override
					public void onDrawerStateChanged(int newState) {
						// Respond when the drawer motion state changes
					}
				}
		);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);
				navHeaderName = findViewById(R.id.navHeaderName);
				navHeaderEmail = findViewById(R.id.navHeaderEmail);
				navHeaderEmail.setText(AccountInformation.email);
				navHeaderName.setText(AccountInformation.username);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}