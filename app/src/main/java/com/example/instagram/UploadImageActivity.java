package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadImageActivity extends AppCompatActivity {

    Bitmap bitmap;
    ImageView imageViewPost;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(UploadImageActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 0);
    }

    public void getPhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
        } else {
            getPhoto();
        }
    }

    public void post(View view) {

        EditText editText = findViewById(R.id.editTextPostCaption);

        if (bitmap == null) {
            Toast.makeText(this, "Click on Image to select an Image", Toast.LENGTH_SHORT).show();

        } else {
            Log.i("INFO", "Bitmap is not null");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] bytes = stream.toByteArray();
            ParseFile file = new ParseFile("image.png", bytes);

            ParseObject parseObject = new ParseObject("Images");
            parseObject.put("image", file);
            parseObject.put("username", ParseUser.getCurrentUser().getUsername());
            parseObject.put("caption", editText.getText().toString());

            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            parseObject.put("dateAndTime", dateFormat.format(date));

            parseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException exception) {
                    if (exception == null) {
                        Toast.makeText(UploadImageActivity.this, "Image Posted Successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UploadImageActivity.this, "Failed to Post Image", Toast.LENGTH_SHORT).show();
                        Log.i("ERROR", "unable to upload " + exception);
                    }
                }
            });

            Intent intent = new Intent(this, FeedActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            Log.i("ACT RES", "Picker");
            Uri imageLocation = data.getData();

            CropImage.activity(imageLocation)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Log.i("ACT RES", "Cropper");

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Uri uri = result.getUri();


            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                imageViewPost.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        AppCompatTextView appCompatTextView = findViewById(R.id.title1);
        String temp = "Post Image";
        appCompatTextView.setText(temp);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent0 = new Intent(UploadImageActivity.this, FeedActivity.class);
                        intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent0);
                        break;
                    case R.id.search:
                        Intent intent1 = new Intent(UploadImageActivity.this, UserActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        break;
                    case R.id.post:
                        break;
                    case R.id.activity:
                        Intent intent2 = new Intent(UploadImageActivity.this, RequestActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        break;
                    case R.id.user:
                        Intent intent3 = new Intent(UploadImageActivity.this, ViewProfileActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent3.putExtra("flag", true);
                        intent3.putExtra("username", ParseUser.getCurrentUser().getUsername());

                        startActivity(intent3);
                        break;
                }

                return false;
            }
        });


        imageViewPost = findViewById(R.id.imageViewPostAct);


    }
}
