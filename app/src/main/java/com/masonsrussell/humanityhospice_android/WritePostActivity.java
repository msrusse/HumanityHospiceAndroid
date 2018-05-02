package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WritePostActivity extends AppCompatActivity
{
	Button writePostButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_post);
		writePostButton = findViewById(R.id.writePostButton);

		writePostButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}
}
