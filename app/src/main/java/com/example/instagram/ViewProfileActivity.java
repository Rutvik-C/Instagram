package com.example.instagram;

import androidx.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    ParseObject currentUserSocial;  // this is the user whose profile is being viewed
    boolean isAuth = false;
    private int followers, following;
    private String userName;

    public void seeSocialStats(View view) {
        if (isAuth) {

            Intent intent = new Intent(this, SocialActivity.class);
            intent.putExtra("username", userName);
            intent.putExtra("followers", followers);
            intent.putExtra("following", following);

            startActivity(intent);
            finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu1, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

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

        if (buttonFollowUnfollow.getText().equals("follow") || buttonFollowUnfollow.getText().equals("follow back")) {
            // send follow request algo

            currentUserSocial.add("pendingRequests", ParseUser.getCurrentUser().getUsername());
            saveParseObjectInBackground(currentUserSocial, "requested");

        } else if (buttonFollowUnfollow.getText().equals("following")) {
            // unfollow algo
            // remove target user from getCurrentUsers Social follows
            // remove getCurrentUsers Social from target users followers

            ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("Social");
            objectParseQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException exception) {
                    if (exception == null && objects != null) {
                        objects.get(0).getList("follows").remove(currentUser.getUsername());
                        List<String> tempList = objects.get(0).getList("follows");
                        objects.get(0).remove("follows");
                        objects.get(0).put("follows", tempList);

                        objects.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException exception1) {
                                if (exception1 == null) {
                                    currentUserSocial.getList("followers").remove(ParseUser.getCurrentUser().getUsername());
                                    List<String> tempList = currentUserSocial.getList("followers");
                                    currentUserSocial.remove("followers");
                                    currentUserSocial.put("followers", tempList);

                                    saveParseObjectInBackground(currentUserSocial, "follow");

                                } else {
                                    Toast.makeText(ViewProfileActivity.this, "Task Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(ViewProfileActivity.this, "Failed to find your social account", Toast.LENGTH_SHORT).show();
                    }
                }
            });


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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Button buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonFollowUnfollow = findViewById(R.id.buttonFollowUnfollow);

        // Action bar setup
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent0 = new Intent(ViewProfileActivity.this, FeedActivity.class);
                        intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent0);
                        break;
                    case R.id.search:
                        Intent intent1 = new Intent(ViewProfileActivity.this, UserActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        break;
                    case R.id.post:
                        Intent intent2 = new Intent(ViewProfileActivity.this, UploadImageActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        break;
                    case R.id.activity:
                        Intent intent3 = new Intent(ViewProfileActivity.this, RequestActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        break;
                    case R.id.user:
                        break;
                }

                return false;
            }
        });

        // Fetching data
        Intent intent = getIntent();
        boolean isUser = intent.getBooleanExtra("flag", false);
        userName = intent.getStringExtra("username");

        // Title based on owner or viewer of account
        AppCompatTextView appCompatTextView = findViewById(R.id.title1);
        if (isUser) {
            buttonEditProfile.setVisibility(View.VISIBLE);
            buttonFollowUnfollow.setVisibility(View.INVISIBLE);
        } else {
            buttonEditProfile.setVisibility(View.INVISIBLE);
            buttonFollowUnfollow.setVisibility(View.VISIBLE);
        }
        appCompatTextView.setText(" " + userName);


        // Getting the user info
        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereEqualTo("username", userName);

        userParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException exception) {
                if (exception == null && objects != null) {
                    currentUser = objects.get(0);

                    ParseQuery<ParseObject> objectParseQuery = new ParseQuery<>("Social");
                    objectParseQuery.whereEqualTo("username", currentUser.getUsername());
                    objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException exception1) {
                            if (exception1 == null && objects != null && objects.size() > 0) {
                                currentUserSocial = objects.get(0);

                                String text;
                                isAuth = false;
                                if (currentUserSocial.getList("followers").contains(ParseUser.getCurrentUser().getUsername()) || isUser) {
                                    text = "following";
                                    isAuth = true;

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

                                                for (ParseObject object : objects) {

                                                    ParseFile file = (ParseFile) object.get("image");
                                                    file.getDataInBackground(new GetDataCallback() {

                                                        @Override
                                                        public void done(byte[] data, ParseException exception1) {
                                                            if (exception1 == null && data != null) {

                                                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                                userPostArrayList.add(bitmap);
                                                                usernameArrayList.add(object.getString("username"));
                                                                createdOnArrayList.add(object.getString("dateAndTime"));
                                                                captionArrayList.add(object.getString("caption"));
                                                                profileImageArrayList.add(R.drawable.man);

                                                                userListView.setBackgroundColor(0x80ffffff);

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

                                } else if (currentUserSocial.getList("pendingRequests").contains(ParseUser.getCurrentUser().getUsername())) {
                                    text = "requested";
                                } else if (currentUserSocial.getList("follows").contains(ParseUser.getCurrentUser().getUsername())){
                                    text = "follow back";
                                } else {
                                    text = "follow";
                                }
                                buttonFollowUnfollow.setText(text);


                                ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("Social");
                                objectParseQuery.whereEqualTo("username", userName);
                                objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException exception) {
                                        if (exception == null && objects != null) {
                                            ParseObject parseObject = objects.get(0);

                                            followers = parseObject.getList("followers").size();
                                            following = parseObject.getList("follows").size();
                                            TextView textView = findViewById(R.id.textViewfollowersNumber);
                                            textView.setText(String.valueOf(followers));
                                            TextView textView1 = findViewById(R.id.textViewFollowingNumber);
                                            textView1.setText(String.valueOf(following));
                                        }
                                    }
                                });

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
