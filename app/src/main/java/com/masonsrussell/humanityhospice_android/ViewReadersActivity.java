package com.masonsrussell.humanityhospice_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewReadersActivity extends AppCompatActivity
{
    ListView patientListView;
    Map<String, String> readerInformation  = new HashMap<>();
    List<String> readerUIDs = new ArrayList<>();
    ArrayList<String> readerNames = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    Bitmap bitmap = null;
    byte[] data = null;
    Uri selectedImage = null;
    TextView navHeaderName, navHeaderEmail;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    ImageView profilePictureView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_readers);
        mDatabase = FirebaseDatabase.getInstance();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        patientListView = findViewById(R.id.patientListView);
        mAuth = FirebaseAuth.getInstance();
        try {
            getReaders();
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "No Current Readers", Toast.LENGTH_SHORT).show();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        setReaderNavMenu();
    }

    private void getReaders()
    {
        DatabaseReference patientsRef = mDatabase.getReference(FirebaseCalls.Patients);
        DatabaseReference individualPatientRef = patientsRef.child(AccountInformation.patientID);
        DatabaseReference readersRef = individualPatientRef.child(FirebaseCalls.Readers);
        try {
            readersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    patientListView.setAdapter(null);
                    HashMap<String, Object> allPatients = (HashMap) dataSnapshot.getValue();
                    try {
                        for (String UID : allPatients.keySet()) {
                            if (allPatients.get(UID).equals(true)) {
                                firebasePatientNamesCall(UID);
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(ViewReadersActivity.this, "No Current Readers", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "No Current Readers", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebasePatientNamesCall(String reader)
    {
        final String readerUID = reader;
        DatabaseReference readersRef = mDatabase.getReference(FirebaseCalls.Readers);
        DatabaseReference individualReadersRef = readersRef.child(reader);
        individualReadersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                readerNames.clear();
                HashMap<String, Object> allReaders = (HashMap) dataSnapshot.getValue();
                readerInformation.put(allReaders.get(FirebaseCalls.FirstName).toString() + " " + allReaders.get(FirebaseCalls.LastName).toString(), readerUID);
                readerUIDs.add(readerUID);
                readerNames.add(allReaders.get(FirebaseCalls.FirstName).toString() + " " + allReaders.get(FirebaseCalls.LastName).toString());
                setListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }


    private void setReaderNavMenu()
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem)
                    {
                        switch(menuItem.toString())
                        {
                            case "My Journal":
                                Intent intent0 = new Intent(getApplicationContext(), JournalActivity.class);
                                startActivity(intent0);
                                finish();
                                break;
                            case "Encouragement Board":
                                Intent intent = new Intent(getApplicationContext(), EncouragementBoardActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case "Photo Album":
                                Intent intent1 = new Intent(getApplicationContext(), PhotoAlbumActivity.class);
                                startActivity(intent1);
                                finish();
                                break;
                            case "Create Family Account":
                                Intent intent2 = new Intent(getApplicationContext(), CreateFamilyAccountActivity.class);
                                startActivity(intent2);
                                finish();
                                break;
                            case "Invite People":
                                Intent intent3 = new Intent(getApplicationContext(), InvitePeopleActivity.class);
                                startActivity(intent3);
                                finish();
                                break;
                            case "Current Readers":
                                mDrawerLayout.closeDrawers();
                                break;
                            case "Sign Out":
                                mAuth.signOut();
                                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(homeIntent);
                                finish();
                                break;
                            case "About Humanity Hospice":
                                Intent intent4 = new Intent(getApplicationContext(), AboutHumanityHospiceActivity.class);
                                startActivity(intent4);
                                finish();
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                navHeaderName = findViewById(R.id.navHeaderName);
                navHeaderEmail = findViewById(R.id.navHeaderEmail);
                navHeaderEmail.setText(AccountInformation.email);
                navHeaderName.setText(AccountInformation.username);
                profilePictureView = findViewById(R.id.userProfilePicImageView);
                if (AccountInformation.profilePictureURL != null)
                {
                    GlideApp.with(this)
                            .load(AccountInformation.profilePictureURL)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profilePictureView);
                }
                LinearLayout profileInfo = findViewById(R.id.profileInfo);
                profileInfo.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        profileImagePicker();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setListView()
    {
        try
        {
            ListAdapter listAdapter = new CustomListAdapter(this, R.layout.patient_listview_adapter, readerNames);
            patientListView.setAdapter(listAdapter);
            patientListView.setOnItemClickListener(listPairedClickItem);
        }
        catch (Exception ex)
        {
            Log.d("ChangePatientActivity", ex.getMessage());
        }
    }

    private class CustomListAdapter extends ArrayAdapter<String>
    {

        private Context mContext;
        private int id;
        private List<String> items;

        public CustomListAdapter(Context context, int textViewResourceId, List<String> patients)
        {
            super(context, textViewResourceId, patients);
            mContext = context;
            id = textViewResourceId;
            items = patients;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent)
        {
            View mView = v;
            if (mView == null)
            {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(id, null);
            }

            TextView postBody = mView.findViewById(R.id.postBodyTextView);

            if (items.get(position) != null)
            {
                postBody.setText(items.get(position));
            }

            return mView;
        }
    }

    private AdapterView.OnItemClickListener listPairedClickItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String uid = readerUIDs.get(arg2);
            getSelectedPatientUID(uid);
        }
    };

    private void getSelectedPatientUID(final String reader)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("You are about to blacklist this reader. By doing so they will no longer be able to read or comment on your profile. Are you sure you want to continue?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseCalls.blacklistReader(reader);
                        removeReaderComments(reader);
                        Toast.makeText(getApplicationContext(), "Reader has been Blacklisted", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void removeReaderComments(final String reader)
    {
        DatabaseReference journals = mDatabase.getReference(FirebaseCalls.Journals);
        final DatabaseReference patientRef = journals.child(AccountInformation.patientID);
        patientRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Map<String, Object>> postsMap = (HashMap) dataSnapshot.getValue();
                HashMap<String, Object> postsMapToPush = new HashMap<>();
                for (String post : postsMap.keySet())
                {
                    if(postsMap.get(post).containsKey(FirebaseCalls.Comments))
                    {
                        HashMap<String, Map<String, Object>> commentsMap = (HashMap) postsMap.get(post).get(FirebaseCalls.Comments);
                        for (String comment : commentsMap.keySet()) {
                            if (commentsMap.get(comment).get(FirebaseCalls.PosterUID).equals(reader)) {
                                patientRef.child(post).child(FirebaseCalls.Comments).child(comment).setValue(null);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void profileImagePicker()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_camera)
                .setTitle("Update Profile Picture")
                .setMessage("Either select an image from the gallery or take a new photo")
                .setPositiveButton("Choose Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chooseImage();
                    }
                })
                .setNegativeButton("Take Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        takePicture();
                    }
                })
                .show();
    }

    private void chooseImage() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);
    }

    private void takePicture()
    {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK && imageReturnedIntent != null){
                    bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data = baos.toByteArray();
                    FirebaseCalls.addProfilePictureFromCamera(data);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    selectedImage = imageReturnedIntent.getData();
                    FirebaseCalls.addProfilePictureFromGallery(selectedImage);
                }
                break;
        }
    }

    private void reloadProfilePicture()
    {
        GlideApp.with(this)
                .load(AccountInformation.profilePictureURL)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePictureView);
    }
}
