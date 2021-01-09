package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FollowersFragment extends Fragment {

    String userName;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            SocialActivity.textViewFollowers.setBackgroundResource(R.color.white50);
            SocialActivity.textViewFollowersText.setBackgroundResource(R.color.white50);
            SocialActivity.textViewFollowing.setBackgroundResource(R.color.transparent);
            SocialActivity.textViewFollowingText.setBackgroundResource(R.color.transparent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followers, container, false);

        userName = SocialActivity.userName;
        Log.i("USERNAME follows", userName);

        ArrayList<String> arrayListUsername = new ArrayList<>();
        ArrayList<Integer> arrayListImages = new ArrayList<>();

        ListView listView = view.findViewById(R.id.listViewFollowers);
        MyArrayAdapter myArrayAdapter = new MyArrayAdapter(getActivity(), arrayListUsername, arrayListImages, false);
        listView.setAdapter(myArrayAdapter);

        ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("Social");
        objectParseQuery.whereEqualTo("username", userName);
        objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException exception) {
                if (exception == null && objects != null) {
                    ParseObject parseObject = objects.get(0);

                    String text = String.valueOf(parseObject.getList("followers").size());
                    SocialActivity.textViewFollowers.setText(text);

                    arrayListUsername.clear();
                    arrayListImages.clear();
                    for (Object username : parseObject.getList("followers")) {
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
                Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
                intent.putExtra("flag", false);
                intent.putExtra("username", arrayListUsername.get(position));

                startActivity(intent);
            }
        });

        return view;
    }
}
