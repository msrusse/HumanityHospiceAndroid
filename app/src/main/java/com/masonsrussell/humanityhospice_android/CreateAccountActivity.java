package com.masonsrussell.humanityhospice_android;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CreateAccountActivity extends AppCompatActivity
{
	Button createAccountButton;
	EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, verifyPasswordEditText;
	TextView loginView;
	private FirebaseAuth mAuth;
	String password, email, firstName, lastName, patientAccessCode;
	private FirebaseUser user;
	private static final String TAG = "CreateAccountActivity";
	RadioGroup accountTypeSelector;
	RadioButton selectedAccountType;
	Boolean verifiedAccess = true;
	static HashMap<String, String> patientsList = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		firstNameEditText = findViewById(R.id.firstNameEditText);
		lastNameEditText = findViewById(R.id.lastNameEditText);
		emailEditText = findViewById(R.id.emailEditText);
		accountTypeSelector = findViewById(R.id.accountTypeSelector);
		passwordEditText = findViewById(R.id.passwordEditText);
		verifyPasswordEditText = findViewById(R.id.reenterPasswordEditText);
		loginView = findViewById(R.id.loginView);
		createAccountButton = findViewById(R.id.createAccountButton);
		mAuth = FirebaseAuth.getInstance();

		FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
		DatabaseReference inviteCodesRef = mDatabase.getReference(FirebaseCalls.InviteCodes);
		inviteCodesRef.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				try
				{
					HashMap<String, HashMap<String, Object>> allPatients = (HashMap) dataSnapshot.getValue();
					for (String id : allPatients.keySet())
					{
						patientsList.put(id, allPatients.get(id).get("Patient").toString());
					}

				}
				catch(Exception ex)
				{
					Log.d("CreateAccount", ex.getMessage());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});

		accountTypeSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, final int checkedId)
			{
				selectedAccountType = findViewById(checkedId);
				if (selectedAccountType.getText().equals("Reader"))
				{
					displayDialog();
				} else
				{
					verifiedAccess = true;
				}
			}
		});

		createAccountButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!TextUtils.isEmpty(emailEditText.getText()) && !TextUtils.isEmpty(firstNameEditText.getText()) && !TextUtils.isEmpty(lastNameEditText.getText()) && !TextUtils.isEmpty(passwordEditText.getText()) && !TextUtils.isEmpty(verifyPasswordEditText.getText()) && verifiedAccess)
				{
					email = emailEditText.getText().toString();
					firstName = firstNameEditText.getText().toString();
					lastName = lastNameEditText.getText().toString();
					if (passwordEditText.getText().toString().equals(verifyPasswordEditText.getText().toString()))
					{
						password = passwordEditText.getText().toString();
						displayPrivacyDialog();
					} else
					{
						Toast.makeText(CreateAccountActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
					}

				} else if (!verifiedAccess)
				{
					accountTypeSelector.check(R.id.friendButton);
					Toast.makeText(getApplicationContext(), "Please enter a verified patient code", Toast.LENGTH_SHORT).show();
					displayDialog();
				} else
				{
					Toast.makeText(CreateAccountActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
				}
			}
		});

		loginView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private void displayDialog()
	{
		final Dialog dialog = new Dialog(CreateAccountActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_enter_patient_code);
		Button enter = dialog.findViewById(R.id.enterButton);
		Button cancel = dialog.findViewById(R.id.cancelButton);
		final EditText accessCode = dialog.findViewById(R.id.accessCode);
		dialog.show();

		enter.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!TextUtils.isEmpty(accessCode.getText()))
				{
					String patientCode = accessCode.getText().toString();
					if (checkForPatientCode(patientCode))
					{
						patientAccessCode = patientCode;
						verifiedAccess = true;
						dialog.hide();
					} else
					{
						verifiedAccess = false;
						Toast.makeText(getApplicationContext(), "No matching Patient for entered code", Toast.LENGTH_SHORT).show();
					}
				} else
				{
					verifiedAccess = false;
					Toast.makeText(getApplicationContext(), "Please enter an access code", Toast.LENGTH_SHORT).show();
				}
			}
		});

		cancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				verifiedAccess = false;
				dialog.hide();
			}
		});
	}

	private void displayPrivacyDialog() {
		Dialog dialog = new Dialog(CreateAccountActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_privacy_policy);
		TextView privacyPolicy = dialog.findViewById(R.id.privacyPolicyView);
		privacyPolicy.setText(privacyPolicyString);
		CheckBox agreeBox = dialog.findViewById(R.id.agreeBox);
		dialog.show();

		agreeBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				createAccount();
			}
		});
	}

	private void createAccount()
	{
		mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							Log.d(TAG, "createUserWithEmail:success");
							user = mAuth.getCurrentUser();
							createAccountInDatabase();
						} else
						{
							Log.w(TAG, "createUserWithEmail:failure", task.getException());
							Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private void createAccountInDatabase()
	{
		int accountTypeSelected = accountTypeSelector.getCheckedRadioButtonId();
		selectedAccountType = findViewById(accountTypeSelected);
		if (selectedAccountType.getText().equals(FirebaseCalls.Patient))
		{
			AccountInformation.patientID = mAuth.getCurrentUser().getUid();
			FirebaseCalls.createPatient(generateRandom(), firstName, lastName);
		} else
		{
			if (patientsList.keySet().contains(patientAccessCode)) FirebaseCalls.createReader(firstName, lastName, patientsList.get(patientAccessCode));
		}
		addPersonalData();
	}

	private void addPersonalData()
	{
		UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
				.setDisplayName(firstName + " " + lastName)
				.build();

		user.updateProfile(profileUpdates)
				.addOnCompleteListener(new OnCompleteListener<Void>()
				{
					@Override
					public void onComplete(@NonNull Task<Void> task)
					{
						if (task.isSuccessful())
						{
							Log.d(TAG, "User profile updated.");
							Intent intent = new Intent(getApplicationContext(), CheckAccountTypeActivity.class);
							startActivity(intent);
							finish();
						}
					}
				});
	}

	static String generateString(Random rng, String characters)
	{
		char[] text = new char[6];
		for (int i = 0; i < 6; i++)
		{
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}

	String generateRandom()
	{
		Random random = new Random();
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		return generateString(random, chars);
	}

	static boolean checkForPatientCode(String enteredCode)
	{
		if (patientsList.keySet().contains(enteredCode)) return true;
		return false;
	}

	public static String privacyPolicyString = "Privacy Policy\n" +
			"\n" +
			"                    Effective date: June 25, 2018\n" +
			"                    Humanity Hospice, LLC (\"us\", \"we\", or \"our\") operates the website and the Humanity Connect mobile application (the \"Service\").\n" +
			"                    This page informs you of our policies regarding the collection, use, and disclosure of personal data when you use our Service and the choices you have associated with that data.\n" +
			"                    We use your data to provide and improve the Service. By using the Service, you agree to the collection and use of information in accordance with this policy. Unless otherwise defined in this Privacy Policy, terms used in this Privacy Policy have the same meanings as in our Terms and Conditions.\n" +
			"                    Definitions:\n" +
			"                    Service\n" +
			"                    Service means the website and the Humanity Connect mobile application operated by Humanity Hospice, LLC\n" +
			"                    Personal Data\n" +
			"                    Personal Data means data about a living individual who can be identified from those data (or from those and other information either in our possession or likely to come into our possession).\n" +
			"                    Usage Data\n" +
			"                    Usage Data is data collected automatically either generated by the use of the Service or from the Service infrastructure itself (for example, the duration of a page visit).\n" +
			"                    Cookies\n" +
			"                    Cookies are small pieces of data stored on your device (computer or mobile device).\n" +
			"                    Information Collection and Use\n" +
			"                    We collect several different types of information for various purposes to provide and improve our Service to you.\n" +
			"                    Types of Data Collected\n" +
			"                    Personal Data\n" +
			"                    While using our Service, we may ask you to provide us with certain personally identifiable information that can be used to contact or identify you (\"Personal Data\"). Personally identifiable information may include, but is not limited to:\n" +
			"                    Email address\n" +
			"                    First name and last name\n" +
			"                    Phone number\n" +
			"                    Address, State, Province, ZIP/Postal code, City\n" +
			"                    Cookies and Usage Data\n" +
			"                    We may use your Personal Data to contact you with newsletters, marketing or promotional materials and other information that may be of interest to you. You may opt out of receiving any, or all, of these communications from us by following the unsubscribe link or instructions provided in any email we send.\n" +
			"                    Usage Data\n" +
			"                    We may also collect information that your browser sends whenever you visit our Service or when you access the Service by or through a mobile device (\"Usage Data\").\n" +
			"                    This Usage Data may include information such as your computer's Internet Protocol address (e.g. IP address), browser type, browser version, the pages of our Service that you visit, the time and date of your visit, the time spent on those pages, unique device identifiers and other diagnostic data.\n" +
			"                    When you access the Service by or through a mobile device, this Usage Data may include information such as the type of mobile device you use, your mobile device unique ID, the IP address of your mobile device, your mobile operating system, the type of mobile Internet browser you use, unique device identifiers and other diagnostic data.\n" +
			"                    Location Data\n" +
			"                    We may use and store information about your location if you give us permission to do so (“Location Data”). We use this data to provide features of our Service, to improve and customize our Service.\n" +
			"                    You can enable or disable location services when you use our Service at any time, through your device settings.\n" +
			"                    Tracking Cookies Data\n" +
			"                    We use cookies and similar tracking technologies to track the activity on our Service and hold certain information.\n" +
			"                    Cookies are files with small amount of data which may include an anonymous unique identifier. Cookies are sent to your browser from a website and stored on your device. Tracking technologies also used are beacons, tags, and scripts to collect and track information and to improve and analyze our Service.\n" +
			"                    You can instruct your browser to refuse all cookies or to indicate when a cookie is being sent. However, if you do not accept cookies, you may not be able to use some portions of our Service.\n" +
			"                    Examples of Cookies we use:\n" +
			"                    Session Cookies. We use Session Cookies to operate our Service.\n" +
			"                    Preference Cookies. We use Preference Cookies to remember your preferences and various settings.\n" +
			"                    Security Cookies. We use Security Cookies for security purposes.\n" +
			"                    Use of Data\n" +
			"                    Humanity Hospice, LLC uses the collected data for various purposes:\n" +
			"                    To provide and maintain our Service\n" +
			"                    To notify you about changes to our Service\n" +
			"                    To allow you to participate in interactive features of our Service when you choose to do so\n" +
			"                    To provide customer support\n" +
			"                    To gather analysis or valuable information so that we can improve our Service\n" +
			"                    To monitor the usage of our Service\n" +
			"                    To detect, prevent and address technical issues\n" +
			"                    To provide you with news, special offers and general information about other goods, services and events which we offer that are similar to those that you have already purchased or enquired about unless you have opted not to receive such information\n" +
			"                    Transfer of Data\n" +
			"                    Your information, including Personal Data, may be transferred to — and maintained on — computers located outside of your state, province, country or other governmental jurisdiction where the data protection laws may differ than those from your jurisdiction.\n" +
			"                    If you are located outside and choose to provide information to us, please note that we transfer the data, including Personal Data, to and process it there.\n" +
			"                    Your consent to this Privacy Policy followed by your submission of such information represents your agreement to that transfer.\n" +
			"                    Humanity Hospice, LLC will take all steps reasonably necessary to ensure that your data is treated securely and in accordance with this Privacy Policy and no transfer of your Personal Data will take place to an organization or a country unless there are adequate controls in place including the security of your data and other personal information.\n" +
			"                    Disclosure of Data\n" +
			"                    Business Transaction\n" +
			"                    If Humanity Hospice, LLC is involved in a merger, acquisition or asset sale, your Personal Data may be transferred. We will provide notice before your Personal Data is transferred and becomes subject to a different Privacy Policy.\n" +
			"                    Disclosure for Law Enforcement\n" +
			"                    Under certain circumstances, Humanity Hospice, LLC may be required to disclose your Personal Data if required to do so by law or in response to valid requests by public authorities (e.g. a court or a government agency).\n" +
			"                    Legal Requirements\n" +
			"                    Humanity Hospice, LLC may disclose your Personal Data in the good faith belief that such action is necessary to:\n" +
			"                    To comply with a legal obligation\n" +
			"                    To protect and defend the rights or property of Humanity Hospice, LLC\n" +
			"                    To prevent or investigate possible wrongdoing in connection with the Service\n" +
			"                    To protect the personal safety of users of the Service or the public\n" +
			"                    To protect against legal liability\n" +
			"                    Security of Data\n" +
			"                    The security of your data is important to us, but remember that no method of transmission over the Internet, or method of electronic storage is 100% secure. While we strive to use commercially acceptable means to protect your Personal Data, we cannot guarantee its absolute security.\n" +
			"                    Service Providers\n" +
			"                    We may employ third party companies and individuals to facilitate our Service (\"Service Providers\"), to provide the Service on our behalf, to perform Service-related services or to assist us in analyzing how our Service is used.\n" +
			"                    These third parties have access to your Personal Data only to perform these tasks on our behalf and are obligated not to disclose or use it for any other purpose.\n" +
			"                    Analytics\n" +
			"                    We may use third-party Service Providers to monitor and analyze the use of our Service.\n" +
			"                    Google Analytics\n" +
			"                    Google Analytics is a web analytics service offered by Google that tracks and reports website traffic. Google uses the data collected to track and monitor the use of our Service. This data is shared with other Google services. Google may use the collected data to contextualize and personalize the ads of its own advertising network.\n" +
			"                    For more information on the privacy practices of Google, please visit the Google Privacy Terms web page: https://policies.google.com/privacy?hl=en\n" +
			"                    Links to Other Sites\n" +
			"                    Our Service may contain links to other sites that are not operated by us. If you click on a third party link, you will be directed to that third party's site. We strongly advise you to review the Privacy Policy of every site you visit.\n" +
			"                    We have no control over and assume no responsibility for the content, privacy policies or practices of any third party sites or services.\n" +
			"                    Children's Privacy\n" +
			"                    Our Service does not address anyone under the age of 18 (\"Children\").\n" +
			"                    We do not knowingly collect personally identifiable information from anyone under the age of 18. If you are a parent or guardian and you are aware that your child has provided us with Personal Data, please contact us. If we become aware that we have collected Personal Data from children without verification of parental consent, we take steps to remove that information from our servers.\n" +
			"                    Changes to This Privacy Policy\n" +
			"                    We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page.\n" +
			"                    We will let you know via email and/or a prominent notice on our Service, prior to the change becoming effective and update the \"effective date\" at the top of this Privacy Policy.\n" +
			"                    You are advised to review this Privacy Policy periodically for any changes. Changes to this Privacy Policy are effective when they are posted on this page.\n" +
			"                    Contact Us\n" +
			"                    If you have any questions about this Privacy Policy, please contact us:\n" +
			"                    By email: Info@humanityhospice.com\n" +
			"                    By visiting this page on our website: Www.humanityhospice.com\n" +
			"                    By phone number: 405.418.2530";
}
