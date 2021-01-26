package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment {

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            FeedActivity.chatButtonImage.setVisibility(View.INVISIBLE);
            FeedActivity.backButtonImage.setVisibility(View.VISIBLE);

            String temp = "Chats";
            FeedActivity.appCompatTextView.setText(temp);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        ArrayList<String> arrayListUsername = new ArrayList<>();
        ArrayList<Integer> arrayListImages = new ArrayList<>();

        ListView listView = view.findViewById(R.id.listViewChatPeople);
        MyArrayAdapter myArrayAdapter = new MyArrayAdapter(Objects.requireNonNull(getActivity()), arrayListUsername, arrayListImages, false);
        listView.setAdapter(myArrayAdapter);

        ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("Social");
        objectParseQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException exception) {
                if (exception == null && objects != null) {
                    ParseObject parseObject = objects.get(0);

                    arrayListUsername.clear();
                    arrayListImages.clear();
                    for (Object username : Objects.requireNonNull(parseObject.getList("follows"))) {
                        arrayListUsername.add(username.toString());
                        arrayListImages.add(R.drawable.man);
                    }
                    myArrayAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chattingWith", arrayListUsername.get(position));

                startActivity(intent);
            }
        });

        return view;
    }
}
