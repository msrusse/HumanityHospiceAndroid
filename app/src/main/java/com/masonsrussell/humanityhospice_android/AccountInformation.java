package com.masonsrussell.humanityhospice_android;

public class AccountInformation
{
	public static String accountType;
	public static String username;
	public static String patientID;
	public static String email;

	public static void setAccountInfo(String passedAccountType, String passedUsername, String patient, String userEmail)
	{
		accountType = passedAccountType;
		username = passedUsername;
		patientID = patient;
		email = userEmail;
	}
}
