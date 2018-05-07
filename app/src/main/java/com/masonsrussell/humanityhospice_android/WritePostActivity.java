package com.masonsrussell.humanityhospice_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WritePostActivity extends AppCompatActivity
{
	private EditText postBox;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_post);
		Button writePostButton = findViewById(R.id.writePostButton);
		Button closeButton = findViewById(R.id.closeButton);
		postBox = findViewById(R.id.postEditText);

		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		writePostButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (TextUtils.isEmpty(postBox.getText()))
				{
					Toast.makeText(getApplicationContext(), "A post must be written to publish", Toast.LENGTH_SHORT).show();
				}
				else
				{
					FirebaseCalls.createPost(postBox.getText().toString());
					finish();
				}
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		finish();
	}
}
