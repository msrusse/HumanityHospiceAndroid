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

public class WriteEncouragementActivity extends AppCompatActivity
{
	private Button writePostButton;
	private EditText postBox;
	private FirebaseAuth mAuth;
	private FirebaseDatabase mDatabase;
	int totalPosts = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_post);
		writePostButton = findViewById(R.id.writePostButton);
		postBox = findViewById(R.id.postEditText);
		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance();

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
					getTotalPosts(postBox.getText().toString());
					finish();
				}
			}
		});
	}

	private void getTotalPosts(final String post)
	{
		DatabaseReference postsRef = mDatabase.getReference("EncouragementBoard");
		DatabaseReference pateintRef = postsRef.child(mAuth.getCurrentUser().getUid());
		pateintRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				if (dataSnapshot.getValue() != null)
				{
					Map<Object, Object> snapshot = (HashMap) dataSnapshot.getValue();
					for (Object key : snapshot.keySet())
					{
						totalPosts++;
					}
				}
				FirebaseCalls.createEncouragementPost(post, totalPosts);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}
}
