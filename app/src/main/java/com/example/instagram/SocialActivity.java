package com.example.instagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class SocialActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    static TextView textViewFollowers, textViewFollowing, textViewFollowersText, textViewFollowingText;
    static String userName;
    SectionStatePagerAdapter sectionStatePagerAdapter;
    ViewPager viewPager;

    public void showSocialProfile(View v) {
        TextView textView = (TextView) v;

        if (Integer.parseInt(textView.getTag().toString()) == 0) {
            // followers
            textViewFollowers.setBackgroundResource(R.color.white50);
            textViewFollowersText.setBackgroundResource(R.color.white50);
            textViewFollowing.setBackgroundResource(R.color.transparent);
            textViewFollowingText.setBackgroundResource(R.color.transparent);

            viewPager.setCurrentItem(0);

        } else {
            // following
            textViewFollowers.setBackgroundResource(R.color.transparent);
            textViewFollowersText.setBackgroundResource(R.color.transparent);
            textViewFollowing.setBackgroundResource(R.color.white50);
            textViewFollowingText.setBackgroundResource(R.color.white50);

            viewPager.setCurrentItem(1);

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(SocialActivity.this, ViewProfileActivity.class);
            intent.putExtra("flag", userName.equals(ParseUser.getCurrentUser().getUsername()));
            intent.putExtra("username", userName);

            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        AppCompatTextView appCompatTextView = findViewById(R.id.title1);
        String temp = "Your Friends";
        appCompatTextView.setText(temp);

        Intent intent = getIntent();
        userName = intent.getStringExtra("username");
        int followers = intent.getIntExtra("followers", 0);
        int following = intent.getIntExtra("following", 0);

        textViewFollowers = findViewById(R.id.textViewfollowersNumber);
        textViewFollowing = findViewById(R.id.textViewFollowingNumber);
        textViewFollowersText = findViewById(R.id.textView6);
        textViewFollowingText = findViewById(R.id.textView7);

        textViewFollowers.setText(String.valueOf(followers));
        textViewFollowing.setText(String.valueOf(following));

        viewPager = findViewById(R.id.viewPager);
        sectionStatePagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        sectionStatePagerAdapter.addFragment(new FollowersFragment(), "followers");
        sectionStatePagerAdapter.addFragment(new FollowingFragment(), "following");

        viewPager.setAdapter(sectionStatePagerAdapter);
        viewPager.setCurrentItem(0);

    }
}
