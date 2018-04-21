package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
	Button signInButton;
	TextView createAccountView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		signInButton = findViewById(R.id.signInButton);
		createAccountView = findViewById(R.id.createAccountView);

		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				//Intent intent = new Intent(this, LoginActivity.class);
				//startActivity(intent);
			}
		});

		createAccountView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				//Intent intent = new Intent(this, CreateAccountActivity.class);
				//startActivity(intent);
			}
		});
	}
}
