package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.awt.font.TextAttribute;

public class CreateAccountActivity extends AppCompatActivity
{
	Button createAccountButton;
	EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, verifyPasswordEditText;
	TextView loginView;
	private FirebaseAuth mAuth;
	String password, email, firstName, lastName;
	private static final String TAG = "CreateAccountActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		getSupportActionBar().setTitle("Create Account");
		firstNameEditText = findViewById(R.id.firstNameEditText);
		lastNameEditText = findViewById(R.id.lastNameEditText);
		emailEditText = findViewById(R.id.emailEditText);
		passwordEditText = findViewById(R.id.passwordEditText);
		verifyPasswordEditText = findViewById(R.id.reenterPasswordEditText);
		loginView = findViewById(R.id.loginView);
		createAccountButton = findViewById(R.id.createAccountButton);
		mAuth = FirebaseAuth.getInstance();

		createAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (!TextUtils.isEmpty(emailEditText.getText()) && TextUtils.isEmpty(firstNameEditText.getText()) && TextUtils.isEmpty(lastNameEditText.getText()) && TextUtils.isEmpty(passwordEditText.getText()) && TextUtils.isEmpty(verifyPasswordEditText.getText()))
				{
					email = emailEditText.getText().toString();
					firstName = firstNameEditText.getText().toString();
					lastName = lastNameEditText.getText().toString();
					if(passwordEditText.getText().toString().equals(verifyPasswordEditText.getText().toString()))
					{
						password = passwordEditText.getText().toString();
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
							FirebaseUser user = mAuth.getCurrentUser();
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "createUserWithEmail:failure", task.getException());
							Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();
						}

						// ...
					}
				});
	}
}
