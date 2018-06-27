package com.masonsrussell.humanityhospice_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AddPatientActivity extends AppCompatActivity
{

	static ArrayList<String> patientIds = new ArrayList<>();
	static HashMap<String, String> patientInviteCodes = new HashMap<>();
	private DrawerLayout mDrawerLayout;
	Bitmap bitmap = null;
	byte[] data = null;
	Uri selectedImage = null;
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
		navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener()
				{
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem)
					{
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
								Intent intent1 = new Intent(getApplicationContext(), PhotoAlbumActivity.class);
								startActivity(intent1);
								finish();
								break;
							case "Sign Out":
								mAuth.signOut();
								Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
								startActivity(homeIntent);
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
						mDrawerLayout.closeDrawers();
						return true;
					}
				});

		mDrawerLayout.addDrawerListener(
				new DrawerLayout.DrawerListener() {
					@Override
					public void onDrawerSlide(View drawerView, float slideOffset) {
					}

					@Override
					public void onDrawerOpened(View drawerView) {
					}

					@Override
					public void onDrawerClosed(View drawerView) {
					}

					@Override
					public void onDrawerStateChanged(int newState) {
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
				ImageView profilePictureView = findViewById(R.id.profilePicImageView);
				if (AccountInformation.profilePictureURL != null)
				{
					Glide.with(this).load(AccountInformation.profilePictureURL).into(profilePictureView);
				}
				LinearLayout profileInfo = findViewById(R.id.profileInfo);
				profileInfo.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						profileImagePicker();
					}
				});
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void profileImagePicker()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_menu_camera)
				.setTitle("Update Profile Picture")
				.setMessage("Either select an image from the gallery or take a new photo")
				.setPositiveButton("Choose Photo", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						chooseImage();
					}
				})
				.setNegativeButton("Take Photo", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						takePicture();
					}
				})
				.show();
	}

	private void chooseImage() {
		Intent pickPhoto = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(pickPhoto , 1);
	}

	private void takePicture()
	{
		Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch(requestCode) {
			case 0:
				if(resultCode == RESULT_OK && imageReturnedIntent != null){
					bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					data = baos.toByteArray();
					FirebaseCalls.addProfilePictureFromCamera(data);
				}

				break;
			case 1:
				if(resultCode == RESULT_OK){
					selectedImage = imageReturnedIntent.getData();
					FirebaseCalls.addProfilePictureFromGallery(selectedImage);
				}
				break;
		}
	}
}
