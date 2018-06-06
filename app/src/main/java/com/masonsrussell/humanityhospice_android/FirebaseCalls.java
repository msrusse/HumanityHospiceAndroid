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
import com.google.firebase.auth.UserProfileChangeRequest;
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

	public static void createJournalPostWithoutPhoto(String post)
	{
		DatabaseReference posts = mDatabase.getReference("Journals");
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		// TODO: Add profilePictureURL field in post
		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put("poster", AccountInformation.username);
		posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime() / 1000);
		posterInfo.put("post", post);
		newPost.updateChildren(posterInfo);
	}

	public static void createJournalPostWithPhoto(String post, Object imageURl)
	{
		DatabaseReference posts = mDatabase.getReference("Journals");
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		// TODO: Add profilePictureURL field in post
		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put("poster", AccountInformation.username);
		posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime() / 1000);
		posterInfo.put("postImageURL", imageURl);
		posterInfo.put("post", post);
		newPost.updateChildren(posterInfo);
		createAlbumPost(post, imageURl);
	}

	public static void createEncouragementPost(String post)
	{
		DatabaseReference posts = mDatabase.getReference("EncouragementBoard");
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		// TODO: Add profilePictureURL field in post
		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put("poster", AccountInformation.username);
		posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime() / 1000);
		posterInfo.put("post", post);
		posterInfo.put("posterID", mAuth.getCurrentUser().getUid());
		newPost.updateChildren(posterInfo);
	}

	private static void createFirstPost(String fName, String lName)
	{
		DatabaseReference posts = mDatabase.getReference("Journals");
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		// TODO: Add profilePictureURL field in post
		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put("poster", fName + " " + lName);
		posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime() / 1000);
		posterInfo.put("post", "Joined Humanity Hospice");
		newPost.updateChildren(posterInfo);
	}

	public static void createPatient(String inviteCode, String fName, String lName)
	{
		DatabaseReference patients = mDatabase.getReference("Patients");
		DatabaseReference individualPatient = patients.child(AccountInformation.patientID);
		DatabaseReference patientMetaData = individualPatient.child("MetaData");

		// TODO: Add profilePictureURL field in post
		Map<String, Object> patientInfo = new HashMap<>();
		patientInfo.put("InviteCode", inviteCode);
		individualPatient.updateChildren(patientInfo);

		Map<String, Object> metaDataMap = new HashMap<>();
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

		// TODO: Add profilePictureURL field in post
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

		// TODO: Add profilePictureURL field in post
		Map<String, Object> patientInfo = new HashMap<>();
		patientInfo.put("PatientID", AccountInformation.patientID);
		individualFamily.updateChildren(patientInfo);

		Map<String, Object> metaDataMap = new HashMap<>();
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

	public static void addAlbumPictures(Uri file, final String post, final String activity)
	{
		storage = FirebaseStorage.getInstance();
		storageReference = storage.getReference();

		final StorageReference albumImageRef = storageReference.child(activity + "/" + AccountInformation.patientID + "/post-" + Calendar.getInstance().getTime().getTime() / 1000);
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
								if (activity.equals("Journals"))
								{
									createJournalPostWithPhoto(post, uri.toString());
								} else
								{
									createAlbumPost(post, uri.toString());
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

	private static void createAlbumPost(String post, Object imageURL)
	{
		DatabaseReference posts = mDatabase.getReference("PhotoAlbum");
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference newPost = patientsPosts.push();

		if (post == null)
		{
			Map<String, Object> posterInfo = new HashMap<>();
			posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime() / 1000);
			posterInfo.put("postImageURL", imageURL);
			newPost.updateChildren(posterInfo);
		} else
		{
			Map<String, Object> posterInfo = new HashMap<>();
			posterInfo.put("caption", post);
			posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime() / 1000);
			posterInfo.put("postImageURL", imageURL);
			newPost.updateChildren(posterInfo);
		}
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
								if (activity.equals("Journal"))
								{
									createJournalPostWithPhoto(post, uri.toString());
								} else
								{
									createAlbumPost(post, uri.toString());
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
		DatabaseReference posts = mDatabase.getReference("Journals");
		DatabaseReference patientsPosts = posts.child(AccountInformation.patientID);
		DatabaseReference currentPost = patientsPosts.child(postID);
		DatabaseReference commentsRef = currentPost.child("comments");
		DatabaseReference newComment = commentsRef.push();

		Map<String, Object> posterInfo = new HashMap<>();
		posterInfo.put("poster", AccountInformation.username);
		posterInfo.put("timestamp", Calendar.getInstance().getTime().getTime() / 1000);
		posterInfo.put("post", commentToAdd);
		posterInfo.put("posterID", mAuth.getCurrentUser().getUid());
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
								AccountInformation.profilePictureURL = uri.toString();
								UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
										.setPhotoUri(uri)
										.build();

								mAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
									@Override
									public void onComplete(@NonNull Task<Void> task)
									{
										Log.d("addProfilePicFromCam", "User Profile Updated");
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
								AccountInformation.profilePictureURL = uri.toString();
								UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
										.setPhotoUri(uri)
										.build();

								mAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
									@Override
									public void onComplete(@NonNull Task<Void> task)
									{
										Log.d("addProfilePicFromGal", "User Profile Updated");
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
}
