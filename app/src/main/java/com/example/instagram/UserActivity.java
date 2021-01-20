package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    ListView listViewUsers;
    EditText searchUserEditText;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(UserActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(listViewUsers.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        AppCompatTextView appCompatTextView = findViewById(R.id.title1);
        String temp = "Search Users";
        appCompatTextView.setText(temp);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent0 = new Intent(UserActivity.this, FeedActivity.class);
                        intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent0);
                        break;
                    case R.id.search:
                        break;
                    case R.id.post:
                        Intent intent1 = new Intent(UserActivity.this, UploadImageActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        break;
                    case R.id.activity:
                        Intent intent2 = new Intent(UserActivity.this, RequestActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        break;
                    case R.id.user:
                        Intent intent3 = new Intent(UserActivity.this, ViewProfileActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent3.putExtra("flag", true);
                        intent3.putExtra("username", ParseUser.getCurrentUser().getUsername());

                        startActivity(intent3);
                        break;
                }

                return false;
            }
        });


        listViewUsers = findViewById(R.id.listViewUsers);
        searchUserEditText = findViewById(R.id.editTextSearchUsers);

        ArrayList<String> users = new ArrayList<>();
        ArrayList<Integer> images = new ArrayList<>();
        MyArrayAdapter myArrayAdapter = new MyArrayAdapter(getApplicationContext(), users, images, false);

        listViewUsers.setAdapter(myArrayAdapter);

        searchUserEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
                    userParseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    userParseQuery.whereStartsWith("username", s.toString());
                    userParseQuery.addAscendingOrder("username");

                    userParseQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException exception) {
                            if (exception == null && objects != null) {
                                users.clear();
                                images.clear();
                                for (ParseUser object : objects) {
                                    users.add(object.getUsername());
                                    images.add(R.drawable.man);
                                }

                                myArrayAdapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(UserActivity.this, "Unable to load users", Toast.LENGTH_SHORT).show();
                                Log.i("ERROR", "" + exception);
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UserActivity.this, ViewProfileActivity.class);
                intent.putExtra("flag", false);
                intent.putExtra("username", users.get(position));

                startActivity(intent);
            }
        });

    }
}