package com.example.instagram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            FeedActivity.chatButtonImage.setVisibility(View.VISIBLE);
            FeedActivity.backButtonImage.setVisibility(View.INVISIBLE);

            String temp = "Instagram";
            FeedActivity.appCompatTextView.setText(temp);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        FeedActivity.chatButtonImage.setVisibility(View.VISIBLE);
        FeedActivity.backButtonImage.setVisibility(View.INVISIBLE);

        ListView feedListView = view.findViewById(R.id.feedListView);

        ArrayList<String> usernameArrayList = new ArrayList<>();
        ArrayList<Bitmap> userPostArrayList = new ArrayList<>();
        ArrayList<String> createdOnArrayList = new ArrayList<>();
        ArrayList<Integer> profileImageArrayList = new ArrayList<>();
        ArrayList<String> captionArrayList = new ArrayList<>();

        ArrayList<String> arrayList = new ArrayList<>();
        ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("Social");
        objectParseQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException exception) {
                if (exception == null && objects != null) {
                    ParseObject parseObject = objects.get(0);

                    for (Object username : parseObject.getList("follows")) {
                        arrayList.add(username.toString());
                    }

                    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Images");
                    parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    parseQuery.whereContainedIn("username", arrayList);
                    parseQuery.addDescendingOrder("dateAndTime");

                    parseQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException exception) {
                            if (exception == null && objects != null) {

                                for (ParseObject object : objects) {

                                    ParseFile file = (ParseFile) object.get("image");
                                    file.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException exception1) {
                                            if (exception1 == null && data != null) {

                                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                userPostArrayList.add(bitmap);
                                                usernameArrayList.add(object.getString("username"));
                                                createdOnArrayList.add(object.getString("dateAndTime"));
                                                captionArrayList.add(object.getString("caption"));
                                                profileImageArrayList.add(R.drawable.man);

                                                UserFeedAdapter userFeedAdapter = new UserFeedAdapter(getActivity(), usernameArrayList, userPostArrayList, createdOnArrayList, captionArrayList, profileImageArrayList);
                                                feedListView.setAdapter(userFeedAdapter);

                                            } else {
                                                Log.i("Error In", "" + exception1);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Unable to find your social account\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }
}
