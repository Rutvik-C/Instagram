package com.example.instagram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserFeedAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> usernameArrayList;
    ArrayList<Bitmap> imageArrayList;
    ArrayList<String> createdOnArrayList;
    ArrayList<Integer> profilePicArrayList;

    public UserFeedAdapter(@NonNull Context context, ArrayList<String> usernameArrayList, ArrayList<Bitmap> imageArrayList, ArrayList<String> createdOnArrayList, ArrayList<Integer> profilePicArrayList) {
        super(context, R.layout.user_feed_list_view, usernameArrayList);

        this.context = context;
        this.usernameArrayList = usernameArrayList;
        this.imageArrayList = imageArrayList;
        this.createdOnArrayList = createdOnArrayList;
        this.profilePicArrayList = profilePicArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.user_feed_list_view, null, true);

        TextView textView = view.findViewById(R.id.textViewPostUsername);
        ImageView imageView = view.findViewById(R.id.imageViewPost);
        TextView textView1 = view.findViewById(R.id.textViewPostCreatedOn);
        ImageView imageView1 = view.findViewById(R.id.imageViewProfilePic);

        textView.setText(usernameArrayList.get(position));
        imageView.setImageBitmap(imageArrayList.get(position));
        textView1.setText(createdOnArrayList.get(position));
        imageView1.setImageResource(profilePicArrayList.get(position));


        return view;
    }
}
