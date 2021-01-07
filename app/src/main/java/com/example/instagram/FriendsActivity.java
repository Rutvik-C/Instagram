package com.example.instagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    View followerLine;
    View followingLine;
    ListView listView;
    ArrayList<String> arrayListUsername;
    ArrayList<Integer> arrayListImages;
    MyArrayAdapter myArrayAdapter;
    TextView textViewFollowers, textViewFollowing;


    private void fetchListAndUpdateListView(String people) {
        ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("Social");
        objectParseQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException exception) {
                if (exception == null && objects != null) {
                    ParseObject parseObject = objects.get(0);

                    String text = String.valueOf(parseObject.getList(people).size());
                    if (people.equals("followers")) {
                        textViewFollowers.setText(text);
                    } else {
                        textViewFollowing.setText(text);
                    }

                    arrayListUsername.clear();
                    arrayListImages.clear();
                    for (Object username : parseObject.getList(people)) {
                        arrayListUsername.add(username.toString());
                        arrayListImages.add(R.drawable.man);
                    }
                    myArrayAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(FriendsActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void showFollowers(View view) {
        followerLine.setVisibility(View.VISIBLE);
        followingLine.setVisibility(View.INVISIBLE);

        fetchListAndUpdateListView("followers");

    }

    public void showFollowing(View view) {
        followerLine.setVisibility(View.INVISIBLE);
        followingLine.setVisibility(View.VISIBLE);

        fetchListAndUpdateListView("follows");

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

        textViewFollowers = findViewById(R.id.textViewfollowersNumber);
        textViewFollowing = findViewById(R.id.textViewFollowingNumber);

        listView = findViewById(R.id.listView);
        arrayListUsername = new ArrayList<>();
        arrayListImages = new ArrayList<>();
        myArrayAdapter = new MyArrayAdapter(FriendsActivity.this, arrayListUsername, arrayListImages, false);
        listView.setAdapter(myArrayAdapter);

        showFollowers(listView);
        showFollowing(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FriendsActivity.this, ViewProfileActivity.class);
                intent.putExtra("flag", false);
                intent.putExtra("username", arrayListUsername.get(position));

                startActivity(intent);
            }
        });
    }
}