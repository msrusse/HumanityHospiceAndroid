package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
	private FirebaseAuth mAuth;
	Button signInButton;
	TextView createAccountView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAuth = FirebaseAuth.getInstance();


	}

	@Override
	public void onStart() {
		super.onStart();
		// Check if user is signed in (non-null) and update UI accordingly.
		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (currentUser != null)
		{
			Intent intent = new Intent(this, JournalActivity.class);
			startActivity(intent);
		}
		else
		{
			onLoad();
		}
	}

	private void onLoad()
	{
		signInButton = findViewById(R.id.signInButton);
		createAccountView = findViewById(R.id.createAccountView);

		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
			}
		});

		createAccountView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
				startActivity(intent);
			}
		});
	}


}
