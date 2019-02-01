package com.masonsrussell.humanityhospice_android;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.BooleanNode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.support.constraint.Constraints.TAG;

public class FirebaseCalls
{
	private static final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
	private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
	private static StorageReference storageReference;
	private static FirebaseStorage storage;
	public static final String Journals = "Journals";
	public static final String PosterUID = "PosterUID";
	public static final String Timestamp = "Timestamp";
	public static final String PosterName = "PosterName";
	public static final String Post = "Post";
	public static final String PostImageURL = "PostImageURL";
	public static final String PatientName = "PatientName";
	public static final String Message = "Message";
	public static final String Comments = "Comments";
	public static final String EncouragementBoards = "EncouragementBoards";
	public static final String Patients = "Patients";
	public static final String InviteCodes = "InviteCodes";
	public static final String Patient = "Patient";
	public static final String FirstName = "FirstName";
	public static final String LastName = "LastName";
	public static final String FullName = "FullName";
	public static final String InviteCode = "InviteCode";
	public static final String PatientUID = "PatientUID";
	public static final String Readers = "Readers";
	public static final String PatientsList = "PatientList";
	public static final String ReadingFrom = "ReadingFrom";
	public static final String Family = "Family";
	public static final String PhotoAlbum = "PhotoAlbum";
	public static final String Comment = "Comment";
	public static final String ProfilePictures = "ProfilePictures";
	public static final String URL = "URL";
	static String ImageName = "imageName";

	public static void createJournalPostWithoutPhoto(String post)
	{
		DatabaseReference posts = mDatabase.getReference(Journals);
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put(PosterUID, AccountInformation.patientID);
		posterInfo.put(PatientName, AccountInformation.username);
		posterInfo.put(Timestamp, Calendar.getInstance().getTime().getTime() / 1000);
		posterInfo.put(Post, post);
		newPost.updateChildren(posterInfo);
	}

	public static void createJournalPostWithPhoto(String post, Object imageURl, String imageName)
	{
		DatabaseReference posts = mDatabase.getReference(Journals);
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put(PosterUID, AccountInformation.patientID);
		posterInfo.put(PatientName, AccountInformation.username);
		posterInfo.put(Timestamp, Calendar.getInstance().getTime().getTime() / 1000);
		posterInfo.put(FirebaseCalls.PostImageURL, imageURl);
		posterInfo.put(Post, post);
		newPost.updateChildren(posterInfo);
		createAlbumPost(imageURl, imageName);
	}

	public static void createEncouragementPost(String post)
	{
		DatabaseReference posts = mDatabase.getReference(EncouragementBoards);
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put(PosterName, AccountInformation.username);
		posterInfo.put(Timestamp, Calendar.getInstance().getTime().getTime() / 1000);
		posterInfo.put(Message, post);
		posterInfo.put(PosterUID, mAuth.getCurrentUser().getUid());
		newPost.updateChildren(posterInfo);
	}

	private static void createFirstPost(String fName, String lName)
	{
		DatabaseReference posts = mDatabase.getReference(Journals);
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put(PosterUID, AccountInformation.patientID);
		posterInfo.put(PatientName, fName + " " + lName);
		posterInfo.put(Timestamp, Calendar.getInstance().getTime().getTime() / 1000);
		posterInfo.put(Post, "Joined Humanity Hospice");
		newPost.updateChildren(posterInfo);
	}

	public static void createPatient(String inviteCode, String fName, String lName)
	{
		DatabaseReference patients = mDatabase.getReference(Patients);
		DatabaseReference individualPatient = patients.child(AccountInformation.patientID);
		DatabaseReference inviteCodesRef = mDatabase.getReference(InviteCodes);

		Map<String, Object> inviteCodeMap = new HashMap<>();
		inviteCodeMap.put(FirebaseCalls.Patient, AccountInformation.patientID);
		inviteCodesRef.child(inviteCode).updateChildren(inviteCodeMap);

		Map<String, Object> patientInfo = new HashMap<>();
		patientInfo.put(InviteCode, inviteCode);
		individualPatient.updateChildren(patientInfo);

		Map<String, Object> metaDataMap = new HashMap<>();
		metaDataMap.put(FirstName, fName);
		metaDataMap.put(LastName, lName);
		metaDataMap.put(FullName, fName + " " + lName);
		individualPatient.updateChildren(metaDataMap);
		createFirstPost(fName, lName);
	}

