package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

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

    SectionStatePagerAdapter sectionStatePagerAdapter;
    static ViewPager viewPager;
    static AppCompatImageView backButtonImage, chatButtonImage;
    static AppCompatTextView appCompatTextView;


    public void chats(View view) {
        viewPager.setCurrentItem(1);
        chatButtonImage.setVisibility(View.INVISIBLE);
        backButtonImage.setVisibility(View.VISIBLE);
    }

    public void back(View view) {
        viewPager.setCurrentItem(0);
        chatButtonImage.setVisibility(View.VISIBLE);
        backButtonImage.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_with_rightcornerbutton);
        backButtonImage = findViewById(R.id.backButton);
        chatButtonImage = findViewById(R.id.chatButton);
        backButtonImage.setVisibility(View.INVISIBLE);

        appCompatTextView = findViewById(R.id.title1);

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

        viewPager = findViewById(R.id.feedViewPager);
        sectionStatePagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        sectionStatePagerAdapter.addFragment(new FeedFragment(), "feed");
        sectionStatePagerAdapter.addFragment(new ChatFragment(), "chats");

        viewPager.setAdapter(sectionStatePagerAdapter);
        viewPager.setCurrentItem(0);

    }
}
