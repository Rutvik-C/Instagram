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

    public void viewProfile(View view) {
        Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra("flag", true);
        intent.putExtra("username", ParseUser.getCurrentUser().getUsername());

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.shareButton) {
            Intent intent = new Intent(this, UploadImageActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.logOut) {
            ParseUser.logOut();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.userButton) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.userRequests) {
            Intent intent = new Intent(this, RequestActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.friends) {
            Intent intent = new Intent(this, FriendsActivity.class);
            startActivity(intent);

        }
        finish();

        return super.onOptionsItemSelected(item);
    }

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

        TextView textView = findViewById(R.id.textView2);
        textView.setText(ParseUser.getCurrentUser().getUsername());

        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Images");
        parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
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

                                    Log.i("INFO", usernameArrayList.toString());
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

    }
}
