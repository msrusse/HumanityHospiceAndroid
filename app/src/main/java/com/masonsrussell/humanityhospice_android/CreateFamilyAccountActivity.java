package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateFamilyAccountActivity extends AppCompatActivity
{
	private FirebaseAuth mAuth;
	private FirebaseAuth mAuth2;
	private DrawerLayout mDrawerLayout;
	private TextView navHeaderName, navHeaderEmail;
	private Button createAccountButton;
	private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, verifyPasswordEditText;
	private String password, email, firstName, lastName;
	private FirebaseUser originalUser, newUser;
	private static final String TAG = "CreateFamilyAccount";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_family_account);
		mDrawerLayout = findViewById(R.id.drawer_layout);
		mAuth = FirebaseAuth.getInstance();
		createAccountButton = findViewById(R.id.createAccountButton);
		firstNameEditText = findViewById(R.id.firstNameEditText);
		lastNameEditText = findViewById(R.id.lastNameEditText);
		emailEditText = findViewById(R.id.emailEditText);
		passwordEditText = findViewById(R.id.passwordEditText);
		verifyPasswordEditText = findViewById(R.id.reenterPasswordEditText);
		originalUser = mAuth.getCurrentUser();
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionbar = getSupportActionBar();
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener()
				{
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem)
					{
						// set item as selected to persist highlight
						switch(menuItem.toString())
						{
							case "My Journal":
								Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
								startActivity(intent);
								finish();
								break;
							case "Encouragement Board":
								Intent intent2 = new Intent(getApplicationContext(), EncouragementBoardActivity.class);
								startActivity(intent2);
								finish();
								break;
							case "My Photo Album":

								break;
							case "Create Family Account":
								mDrawerLayout.closeDrawers();
								break;
							case "Invite People":
								Intent intent3 = new Intent(getApplicationContext(), InvitePeopleActivity.class);
								startActivity(intent3);
								finish();
								break;
							case "Sign Out":
								mAuth.signOut();
								finish();
								break;
							case "About Humanity Hospice":

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

		createAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (!TextUtils.isEmpty(emailEditText.getText()) && !TextUtils.isEmpty(firstNameEditText.getText()) && !TextUtils.isEmpty(lastNameEditText.getText()) && !TextUtils.isEmpty(passwordEditText.getText()) && !TextUtils.isEmpty(verifyPasswordEditText.getText()))
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
						Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
					}

				}
				else
				{
					Toast.makeText(getApplicationContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);
				navHeaderName = findViewById(R.id.navHeaderName);
				navHeaderEmail = findViewById(R.id.navHeaderEmail);
				navHeaderEmail.setText(mAuth.getCurrentUser().getEmail());
				navHeaderName.setText(mAuth.getCurrentUser().getDisplayName());
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			mAuth.signOut();
			finish();
		}
	}

	private void createAccount()
	{
		final String patientCode = mAuth.getCurrentUser().getUid();
		FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
				.setDatabaseUrl("https://console.firebase.google.com/u/0/project/humanityhospice-9ce45/")
				.setApiKey("AIzaSyAwIcQm5O23O87FXYTa8jkPPeOlu9HRJCM")
				.setApplicationId("humanityhospice-9ce45").build();

		try { FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "HumanityHospice");
			mAuth2 = FirebaseAuth.getInstance(myApp);
		} catch (IllegalStateException e){
			mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("HumanityHospice"));
		}
		mAuth2.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {


						if (!task.isSuccessful()) {
							String ex = task.getException().toString();
							Toast.makeText(CreateFamilyAccountActivity.this, "Registration Failed"+ex,
									Toast.LENGTH_LONG).show();
						}
						else
						{
							createAccountInDatabase(patientCode, mAuth2.getUid());
						}
					}
				});

	}

	private void createAccountInDatabase(String patientCode, String familyID)
	{
		FirebaseCalls.createFamily(firstName, lastName, patientCode, familyID);
		addPersonalData();
	}

	private void addPersonalData()
	{
		Toast.makeText(CreateFamilyAccountActivity.this, "Registration successful",
				Toast.LENGTH_SHORT).show();
		mAuth2.signOut();
		Intent intent = new Intent(getApplicationContext(), CreateFamilyAccountActivity.class);
		startActivity(intent);
	}
}