	public static void createReader(String fName, String lName, String patientID)
	{
		DatabaseReference patients = mDatabase.getReference(Readers);
		DatabaseReference individualReader = patients.child(mAuth.getCurrentUser().getUid());
		DatabaseReference patientsToReadFrom = individualReader.child(PatientsList);

		Map<String, Object> readerInfo = new HashMap<>();
		readerInfo.put(ReadingFrom, patientID);
		individualReader.updateChildren(readerInfo);

		Map<String, Object> metaDataMap = new HashMap<>();
		metaDataMap.put(FirstName, fName);
		metaDataMap.put(LastName, lName);
		metaDataMap.put(FullName, fName + " " + lName);
		individualReader.updateChildren(metaDataMap);

		Map<String, Object> patientsReadingMap = new HashMap<>();
		patientsReadingMap.put(patientID, true);
		patientsToReadFrom.updateChildren(patientsReadingMap);

		Map<String, Object> patientReaderInstance = new HashMap<>();
		patientReaderInstance.put(mAuth.getCurrentUser().getUid(), true);
		DatabaseReference patientsRef = mDatabase.getReference(Patients);
		DatabaseReference patientReadersRef = patientsRef.child(patientID).child(Readers);
		patientReadersRef.updateChildren(patientReaderInstance);
	}

	public static void createFamily(String fName, String lName, String familyID)
	{
		DatabaseReference family = mDatabase.getReference(Family);
		DatabaseReference individualFamily = family.child(familyID);

		Map<String, Object> patientInfo = new HashMap<>();
		patientInfo.put(PatientUID, AccountInformation.patientID);
		individualFamily.updateChildren(patientInfo);

		Map<String, Object> metaDataMap = new HashMap<>();
		metaDataMap.put(FirstName, fName);
		metaDataMap.put(LastName, lName);
		metaDataMap.put(FullName, fName + " " + lName);
		individualFamily.updateChildren(metaDataMap);
	}

	public static void addAdditionalPatientForReader(String patientID)
	{
		DatabaseReference readers = mDatabase.getReference(Readers);
		DatabaseReference individualReader = readers.child(mAuth.getCurrentUser().getUid());
		DatabaseReference readersPatients = individualReader.child(PatientsList);
		DatabaseReference readingFromRef = individualReader.child(ReadingFrom);

		Map<String, Object> readersPatientsMap = new HashMap<>();
		readersPatientsMap.put(patientID, true);
		readersPatients.updateChildren(readersPatientsMap);

		readingFromRef.setValue(patientID);

		DatabaseReference patients = mDatabase.getReference(Patients);
		DatabaseReference individualPatient = patients.child(patientID);
		DatabaseReference readersList = individualPatient.child(Readers);
		Map<String, Object> patientReadersMap = new HashMap<>();
		patientReadersMap.put(mAuth.getCurrentUser().getUid(), true);
		readersList.updateChildren(patientReadersMap);
	}

	public static void updatePatientReadingFrom(String patientID)
	{
		DatabaseReference readers = mDatabase.getReference(Readers);
		DatabaseReference individualReader = readers.child(mAuth.getCurrentUser().getUid());
		DatabaseReference readingFromRef = individualReader.child(ReadingFrom);
		readingFromRef.setValue(patientID);
	}

