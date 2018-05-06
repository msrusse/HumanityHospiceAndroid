package com.masonsrussell.humanityhospice_android;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.util.List;
import java.util.Map;

public class ChangePatientActivity extends AppCompatActivity
{
	ListView patientListView;
	Map<String, String> patientInformation  = new HashMap<>();
	ArrayList<String> patientNames = new ArrayList<>();
	private DrawerLayout mDrawerLayout;
	TextView navHeaderName, navHeaderEmail;
	private FirebaseDatabase mDatabase;
	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_patient);
		mDatabase = FirebaseDatabase.getInstance();
		mDrawerLayout = findViewById(R.id.drawer_layout);
		patientListView = findViewById(R.id.patientListView);
		mAuth = FirebaseAuth.getInstance();
		getPatients();
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionbar = getSupportActionBar();
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
		setReaderNavMenu();
	}

	private void getPatients()
	{
		DatabaseReference readersRef = mDatabase.getReference("Readers");
		DatabaseReference individualReadersRef = readersRef.child(mAuth.getCurrentUser().getUid());
		DatabaseReference readersPatientsRef = individualReadersRef.child("Patients");

		readersPatientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> allPatients = (HashMap) dataSnapshot.getValue();
				for (String UID : allPatients.keySet())
				{
					firebasePatientNamesCall(UID);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	private void firebasePatientNamesCall(String patient)
	{
		final String patientUID = patient;
		DatabaseReference patientsRef = mDatabase.getReference("Patients");
		DatabaseReference individualPatientRef = patientsRef.child(patient);
		DatabaseReference patientMetaDataRef = individualPatientRef.child("MetaData");
		patientMetaDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> allPatients = (HashMap) dataSnapshot.getValue();
				patientInformation.put(allPatients.get("firstName").toString() + " " + allPatients.get("lastName").toString(), patientUID);
				patientNames.add(allPatients.get("firstName").toString() + " " + allPatients.get("lastName").toString());
				setListView();
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
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
								Intent intent3 = new Intent(getApplicationContext(), AddPatientActivity.class);
								startActivity(intent3);
								finish();
								break;
							case "Change Patient":
								mDrawerLayout.closeDrawers();
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

	private void setListView()
	{
		try
		{
			ListAdapter listAdapter = new CustomListAdapter(ChangePatientActivity.this, R.layout.patient_listview_adapter, patientNames);
			patientListView.setAdapter(listAdapter);
			patientListView.setOnItemClickListener(listPairedClickItem);
		}
		catch (Exception ex)
		{
			Log.d("ChangePatientActivity", ex.getMessage());
		}
	}

	private class CustomListAdapter extends ArrayAdapter<String>
	{

		private Context mContext;
		private int id;
		private List<String> items;

		public CustomListAdapter(Context context, int textViewResourceId, List<String> patients)
		{
			super(context, textViewResourceId, patients);
			mContext = context;
			id = textViewResourceId;
			items = patients;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent)
		{
			View mView = v;
			if (mView == null)
			{
				LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				mView = vi.inflate(id, null);
			}

			TextView postBody = mView.findViewById(R.id.postBodyTextView);

			if (items.get(position) != null)
			{
				postBody.setText(items.get(position));
			}

			return mView;
		}
	}

	private AdapterView.OnItemClickListener listPairedClickItem = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			String selected = patientListView.getItemAtPosition(arg2).toString();
			getSelectedPatientUID(selected);
		}
	};

	private void getSelectedPatientUID(String patientName)
	{
		String user = patientInformation.get(patientName);
		FirebaseCalls.updatePatientReadingFrom(user);
		AccountInformation.patientID = user;
		Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
		startActivity(intent);
		finish();
	}
}
