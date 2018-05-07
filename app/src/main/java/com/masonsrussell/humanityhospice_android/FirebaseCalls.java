package com.masonsrussell.humanityhospice_android;

import android.net.Uri;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FirebaseCalls
{
	private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
	private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
	private static StorageReference storageReference;

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
		StorageReference profilePicturesRef = storageReference.child("ProfilePictures");
		StorageReference currentUserProfileRef = profilePicturesRef.child(mAuth.getCurrentUser().getUid());
		StorageReference imageRef = storageReference.child("ProfilePictures/" + mAuth.getCurrentUser().getUid() + "/" + file.getLastPathSegment());

	}

	public static void addAlbumPictures(Uri file)
	{
		StorageReference albumPicturesRef = storageReference.child("PhotoAlbum");
		StorageReference patientAlbumsRef = albumPicturesRef.child(AccountInformation.patientID);
	}
}
