package com.masonsrussell.humanityhospice_android;

public class AccountInformation
{
	public static String accountType;
	public static String username;
	public static String patientID;

	public static void setAccountInfo(String passedAccountType, String passedUsername, String patient)
	{
		accountType = passedAccountType;
		username = passedUsername;
		patientID = patient;
	}
}
