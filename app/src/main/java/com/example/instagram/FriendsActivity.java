package com.example.instagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FriendsActivity extends AppCompatActivity {

    View followerLine;
    View followingLine;

    public void showFollowers(View view) {
        followerLine.setVisibility(View.VISIBLE);
        followingLine.setVisibility(View.INVISIBLE);


    }

    public void showFollowing(View view) {
        followerLine.setVisibility(View.INVISIBLE);
        followingLine.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_with_back);
        AppCompatImageView appCompatImageView = findViewById(R.id.imageViewBack);
        appCompatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, FeedActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AppCompatTextView appCompatTextView = findViewById(R.id.title1);
        String temp = "Your Friends";
        appCompatTextView.setText(temp);

        followerLine = findViewById(R.id.followersLine);
        followingLine = findViewById(R.id.followingLine);
    }
}