package com.example.instagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ViewProfileActivity extends AppCompatActivity {

    Button buttonFollowUnfollow;
    ParseUser currentUser;
    ParseObject currentUserSocial;

    public void saveParseObjectInBackground(ParseObject parseObject, String message) {
        // Saves the given object and updates the button text

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException exception) {
                if (exception == null) {
                    buttonFollowUnfollow.setText(message);
                } else {
                    Toast.makeText(ViewProfileActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("REQ ERR", exception.toString());
                }
            }
        });
    }

    public void updateProfile(View view) {
        Toast.makeText(this, "Will be available in future updates", Toast.LENGTH_SHORT).show();
    }

    public void followUnfollow(View view) {
        // Updates database according to follow unfollow requests

        if (buttonFollowUnfollow.getText().equals("follow")) {
            // send follow request algo

            currentUserSocial.add("pendingRequests", ParseUser.getCurrentUser().getUsername());
            saveParseObjectInBackground(currentUserSocial, "requested");

        } else if (buttonFollowUnfollow.getText().equals("following")) {
            // unfollow algo

        } else {
            // take back request algo
            currentUserSocial.getList("pendingRequests").remove(ParseUser.getCurrentUser().getUsername());
            List<String> tempList = currentUserSocial.getList("pendingRequests");
            currentUserSocial.remove("pendingRequests");
            currentUserSocial.put("pendingRequests", tempList);

            saveParseObjectInBackground(currentUserSocial, "follow");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(ViewProfileActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        EditText editTextProfileUsername = findViewById(R.id.editTextProfileUsername);
        Button buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonFollowUnfollow = findViewById(R.id.buttonFollowUnfollow);

        // Action bar setup
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_with_back);
        AppCompatImageView appCompatImageView = findViewById(R.id.imageViewBack);
        appCompatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfileActivity.this, FeedActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Fetching data
        Intent intent = getIntent();
        boolean isUser = intent.getBooleanExtra("flag", false);
        String userName = intent.getStringExtra("username");

        // Title based on owner or viewer of account
        AppCompatTextView appCompatTextView = findViewById(R.id.title1);
        String temp;
        if (isUser) {
            buttonEditProfile.setVisibility(View.VISIBLE);
            buttonFollowUnfollow.setVisibility(View.INVISIBLE);
            temp = "Your Profile";
        } else {
            buttonEditProfile.setVisibility(View.INVISIBLE);
            buttonFollowUnfollow.setVisibility(View.VISIBLE);
            temp = "User Profile";
        }
        appCompatTextView.setText(temp);

        // Getting the user info
        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereEqualTo("username", userName);

        userParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException exception) {
                if (exception == null && objects != null) {
                    currentUser = objects.get(0);
                    editTextProfileUsername.setText(currentUser.getUsername());

                    ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("Social");
                    objectParseQuery.whereEqualTo("username", currentUser.getUsername());
                    objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException exception1) {
                            if (exception1 == null && objects != null && objects.size() > 0) {
                                currentUserSocial = objects.get(0);

                                String text;
                                if (currentUserSocial.getList("followers").contains(ParseUser.getCurrentUser().getUsername())) {
                                    text = "following";
                                } else if (currentUserSocial.getList("pendingRequests").contains(ParseUser.getCurrentUser().getUsername())) {
                                    text = "requested";
                                } else {
                                    text = "follow";
                                }
                                buttonFollowUnfollow.setText(text);
                            }
                        }
                    });

                    // Getting user posts
                    ListView userListView = findViewById(R.id.specificPostListView);
                    
                    ArrayList<String> usernameArrayList = new ArrayList<>();
                    ArrayList<Bitmap> userPostArrayList = new ArrayList<>();
                    ArrayList<String> createdOnArrayList = new ArrayList<>();
                    ArrayList<String> captionArrayList = new ArrayList<>();
                    ArrayList<Integer> profileImageArrayList = new ArrayList<>();

                    ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Images");
                    parseQuery.whereEqualTo("username", userName);
                    parseQuery.addDescendingOrder("createdAt");

                    parseQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException exception) {
                            if (exception == null && objects != null) {
                                Log.i("INFO", "Here1");

                                for (ParseObject object : objects) {
                                    Log.i("Loop", "In loop now...");

                                    ParseFile file = (ParseFile) object.get("image");
                                    file.getDataInBackground(new GetDataCallback() {

                                        @Override
                                        public void done(byte[] data, ParseException exception1) {
                                            if (exception1 == null && data != null) {
                                                Log.i("Image", "Got an image");

                                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                userPostArrayList.add(bitmap);
                                                usernameArrayList.add(object.getString("username"));
                                                createdOnArrayList.add(object.getString("dateAndTime"));
                                                captionArrayList.add(object.getString("caption"));
                                                profileImageArrayList.add(R.drawable.man);

                                                userListView.setBackgroundColor(0x80ffffff);

                                                Log.i("INFO", usernameArrayList.toString());
                                                UserFeedAdapter userFeedAdapter = new UserFeedAdapter(ViewProfileActivity.this, usernameArrayList, userPostArrayList, createdOnArrayList, captionArrayList, profileImageArrayList);
                                                userListView.setAdapter(userFeedAdapter);

                                            } else {
                                                Log.i("Error In", "" + exception1);
                                            }
                                        }
                                    });
                                }


                            }
                        }
                    });




                } else {
                    Toast.makeText(ViewProfileActivity.this, "Failed to load Profile", Toast.LENGTH_SHORT).show();
                    Log.i("ERROR", "" + exception);
                }
            }
        });
    }
}
