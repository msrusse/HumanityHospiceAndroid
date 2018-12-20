package com.masonsrussell.humanityhospice_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WriteEncouragementActivity extends AppCompatActivity
{
	private EditText postBox;
	private FirebaseAuth mAuth;
	private FirebaseDatabase mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_encouragement);
		Button writePostButton = findViewById(R.id.writePostButton);
		postBox = findViewById(R.id.postEditText);
		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance();
		ImageView profilePicture = findViewById(R.id.profilePicture);
		if (AccountInformation.profilePictureURL != null)
		{
			GlideApp.with(this)
					.load(mAuth.getCurrentUser().getPhotoUrl().toString())
					.apply(RequestOptions.circleCropTransform())
					.into(profilePicture);
		}
		writePostButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (TextUtils.isEmpty(postBox.getText()))
				{
					Toast.makeText(getApplicationContext(), "A message must be written to be sent", Toast.LENGTH_SHORT).show();
				}
				else
				{
					getTotalPosts(postBox.getText().toString());
					finish();
				}
			}
		});
		Button cancelButton = findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
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
				FirebaseCalls.createEncouragementPost(post);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}
}
