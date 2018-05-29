package com.masonsrussell.humanityhospice_android;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

public class FirebaseCalls
{
	private static final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
	private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
	private static StorageReference storageReference;
	private static FirebaseStorage storage;

	public static void createPost(String post)
	{
		DatabaseReference posts = mDatabase.getReference("Journals");
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put("poster", AccountInformation.username);
		posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime());
		posterInfo.put("post", post);
		newPost.updateChildren(posterInfo);
	}

	public static void createEncouragementPost(String post, int totalPosts)
	{
		DatabaseReference posts = mDatabase.getReference("EncouragementBoard");
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put("poster", AccountInformation.username);
		posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime());
		posterInfo.put("post", post);
		posterInfo.put("posterID", mAuth.getCurrentUser().getUid());
		newPost.updateChildren(posterInfo);
	}

	private static void createFirstPost(String fName, String lName)
	{
		DatabaseReference posts = mDatabase.getReference("Journals");
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.child("post0");

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put("poster", fName + " " + lName);
		posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime());
		posterInfo.put("post", "Joined Humanity Hospice");
		newPost.updateChildren(posterInfo);
	}

	public static void createPatient(String inviteCode, String fName, String lName)
	{
		DatabaseReference patients = mDatabase.getReference("Patients");
		DatabaseReference individualPatient = patients.child(AccountInformation.patientID);
		DatabaseReference patientMetaData = individualPatient.child("MetaData");

		Map<String, Object> patientInfo = new HashMap<>();
		patientInfo.put("FamilyID", "");
		patientInfo.put("InviteCode", inviteCode);
		individualPatient.updateChildren(patientInfo);

		Map<String, Object> metaDataMap = new HashMap<>();
		metaDataMap.put("DOB", 0);
		metaDataMap.put("firstName", fName);
		metaDataMap.put("lastName", lName);
		patientMetaData.updateChildren(metaDataMap);
		createFirstPost(fName, lName);
	}

	public static void createReader(String fName, String lName, String patientID)
	{
		DatabaseReference patients = mDatabase.getReference("Readers");
		DatabaseReference individualReader = patients.child(mAuth.getCurrentUser().getUid());
		DatabaseReference readerMetaData = individualReader.child("MetaData");
		DatabaseReference patientsToReadFrom = individualReader.child("Patients");

		Map<String, Object> readerInfo = new HashMap<>();
		readerInfo.put("ReadingFrom", patientID);
		individualReader.updateChildren(readerInfo);

		Map<String, Object> metaDataMap = new HashMap<>();
		metaDataMap.put("firstName", fName);
		metaDataMap.put("lastName", lName);
		readerMetaData.updateChildren(metaDataMap);

		Map<String, Object> patientsReadingMap = new HashMap<>();
		patientsReadingMap.put(patientID, true);
		patientsToReadFrom.updateChildren(patientsReadingMap);
	}

	public static void createFamily(String fName, String lName, String familyID)
	{
		DatabaseReference family = mDatabase.getReference("Family");
		DatabaseReference individualFamily = family.child(familyID);
		DatabaseReference familyMetaData = individualFamily.child("MetaData");

		Map<String, Object> patientInfo = new HashMap<>();
		patientInfo.put("PatientID", AccountInformation.patientID);
		individualFamily.updateChildren(patientInfo);

		Map<String, Object> metaDataMap = new HashMap<>();
		metaDataMap.put("DOB", 0);
		metaDataMap.put("firstName", fName);
		metaDataMap.put("lastName", lName);
		familyMetaData.updateChildren(metaDataMap);
	}

	public static void addAdditionalPatientForReader(String patientID)
	{
		DatabaseReference readers = mDatabase.getReference("Readers");
		DatabaseReference individualReader = readers.child(mAuth.getCurrentUser().getUid());
		DatabaseReference readersPatients = individualReader.child("Patients");
		DatabaseReference readingFromRef = individualReader.child("ReadingFrom");

		Map<String, Object> readersPatientsMap = new HashMap<>();
		readersPatientsMap.put(patientID, true);
		readersPatients.updateChildren(readersPatientsMap);

		readingFromRef.setValue(patientID);
	}

	public static void updatePatientReadingFrom(String patientID)
	{
		DatabaseReference readers = mDatabase.getReference("Readers");
		DatabaseReference individualReader = readers.child(mAuth.getCurrentUser().getUid());
		DatabaseReference readingFromRef = individualReader.child("ReadingFrom");
		readingFromRef.setValue(patientID);
	}

	public static void addProfilePictures(Uri file)
	{
		storage = FirebaseStorage.getInstance();
		storageReference = storage.getReference();

		StorageReference profileImageRef = storageReference.child("ProfilePictures/" + mAuth.getCurrentUser().getUid() + "/ProfilePicture");
		profileImageRef.putFile(file);
	}

	public static void addAlbumPictures(Uri file, final String post, String activity)
	{
		storage = FirebaseStorage.getInstance();
		storageReference = storage.getReference();

		final StorageReference albumImageRef = storageReference.child(activity + "/" + AccountInformation.patientID + "/post-" + Calendar.getInstance().getTime().getTime());
		albumImageRef.putFile(file)
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
					{
						albumImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
							@Override
							public void onSuccess(Uri uri)
							{
								createAlbumPost(post, uri.toString());
							}
						});
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d("addAlbumPicture", e.getMessage());
					}
				});
	}

	private static void createAlbumPost(String post, Object imageURL)
	{
		DatabaseReference posts = mDatabase.getReference("PhotoAlbum");
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put("caption", post);
		posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime());
		posterInfo.put("url", imageURL);
		newPost.updateChildren(posterInfo);
	}

	public static void createPhotoRefFromCamera(byte[] data, final String post, String activity)
	{
		final StorageReference albumImageRef = storageReference.child(activity + "/" + AccountInformation.patientID + "/post-" + Calendar.getInstance().getTime().getTime());
		albumImageRef.putBytes(data)
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
					{
						albumImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
							@Override
							public void onSuccess(Uri uri)
							{
								createAlbumPost(post, uri.toString());
							}
						});
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d("addAlbumPicture", e.getMessage());
					}
				});
	}
}
