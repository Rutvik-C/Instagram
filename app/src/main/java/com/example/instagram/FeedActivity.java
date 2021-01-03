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
    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            Uri imageLocation = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageLocation);
                Log.i("Success", "Image ready!");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] bytes = stream.toByteArray();
                ParseFile file = new ParseFile("image.png", bytes);

                ParseObject parseObject = new ParseObject("Images");
                parseObject.put("image", file);
                parseObject.put("username", ParseUser.getCurrentUser().getUsername());

                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                parseObject.put("dateAndTime", dateFormat.format(date));

                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException exception) {
                        if (exception == null) {
                            Toast.makeText(FeedActivity.this, "Image Posted Successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("ERROR", "unable to upload " + exception);
                        }
                    }
                });

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
            } else {
                getPhoto();
            }

        } else if (item.getItemId() == R.id.logOut) {
            ParseUser.logOut();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.userButton) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
        }

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
                                    profileImageArrayList.add(R.drawable.man);

                                    Log.i("INFO", usernameArrayList.toString());
                                    UserFeedAdapter userFeedAdapter = new UserFeedAdapter(FeedActivity.this, usernameArrayList, userPostArrayList, createdOnArrayList, profileImageArrayList);
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

//    The answer given by Bhavesh to his own question is a perfect answer.
//
//        The only additional point is:
//
//        1. In config.json of parse server, we have to set the "publicServerURL" to the same address as given in "serverURL" in the same file. (Public IP address issued by the EC2 for the instance - or equivalent cloud server).
//
//        2. Now we have to restart the parse server (not the EC2 server)
//
//        3. In case we stop the EC2 instance and re-start; the Public IP allotted for the server may change. In which case we have to change the "publicServerURL" accordingly.
//
//        This will ensure that the images are accessible both in the chrome browser as well as the app installed in the android phone.
//
//        Hope this saves time for some people.
//
//        Thank you