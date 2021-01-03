package com.example.instagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

        listViewUsers = findViewById(R.id.listViewUsers);
        searchUserEditText = findViewById(R.id.editTextSearchUsers);

        ArrayList<String> users = new ArrayList<>();
        ArrayList<Integer> images = new ArrayList<>();
        MyArrayAdapter myArrayAdapter = new MyArrayAdapter(getApplicationContext(), users, images);

        listViewUsers.setAdapter(myArrayAdapter);

        searchUserEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("on", s.toString());


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