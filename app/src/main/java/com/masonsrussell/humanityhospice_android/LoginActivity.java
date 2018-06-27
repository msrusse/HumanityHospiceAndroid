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

public class LoginActivity extends AppCompatActivity
{
	Button signInButton;
	EditText passwordEditText, emailEditText;
	TextView createAccountView, resetPasswordView;
	private static final String TAG = "LoginActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		signInButton = findViewById(R.id.signInButton);
		passwordEditText = findViewById(R.id.passwordEditText);
		emailEditText = findViewById(R.id.emailEditText);
		createAccountView = findViewById(R.id.createAccountView);
		resetPasswordView = findViewById(R.id.resetPasswordView);

		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (!TextUtils.isEmpty(passwordEditText.getText()) && !TextUtils.isEmpty(emailEditText.getText()))
				{
					login(emailEditText.getText().toString(), passwordEditText.getText().toString());
				}
				else
				{
					Toast.makeText(LoginActivity.this, "Please Enter Both Fields", Toast.LENGTH_SHORT).show();
				}
			}
		});

		createAccountView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
				startActivity(intent);
				finish();
			}
		});

		resetPasswordView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FirebaseAuth mAuth = FirebaseAuth.getInstance();
				if (!TextUtils.isEmpty(emailEditText.getText())) {
					mAuth.sendPasswordResetEmail(emailEditText.getText().toString())
							.addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									if (task.isSuccessful()) {
										// do something when mail was sent successfully.
										Toast.makeText(getApplicationContext(), "Check your email for further instructions", Toast.LENGTH_LONG).show();
									} else {
										// ...

									}
								}
							});
				}
				else if (TextUtils.isEmpty(emailEditText.getText()))
				{
					Toast.makeText(getApplicationContext(), "Enter a valid email to reset password", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void login(String email, String password)
	{
		FirebaseAuth mAuth = FirebaseAuth.getInstance();
		mAuth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "signInWithEmail:success");
							Intent intent = new Intent(getApplicationContext(), CheckAccountTypeActivity.class);
							startActivity(intent);
							finish();
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInWithEmail:failure", task.getException());
							Toast.makeText(LoginActivity.this, task.getException().getMessage(),
									Toast.LENGTH_SHORT).show();
						}

						// ...
					}
				});
	}
}
