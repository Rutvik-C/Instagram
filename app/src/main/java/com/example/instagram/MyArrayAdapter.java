package com.example.instagram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyArrayAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> usernameArrayList;
    ArrayList<Integer> imageArrayList;
    boolean isFolList;

    public MyArrayAdapter(@NonNull Context context, ArrayList<String> usernameArrayList, ArrayList<Integer> imageArrayList, boolean isFolList) {
        super(context, R.layout.custom_list_view, usernameArrayList);

        this.context = context;
        this.usernameArrayList = usernameArrayList;
        this.imageArrayList = imageArrayList;
        this.isFolList = isFolList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.custom_list_view, null, true);

        TextView textView = view.findViewById(R.id.textView);
        ImageView imageView = view.findViewById(R.id.imageViewProfile);

        textView.setText(usernameArrayList.get(position));
        imageView.setImageResource(imageArrayList.get(position));

        if (isFolList) {
            TextView textView1 = view.findViewById(R.id.textViewfollow);
            TextView textView2 = view.findViewById(R.id.textViewReject);

            textView1.setTag(usernameArrayList.get(position));
            textView2.setTag(usernameArrayList.get(position));

            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
