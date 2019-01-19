package com.masonsrussell.humanityhospice_android;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JournalActivity extends AppCompatActivity {
    TextView navHeaderName, navHeaderEmail;
    Button writePostButton, callNurseButton;
    RecyclerView postsListView;
    Bitmap bitmap = null;
    byte[] data = null;
    Uri selectedImage = null;
    JournalListAdapter mAdapter;
    public static List<Map<String, Object>> posts = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private FirebaseDatabase mDatabase;
    ImageView userProfilePictureView;

    private static final String TAG = "JournalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onLoad();
    }

    private void getProfilePictures() {
        DatabaseReference profilePictures = mDatabase.getReference(FirebaseCalls.ProfilePictures);
        profilePictures.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> profilePictures = (HashMap) dataSnapshot.getValue();
                AccountInformation.profilePictures = profilePictures;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void onLoad() {
        if (AccountInformation.accountType.equals("Reader")) {
            setContentView(R.layout.activity_reader_journal);
            mDrawerLayout = findViewById(R.id.drawer_layout);
            //Toast.makeText(getApplicationContext(), AccountInformation.patientID, Toast.LENGTH_LONG).show();
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();
            postsListView = findViewById(R.id.postsListView);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            setReaderNavMenu();
            mDatabase.getReference(FirebaseCalls.Readers)
                    .child(mAuth.getUid())
                    .child(FirebaseCalls.PatientsList)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            checkForBlackList();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            setContentView(R.layout.activity_journal);
            mDrawerLayout = findViewById(R.id.drawer_layout);
            //Toast.makeText(getApplicationContext(), AccountInformation.patientID, Toast.LENGTH_LONG).show();
            writePostButton = findViewById(R.id.writePostButton);
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();
            postsListView = findViewById(R.id.postsListView);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            setFamilyPatientNavMenu();
            writePostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), WritePostActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        if (AccountInformation.patientID != null) {
            getProfilePictures();
            getJournalPosts();
        } else {
            findViewById(R.id.noPatientTextView).setVisibility(View.VISIBLE);
        }
    }

    private void setFamilyPatientNavMenu() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.toString()) {
                            case "My Journal":
                                mDrawerLayout.closeDrawers();
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
                                Intent intent5 = new Intent(getApplicationContext(), ViewReadersActivity.class);
                                startActivity(intent5);
                                finish();
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

    private void setReaderNavMenu() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.toString()) {
                            case "Journal":
                                mDrawerLayout.closeDrawers();
                                break;
                            case "Encouragement Board":
                                checkForBlackList();
                                Intent intent = new Intent(getApplicationContext(), EncouragementBoardActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case "Photo Album":
                                checkForBlackList();
                                Intent intent1 = new Intent(getApplicationContext(), PhotoAlbumActivity.class);
                                startActivity(intent1);
                                finish();
                                break;
                            case "Sign Out":
                                mAuth.signOut();
                                Intent intent0 = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent0);
                                finish();
                                break;
                            case "About Humanity Hospice":
                                Intent intent2 = new Intent(getApplicationContext(), AboutHumanityHospiceActivity.class);
                                startActivity(intent2);
                                finish();
                                break;
                            case "Add Patient":
                                Intent intent3 = new Intent(getApplicationContext(), AddPatientActivity.class);
                                startActivity(intent3);
                                finish();
                                break;
                            case "Change Patient":
                                Intent intent4 = new Intent(getApplicationContext(), ChangePatientActivity.class);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                navHeaderName = findViewById(R.id.navHeaderName);
                navHeaderEmail = findViewById(R.id.navHeaderEmail);
                navHeaderEmail.setText(AccountInformation.email);
                navHeaderName.setText(AccountInformation.username);
                userProfilePictureView = findViewById(R.id.userProfilePicImageView);
                if (AccountInformation.profilePictureURL != null) {
                    GlideApp.with(this)
                            .load(AccountInformation.profilePictureURL)
                            .apply(RequestOptions.circleCropTransform())
                            .into(userProfilePictureView);
                }
                LinearLayout profileInfo = findViewById(R.id.profileInfo);
                if (!AccountInformation.accountType.equals("Reader")) {
                    callNurseButton = findViewById(R.id.call_nurse_button);
                    callNurseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (AccountInformation.accountType == FirebaseCalls.Patient) {
                                FirebaseCalls.getNurseDetails(new com.masonsrussell.humanityhospice_android.CompletionHandler() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "onSuccess: Got nurse details");
                                        Intent intent = new Intent(getApplicationContext(), VideoChat.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFail(String reason) {
                                        Log.d(TAG, "onFail: " + reason);
                                    }
                                });
                            }
                        }
                    });
                }
                profileInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImagePicker();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void gotToVideoChat() {
        Intent intent = new Intent(this, VideoChat.class);
        startActivity(intent);
    }

    private void getJournalPosts() {
        DatabaseReference journalPostsRef = mDatabase.getReference(FirebaseCalls.Journals);
        journalPostsRef.child(AccountInformation.patientID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    posts.clear();
                    dataSnapshot.getValue();
                    Map<Object, Map> postsMap = (HashMap) dataSnapshot.getValue();
                    for (Object post : postsMap.keySet()) {
                        Map<String, Object> addPost = new HashMap<>();
                        if (postsMap.get(post).containsKey(FirebaseCalls.PostImageURL)) {
                            addPost.put(FirebaseCalls.PostImageURL, postsMap.get(post).get(FirebaseCalls.PostImageURL).toString());
                        }
                        if (postsMap.get(post).containsKey(FirebaseCalls.Comments)) {
                            addPost.put(FirebaseCalls.Comments, postsMap.get(post).get(FirebaseCalls.Comments));
                        }
                        addPost.put("postID", post);
                        addPost.put(FirebaseCalls.Post, postsMap.get(post).get(FirebaseCalls.Post).toString());
                        addPost.put(FirebaseCalls.PosterName, postsMap.get(post).get(FirebaseCalls.PatientName).toString());
                        addPost.put(FirebaseCalls.Timestamp, postsMap.get(post).get(FirebaseCalls.Timestamp));
                        addPost.put(FirebaseCalls.PosterUID, postsMap.get(post).get(FirebaseCalls.PosterUID));
                        posts.add(addPost);
                    }
                    setListView();
                    if (AccountInformation.accountType.equals("Reader")) checkForBlackList();
                } catch (Exception ex) {
                    Log.d("JournalActivity", ex.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setListView() {
        try {
            Collections.sort(posts, new MapComparator());
            Collections.reverse(posts);
            mAdapter = new JournalListAdapter(this, posts);
            postsListView.setAdapter(mAdapter);
            postsListView.setLayoutManager(new LinearLayoutManager(this));
            postsListView.getRecycledViewPool().setMaxRecycledViews(0, 0);
            postsListView.setNestedScrollingEnabled(true);
            postsListView.smoothScrollBy(1, 1);
            postsListView.addOnItemTouchListener(
                    new RecyclerItemClickListener(JournalActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Intent intent = new Intent(getApplicationContext(), JournalCommentActivity.class);
                            if (posts.get(position).containsKey(FirebaseCalls.PostImageURL)) {
                                PostInformation.setPostInfo(posts.get(position).get("postID").toString(), posts.get(position).get(FirebaseCalls.PostImageURL).toString(), posts.get(position).get(FirebaseCalls.PosterUID).toString(), posts.get(position).get(FirebaseCalls.PosterName).toString(), posts.get(position).get(FirebaseCalls.Post).toString(), posts.get(position).get(FirebaseCalls.Timestamp).toString());
                            }
                            else {
                                PostInformation.setPostInfo(posts.get(position).get("postID").toString(), null, posts.get(position).get(FirebaseCalls.PosterUID).toString(), posts.get(position).get(FirebaseCalls.PosterName).toString(), posts.get(position).get(FirebaseCalls.Post).toString(), posts.get(position).get(FirebaseCalls.Timestamp).toString());
                            }
                            intent.putExtra("position", position);
                            startActivity(intent);
                        }
                    })
            );
        } catch (Exception ex) {
            Log.d("JournalActivity", ex.getMessage());
        }
    }

    public void onBackPressed() {
        finish();
    }

    class MapComparator implements Comparator<Map<String, Object>> {
        private final String key;

        private MapComparator() {
            this.key = FirebaseCalls.Timestamp;
        }

        public int compare(Map<String, Object> first,
                           Map<String, Object> second) {
            long firstValue, secondValue;
            try {
                firstValue = (long) first.get(key);
            } catch (Exception e) {
                firstValue = Math.round((double) first.get(key));
            }
            try {
                secondValue = (long) second.get(key);
            } catch (Exception ex) {
                secondValue = Math.round((double) second.get(key));
            }

            return Long.compare(firstValue, secondValue);
        }
    }

    public void profileImagePicker() {
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
        startActivityForResult(pickPhoto, 1);
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK && imageReturnedIntent != null) {
                    bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data = baos.toByteArray();
                    try {
                        FirebaseCalls.addProfilePictureFromCamera(data);
                        reloadProfilePicture();
                    } catch (Exception ex) {
                        Log.d("addProfilePicture", ex.getMessage());
                    }
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    try {
                        FirebaseCalls.addProfilePictureFromGallery(selectedImage);
                        reloadProfilePicture();
                    } catch (Exception ex) {
                        Log.d("addProfilePicture", ex.getMessage());
                    }
                }
                break;
        }
    }

    private void reloadProfilePicture()
    {
        GlideApp.with(this)
                .load(AccountInformation.profilePictureURL)
                .apply(RequestOptions.circleCropTransform())
                .into(userProfilePictureView);
    }

    private void checkForBlackList() {
        mDatabase.getReference(FirebaseCalls.Readers)
                .child(mAuth.getUid())
                .child(FirebaseCalls.PatientsList)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<Object, Boolean> patients = (HashMap) dataSnapshot.getValue();
                        if (patients.containsKey(AccountInformation.patientID)) {
                            if (!patients.get(AccountInformation.patientID)) {
                                AccountInformation.patientID = null;
                                Intent restartJournal = new Intent(getApplicationContext(), JournalActivity.class);
                                startActivity(restartJournal);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
