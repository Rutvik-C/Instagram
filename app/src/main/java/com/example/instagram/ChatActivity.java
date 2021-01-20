package com.example.instagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ListView chatListView;
    ArrayList<String> chatArrayList;
    ArrayAdapter<String> arrayAdapter;
    private SQLiteDatabase database;
    String chattingWith, currentUser;
    ParseObject targetObject;


    public void sendMessage(View view) {
        EditText editText = findViewById(R.id.editTextMessage);
        String text = editText.getText().toString();
        editText.setText("");

        if (!text.equals("")) {
            targetObject.add("messages", text);
            targetObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException exception) {
                    if (exception == null) {
                        database.execSQL(String.format("INSERT INTO table_%s(chatter, text_message) VALUES ('%s', '%s')", chattingWith, currentUser, text));
                        chatArrayList.add(currentUser + ":\n" + text);
                        arrayAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ChatActivity.this, "Failed to send message!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void createChatObject() {
        Log.i("Creating", "Creating object");
        ParseObject parseObject = new ParseObject("Chats");
        parseObject.put("from_to", currentUser + "->" + chattingWith);
        parseObject.put("messages", new ArrayList<String>());
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException exception) {
                if (exception == null) {
                    searchOrCreateChatObject();
                } else {
                    Log.i("FAIL", "failed to create object " + exception);
                }
            }
        });

    }

    private void searchOrCreateChatObject() {
        ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("Chats");
        objectParseQuery.whereEqualTo("from_to", currentUser + "->" + chattingWith);
        objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException exception1) {
                if (exception1 == null && objects != null) {
                    if (objects.size() > 0) {
                        targetObject = objects.get(0);
                        Log.i("Done", "Found");
                    } else {
                        // make object
                        createChatObject();
                    }
                } else {
                    Log.i("ERROR", "" + exception1);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_with_profile);
        AppCompatTextView appCompatTextView = findViewById(R.id.title1);

//        chattingWith = "rutvik"; // will come through intent
//        currentUser = "neelam"; // ParseUser.getCurrentUser().getUsername()
        chattingWith = "neelam";
        currentUser = "rutvik";


        appCompatTextView.setText(chattingWith);

        chatListView = findViewById(R.id.listViewChats);
        chatArrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatArrayList);
        chatListView.setAdapter(arrayAdapter);

        database = this.openOrCreateDatabase("ChatDatabase_" + currentUser, MODE_PRIVATE, null);
        database.execSQL(String.format("CREATE TABLE IF NOT EXISTS table_%s (text_id INTEGER PRIMARY KEY AUTOINCREMENT, chatter VARCHAR, text_message VARCHAR)", chattingWith));

        searchOrCreateChatObject();

        // fetch messages
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Chats");
        parseQuery.whereEqualTo("from_to", chattingWith + "->" + currentUser);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException exception) {
                if (exception == null && objects != null && objects.size() > 0) {
                    Log.i("Extracting", chattingWith + "->" + currentUser);

                    List<String> tempList = objects.get(0).getList("messages");
                    objects.get(0).remove("messages");
                    objects.get(0).put("messages", new ArrayList<String>());
                    objects.get(0).saveInBackground();
                    for (String message : tempList) {
                        database.execSQL(String.format("INSERT INTO table_%s(chatter, text_message) VALUES ('%s', '%s')", chattingWith, chattingWith, message));
                    }

                    Log.i("List", tempList.toString());
                    @SuppressLint("Recycle") Cursor cursor = database.rawQuery(String.format("SELECT * FROM (SELECT * FROM table_%s ORDER BY text_id DESC LIMIT 30) Var1 ORDER BY text_id ASC", chattingWith), null);
                    int chatterColumnIndex = cursor.getColumnIndex("chatter");
                    int messageColumnIndex = cursor.getColumnIndex("text_message");

                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        chatArrayList.add(cursor.getString(chatterColumnIndex) + ":\n" + cursor.getString(messageColumnIndex));

                        arrayAdapter.notifyDataSetChanged();
                        cursor.moveToNext();
                    }
                }
            }
        });

        // fetch updates from server and push it to db and then delete from server
//        String message = "Wohoo";
//        database.execSQL(String.format("INSERT INTO table_%s(chatter, text_message) VALUES ('%s', '%s')", chattingWith, chattingWith, message));
//        message = "YAY";
//        database.execSQL(String.format("INSERT INTO table_%s(chatter, text_message) VALUES ('%s', '%s')", chattingWith, currentUser, message));

        // fetch last n messages from db


    }
}
