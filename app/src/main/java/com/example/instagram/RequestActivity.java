package com.example.instagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestActivity extends AppCompatActivity {

    ArrayList<String> pendingRequests;
    MyArrayAdapter myArrayAdapter;
    ParseObject currUserSocial;

    public void acceptOrReject(View view) {
        TextView textView = (TextView) view;

        if (textView.getText().toString().equals("ACCEPT")) {
            // Accept request

            currUserSocial.add("followers", textView.getTag().toString());

            ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("Social");
            objectParseQuery.whereEqualTo("username", textView.getTag().toString());
            objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException exception) {
                    if (exception == null) {
                        if (objects != null && objects.size() > 0) {
                            objects.get(0).add("follows", ParseUser.getCurrentUser().getUsername());
                            objects.get(0).saveInBackground();

                        } else {
                            Toast.makeText(RequestActivity.this, "Could not find your friends social account", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RequestActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        // Common part for Accept and request
        // Notification deletion logic
        pendingRequests.remove(textView.getTag().toString());
        currUserSocial.remove("pendingRequests");
        currUserSocial.put("pendingRequests", pendingRequests);

        currUserSocial.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException exception) {
                if (exception == null) {
                    Toast.makeText(RequestActivity.this, "Notification Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RequestActivity.this, "Failed to delete Notification", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myArrayAdapter.notifyDataSetChanged();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(RequestActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_with_back);
        AppCompatImageView appCompatImageView = findViewById(R.id.imageViewBack);
        appCompatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestActivity.this, FeedActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AppCompatTextView appCompatTextView = findViewById(R.id.title1);
        String temp = "Follow Requests";
        appCompatTextView.setText(temp);

        ListView listViewUserRequest = findViewById(R.id.listViewUserRequest);
        pendingRequests = new ArrayList<>();
        ArrayList<Integer> pendingReqImages = new ArrayList<>();
        myArrayAdapter = new MyArrayAdapter(RequestActivity.this, pendingRequests, pendingReqImages, true);

        ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("Social");
        objectParseQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException exception) {
                if (exception == null) {
                    if (objects != null && objects.size() > 0) {
                        currUserSocial = objects.get(0);

                        for (Object username : Objects.requireNonNull(currUserSocial.getList("pendingRequests"))) {
                            pendingRequests.add(username.toString());
                            pendingReqImages.add(R.drawable.man);
                        }
                        myArrayAdapter.notifyDataSetChanged();

                        listViewUserRequest.setAdapter(myArrayAdapter);


                    } else {
                        Toast.makeText(RequestActivity.this, "Could not find your social account", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RequestActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        listViewUserRequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RequestActivity.this, ViewProfileActivity.class);
                intent.putExtra("flag", false);
                intent.putExtra("username", pendingRequests.get(position));

                startActivity(intent);
            }
        });

    }
}
