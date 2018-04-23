package com.masonsrussell.humanityhospice_android;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseCalls
{
	private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
	private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
	static ArrayList<String> patientIds = new ArrayList<>();
	static HashMap<String, String> patientInviteCodes = new HashMap<>();

	public void createPost(FirebaseAuth mAuth, String post)
	{

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
		metaDataMap.put("lName", lName);
		patientMetaData.updateChildren(metaDataMap);
	}

	public static HashMap<String, String> getPatientInviteCodes()
	{
		DatabaseReference patients = mDatabase.getReference("Patients");
		patients.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				HashMap<String, Object> allPatients = (HashMap)dataSnapshot.getValue();
				patientIds.addAll(allPatients.keySet());
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
		for(String id : patientIds)
		{
			final String finalId = id;
			patients.child(id).child("InviteCode").addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot)
				{
					patientInviteCodes.put(finalId, dataSnapshot.getValue().toString());
				}

				@Override
				public void onCancelled(DatabaseError databaseError)
				{

				}
			});
		}
		return patientInviteCodes;
	}
}
