package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        ListView feedListView = findViewById(R.id.feedListView);

        ArrayList<String> usernameArrayList = new ArrayList<>();
        ArrayList<Bitmap> userPostArrayList = new ArrayList<>();
        ArrayList<String> createdOnArrayList = new ArrayList<>();
        ArrayList<Integer> profileImageArrayList = new ArrayList<>();
        ArrayList<String> captionArrayList = new ArrayList<>();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        break;
                    case R.id.search:
                        Intent intent0 = new Intent(FeedActivity.this, UserActivity.class);
                        intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent0);
                        finish();
                        break;
                    case R.id.post:
                        Intent intent1 = new Intent(FeedActivity.this, UploadImageActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.activity:
                        Intent intent2 = new Intent(FeedActivity.this, RequestActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.user:
                        Intent intent3 = new Intent(FeedActivity.this, ViewProfileActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent3.putExtra("flag", true);
                        intent3.putExtra("username", ParseUser.getCurrentUser().getUsername());

                        startActivity(intent3);
                        finish();
                        break;
                }

                return false;
            }
        });


        ArrayList<String> arrayList = new ArrayList<>();
        ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("Social");
        objectParseQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException exception) {
                if (exception == null && objects != null) {
                    ParseObject parseObject = objects.get(0);

                    for (Object username : parseObject.getList("follows")) {
                        arrayList.add(username.toString());
                    }

                    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Images");
                    parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    parseQuery.whereContainedIn("username", arrayList);
                    parseQuery.addDescendingOrder("dateAndTime");

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

                                                UserFeedAdapter userFeedAdapter = new UserFeedAdapter(FeedActivity.this, usernameArrayList, userPostArrayList, createdOnArrayList, captionArrayList, profileImageArrayList);
                                                feedListView.setAdapter(userFeedAdapter);

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
                    Toast.makeText(FeedActivity.this, "Unable to find your social account\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