	public static void addAlbumPictures(Uri file, final String post, final String activity)
	{
		storage = FirebaseStorage.getInstance();
		storageReference = storage.getReference();

		final StorageReference albumImageRef = storageReference.child(FirebaseCalls.PhotoAlbum + "/" + AccountInformation.patientID + "/post-" + Calendar.getInstance().getTime().getTime() / 1000);
		albumImageRef.putFile(file)
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
				{
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
					{
						albumImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
						{
							@Override
							public void onSuccess(Uri uri)
							{
								if (activity.equals(Journals))
								{
									createJournalPostWithPhoto(post, uri.toString(), albumImageRef.getName());
								} else
								{
									createAlbumPost(uri.toString(), albumImageRef.getName());
								}
							}
						});
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d("addAlbumPicture", e.getMessage());
					}
				});
	}

	private static void createAlbumPost(Object imageURL, String imageName)
	{
		DatabaseReference posts = mDatabase.getReference(PhotoAlbum);
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put(Timestamp, Calendar.getInstance().getTime().getTime() / 1000);
		posterInfo.put(URL, imageURL);
		posterInfo.put(ImageName, imageName);
		newPost.updateChildren(posterInfo);
	}

	public static void createPhotoRefFromCamera(byte[] data, final String post, final String activity)
	{
		storage = FirebaseStorage.getInstance();
		storageReference = storage.getReference();
		final StorageReference albumImageRef = storageReference.child(activity + "/" + AccountInformation.patientID + "/post-" + Calendar.getInstance().getTime().getTime() / 1000);
		albumImageRef.putBytes(data)
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
				{
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
					{
						albumImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
						{
							@Override
							public void onSuccess(Uri uri)
							{
								if (activity.equals(Journals))
								{
									createJournalPostWithPhoto(post, uri.toString(), albumImageRef.getName());
								} else
								{
									createAlbumPost(uri.toString(), albumImageRef.getName());
								}
							}
						});
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d("addAlbumPicture", e.getMessage());
					}
				});
	}

	public static void addCommentToJournalPost(String commentToAdd, String postID)
	{
		DatabaseReference posts = mDatabase.getReference(Journals);
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference currentPost = patientsPosts.child(postID);
		DatabaseReference commentsRef = currentPost.child(FirebaseCalls.Comments);
		DatabaseReference newComment = commentsRef.push();

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put(PosterName, AccountInformation.username);
		posterInfo.put(Timestamp, Calendar.getInstance().getTime().getTime() / 1000);
		posterInfo.put(Comment, commentToAdd);
		posterInfo.put(PosterUID, mAuth.getCurrentUser().getUid());
		newComment.updateChildren(posterInfo);
	}

	public static void addProfilePictureFromCamera(byte[] data)
	{
		storage = FirebaseStorage.getInstance();
		storageReference = storage.getReference();
		final StorageReference profilePictureRef = storageReference.child("ProfilePictures/" + mAuth.getCurrentUser().getUid() + "/ProfilePicture");
		profilePictureRef.putBytes(data)
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
				{
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
					{
						profilePictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
						{
							@Override
							public void onSuccess(Uri uri)
							{
								UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
										.setPhotoUri(uri)
										.build();
								AccountInformation.UpdateProfilePicture(uri.toString());

								mAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
									@Override
									public void onComplete(@NonNull Task<Void> task)
									{
										Log.d("addProfilePicFromCam", "User Profile Updated");
										addProfilePictureToDatabase();
									}
								});

							}
						});
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d("addAlbumPicture", e.getMessage());
					}
				});

	}

	public static void addProfilePictureFromGallery(Uri file)
	{
		storage = FirebaseStorage.getInstance();
		storageReference = storage.getReference();
		final StorageReference profilePictureRef = storageReference.child("ProfilePictures/" + mAuth.getCurrentUser().getUid() + "/ProfilePicture");
		profilePictureRef.putFile(file)
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
				{
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
					{
						profilePictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
						{
							@Override
							public void onSuccess(Uri uri)
							{
								UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
										.setPhotoUri(uri)
										.build();
								AccountInformation.UpdateProfilePicture(uri.toString());
								mAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
									@Override
									public void onComplete(@NonNull Task<Void> task)
									{
										Log.d("addProfilePicFromGal", "User Profile Updated");
										addProfilePictureToDatabase();
									}
								});

							}
						});
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d("addAlbumPicture", e.getMessage());
					}
				});

	}

	private static void addProfilePictureToDatabase()
    {
        if (mAuth.getCurrentUser().getPhotoUrl() != null) {
            DatabaseReference profilePictures = mDatabase.getReference(ProfilePictures);
            Map<String, Object> profilePic = new HashMap<>();
            profilePic.put(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getPhotoUrl().toString());
            profilePictures.updateChildren(profilePic);
            AccountInformation.profilePictureURL = mAuth.getCurrentUser().getPhotoUrl().toString();
        }
    }

    public static void blacklistReader(String readerUID)
	{
		DatabaseReference patients = mDatabase.getReference(Patients);
		DatabaseReference individualPatient = patients.child(AccountInformation.patientID);
		DatabaseReference patientsToReadFrom = individualPatient.child(Readers);

		Map<String, Object> patientsReadingMap = new HashMap<>();
		patientsReadingMap.put(readerUID, false);
		patientsToReadFrom.updateChildren(patientsReadingMap);

		DatabaseReference readers = mDatabase.getReference(Readers);
		DatabaseReference individualReader = readers.child(readerUID);
		DatabaseReference readerPatients = individualReader.child(PatientsList);

		Map<String, Object> blacklistInfo = new HashMap<>();
		blacklistInfo.put(AccountInformation.patientID, false);
		readerPatients.updateChildren(blacklistInfo);
	}

	public static void pushBlackList(HashMap<String, Object> blackListMap)
	{
		DatabaseReference journals = mDatabase.getReference(Journals);
		DatabaseReference patientJournal = journals.child(AccountInformation.patientID);
		patientJournal.updateChildren(blackListMap);
	}


	public static void getNurseDetails(final com.masonsrussell.humanityhospice_android.CompletionHandler completionHandler) {

		final String patientID = mAuth.getCurrentUser().getUid();
		DatabaseReference reference = mDatabase.getReference(Patients);
		DatabaseReference patientRef = reference.child(patientID);

		patientRef.child("PrimaryNurseID").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				final String nurseID = dataSnapshot.getValue(String.class);
				Log.d(TAG, "onDataChange: NurseID - " + nurseID);

				if (nurseID == null) {
				    completionHandler.onFail("No Assigned Nurse");
                } else {
                    DatabaseReference nursesRef = mDatabase.getReference("Staff");
                    nursesRef.child(nurseID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        	try {
								Nurse myNurse = dataSnapshot.getValue(Nurse.class);
								String team = myNurse.getTeam();

								if (myNurse.getIsOnCall()) {
									// Send the APN
									sendAPNRequest(patientID, nurseID);
									completionHandler.onSuccess();
								} else {
									// Request on call nurse
									requestOnCallNurse(patientID, team, completionHandler);
								}

							} catch (Exception e) {
								Log.d(TAG, "onDataChange: " + e.getLocalizedMessage());
							}

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: ", databaseError.toException());
                            completionHandler.onFail(databaseError.getMessage());
                        }
                    });
                }
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.e(TAG, "onCancelled: ", databaseError.toException());
				completionHandler.onFail(databaseError.getMessage());
			}
		});

	}

	private static void sendAPNRequest(String patientID, String nurseID) {
		String patientName = mAuth.getCurrentUser().getDisplayName();
		String name = AccountInformation.username;

		DatabaseReference calls = mDatabase.getReference("NotificationCenter/Calls");
		Map<String, String> data = new HashMap<>();

		data.put("nurseID", nurseID);
		data.put("patientID", patientID);

		if (patientName.isEmpty()) {
			data.put("greeting", name);
		} else {
			data.put("greeting", patientName);
		}

		calls.push().setValue(data);

	}

	private static void requestOnCallNurse(final String patientID, final String team, final com.masonsrussell.humanityhospice_android.CompletionHandler completion) {
		DatabaseReference nursesRef = mDatabase.getReference("Staff");
		nursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

				List<Nurse> nurses = new ArrayList<>();
				for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
					Nurse nurse = snapshot.getValue(Nurse.class);

					if (nurse.getIsOnCall() && nurse.getTeam() == team) {
						nurses.add(nurse);
					}
				}

				int rnd = new Random().nextInt(nurses.size());
				Nurse chosen = nurses.get(rnd);

				sendAPNRequest(patientID, chosen.getId());

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				completion.onFail(databaseError.getMessage());
			}
		});
	}

}
