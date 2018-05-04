package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.HashMap;
import java.util.Map;

public class WritePostActivity extends AppCompatActivity
{
	private Button writePostButton;
	private EditText postBox;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_post);
		writePostButton = findViewById(R.id.writePostButton);
		postBox = findViewById(R.id.postEditText);

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
}
