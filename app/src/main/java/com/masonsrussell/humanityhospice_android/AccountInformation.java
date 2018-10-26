package com.masonsrussell.humanityhospice_android;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class AccountInformation {
	public static String accountType;
	public static String username;
	public static String patientID;
	public static String email;
	public static String profilePictureURL;
	public static Map profilePictures;
	public static String patientName;

	public static void setAccountInfo(String passedAccountType, String passedUsername, String patient, String userEmail, String pictureURl) {
		accountType = passedAccountType;
		username = passedUsername;
		patientID = patient;
		email = userEmail;
		profilePictureURL = pictureURl;
	}

	public static String getDateFromEpochTime(String epochTime) {
		long millis = Double.valueOf(epochTime).longValue();
		Date date = new Date(millis * 1000);
		DateFormat format = new SimpleDateFormat("hh:mm a MM/dd/yyyy", Locale.getDefault());
		String formatted = format.format(date);
		if (formatted.charAt(0) == '0') {
			formatted = formatted.substring(1, formatted.length());
		}
		return formatted;
	}

	public static void UpdateProfilePicture(String pictureURL)
	{
		profilePictureURL = pictureURL;
	}
}
