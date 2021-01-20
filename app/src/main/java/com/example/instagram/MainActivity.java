package com.example.instagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    TextView textViewToggle;
    Button buttonLogin, buttonSignup;
    EditText editTextUsername, editTextEmail, editTextPasswordLogin, editTextPasswordSignup, editTextPasswordSignupAgain;
    boolean isLoginOnScreen;

    public void launchUserActivity() {
        Intent intent = new Intent(MainActivity.this, FeedActivity.class);

        startActivity(intent);
        finish();
    }

    public void doLogin(View view) {
        Log.i("Button", "Log in");
        String username = editTextUsername.getText().toString();
        String password = editTextPasswordLogin.getText().toString();

        if (username.equals("") || password.equals("")) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
        } else {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException exception) {
                    if (exception == null && user != null) {
                        Log.i("Success", "Log In successful");

                        launchUserActivity();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void doSignup(View view) {
        Log.i("Action", "Sign up");

        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPasswordSignup.getText().toString();
        String passwordAgain = editTextPasswordSignupAgain.getText().toString();

        if (username.equals("") || email.equals("") || password.equals("") || passwordAgain.equals("")) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(passwordAgain)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("Creating User", username + " : " + email + " : " + password);

            ParseUser newUser = new ParseUser();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);

            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException exception) {
                    if (exception == null) {
                        Log.i("Success", "sign up successful");
                        ParseObject parseObject = new ParseObject("Social");
                        parseObject.put("username", username);
                        parseObject.put("pendingRequests", new ArrayList<String>());
                        parseObject.put("follows", new ArrayList<String>());
                        parseObject.put("followers", new ArrayList<String>());

                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException ex) {
                                if (ex == null) {
                                    ParseObject parseObject1 = new ParseObject("Chats");
                                    parseObject1.put("username", username);
                                    parseObject1.put("messages", new ArrayList<String>());
                                    parseObject1.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException exception1) {
                                            if (exception1 == null) {
                                                launchUserActivity();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Failed to Sign In\nTry again after some time", Toast.LENGTH_SHORT).show();
                                                Log.i("Failed", "sign up failed " + exception1);
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(MainActivity.this, "Failed to Sign In\nTry again after some time", Toast.LENGTH_SHORT).show();
                                    Log.i("Failed", "sign up failed " + ex);
                                }
                            }
                        });

                    } else {
                        Toast.makeText(MainActivity.this, "Failed to Sign In\nTry again after some time", Toast.LENGTH_SHORT).show();
                        Log.i("Failed", "sign up failed " + exception);
                    }
                }
            });
        }
    }

    public void toggleOption(View view) {
        String toggleText;

        if (isLoginOnScreen) {
            isLoginOnScreen = false;
            buttonLogin.setVisibility(View.INVISIBLE);
            buttonSignup.setVisibility(View.VISIBLE);
            editTextPasswordLogin.setVisibility(View.INVISIBLE);
            editTextEmail.setVisibility(View.VISIBLE);
            editTextPasswordSignup.setVisibility(View.VISIBLE);
            editTextPasswordSignupAgain.setVisibility(View.VISIBLE);
            toggleText = "LOG IN HERE";

        } else {
            isLoginOnScreen = true;
            buttonLogin.setVisibility(View.VISIBLE);
            buttonSignup.setVisibility(View.INVISIBLE);
            editTextPasswordLogin.setVisibility(View.VISIBLE);
            editTextEmail.setVisibility(View.INVISIBLE);
            editTextPasswordSignup.setVisibility(View.INVISIBLE);
            editTextPasswordSignupAgain.setVisibility(View.INVISIBLE);
            toggleText = "SIGN UP HERE";
        }

        textViewToggle.setText(toggleText);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editTextUsername.getWindowToken(), 0);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (v.getId() == R.id.editTextPasswordLogin) {
                doLogin(v);
            } else if (v.getId() == R.id.editTextPasswordSignupAgain) {
                doSignup(v);
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        textViewToggle = findViewById(R.id.textViewToggle);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin);
        editTextPasswordSignup = findViewById(R.id.editTextPasswordSignup);
        editTextPasswordSignupAgain = findViewById(R.id.editTextPasswordSignupAgain);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);

        isLoginOnScreen = true;
        buttonLogin.setVisibility(View.VISIBLE);
        buttonSignup.setVisibility(View.INVISIBLE);
        editTextPasswordLogin.setVisibility(View.VISIBLE);
        editTextEmail.setVisibility(View.INVISIBLE);
        editTextPasswordSignup.setVisibility(View.INVISIBLE);
        editTextPasswordSignupAgain.setVisibility(View.INVISIBLE);

        ConstraintLayout backgroundLayout = findViewById(R.id.backgroundLayout);
        backgroundLayout.setOnClickListener(this);  // to hide keyboard when touched anywhere on screen # UX
        editTextPasswordLogin.setOnKeyListener(this);  // When enter is pressed directly log in will be clicked
        editTextPasswordSignupAgain.setOnKeyListener(this);  // --"-- sign up --"--


        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
//        if (ParseUser.getCurrentUser() != null) {
//            launchUserActivity();
//        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
