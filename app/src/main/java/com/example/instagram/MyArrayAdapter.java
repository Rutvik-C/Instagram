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

    public MyArrayAdapter(@NonNull Context context, ArrayList<String> usernameArrayList, ArrayList<Integer> imageArrayList) {
        super(context, R.layout.custom_list_view, usernameArrayList);

        this.context = context;
        this.usernameArrayList = usernameArrayList;
        this.imageArrayList = imageArrayList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.custom_list_view, null, true);

        TextView textView = view.findViewById(R.id.textView);
        ImageView imageView = view.findViewById(R.id.imageViewProfile);

        textView.setText(usernameArrayList.get(position));
        imageView.setImageResource(R.drawable.man);
        imageView.setImageResource(imageArrayList.get(position));

        return view;
    }
}
