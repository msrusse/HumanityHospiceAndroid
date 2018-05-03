package com.masonsrussell.humanityhospice_android;

import android.provider.ContactsContract;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FirebaseCalls
{
	private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
	private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

	public static void createPost(String post, int totalPosts)
	{
		DatabaseReference posts = mDatabase.getReference("Journals");
		DatabaseReference patientsPosts = posts.child(mAuth.getCurrentUser().getUid());
		DatabaseReference newPost = patientsPosts.child("post" + totalPosts);

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put("poster", mAuth.getCurrentUser().getDisplayName());
		posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime());
		posterInfo.put("post", post);
		newPost.updateChildren(posterInfo);
	}

	public static void createFirstPost(String fName, String lName)
	{
		DatabaseReference posts = mDatabase.getReference("Journals");
		DatabaseReference patientsPosts = posts.child(mAuth.getCurrentUser().getUid());
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
		DatabaseReference individualPatient = patients.child(mAuth.getCurrentUser().getUid());
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

	public static void createReader(String fName, String lName, String patientID, String accessCode)
	{
		DatabaseReference patients = mDatabase.getReference("Readers");
		DatabaseReference individualReader = patients.child(mAuth.getCurrentUser().getUid());
		DatabaseReference readerMetaData = individualReader.child("MetaData");
		DatabaseReference patientsToReadFrom = individualReader.child("Patients");

		Map<String, Object> readerInfo = new HashMap<>();
		readerInfo.put("ReadingFrom", "");
		individualReader.updateChildren(readerInfo);

		Map<String, Object> metaDataMap = new HashMap<>();
		metaDataMap.put("firstName", fName);
		metaDataMap.put("lastName", lName);
		readerMetaData.updateChildren(metaDataMap);

		Map<String, Object> patientsReadingMap = new HashMap<>();
		patientsReadingMap.put(patientID, accessCode);
		patientsToReadFrom.updateChildren(patientsReadingMap);
	}

	public static void createFamily(String fName, String lName, String patientID, String familyID)
	{
		DatabaseReference family = mDatabase.getReference("Family");
		DatabaseReference individualFamily = family.child(familyID);
		DatabaseReference familyMetaData = individualFamily.child("MetaData");

		Map<String, Object> patientInfo = new HashMap<>();
		patientInfo.put("PatientID", patientID);
		individualFamily.updateChildren(patientInfo);

		Map<String, Object> metaDataMap = new HashMap<>();
		metaDataMap.put("DOB", 0);
		metaDataMap.put("firstName", fName);
		metaDataMap.put("lastName", lName);
		familyMetaData.updateChildren(metaDataMap);
	}
}
