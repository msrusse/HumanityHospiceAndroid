package com.masonsrussell.humanityhospice_android;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AccountInformation
{
	public static String accountType;
	public static String username;
	public static String patientID;
	public static String email;
	public static String profilePictureURL;

	public static void setAccountInfo(String passedAccountType, String passedUsername, String patient, String userEmail)
	{
		accountType = passedAccountType;
		username = passedUsername;
		patientID = patient;
		email = userEmail;
	}

	public static String getDateFromEpochTime(String epochTime)
	{
		long millis = Long.parseLong(epochTime);
		Date date = new Date(millis);
		DateFormat format = new SimpleDateFormat("hh:mm a MM/dd/yyyy", Locale.getDefault());
		String formatted = format.format(date);
		if (formatted.charAt(0) == '0')
		{
			formatted = formatted.substring(1, formatted.length());
		}
		return formatted;
	}
}
