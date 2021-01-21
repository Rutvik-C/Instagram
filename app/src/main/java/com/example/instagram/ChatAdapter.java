package com.example.instagram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> chatArrayList;
    ArrayList<Boolean> booleanArrayList;

    public ChatAdapter(@NonNull Context context, ArrayList<String> arrayList, ArrayList<Boolean> booleanArrayList) {
        super(context, R.layout.chat_layout_left, arrayList);

        this.context = context;
        this.chatArrayList = arrayList;
        this.booleanArrayList = booleanArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View view;
        if (booleanArrayList.get(position)) {  // True for right
            view = layoutInflater.inflate(R.layout.chat_layout_right, null, true);

            TextView textView = view.findViewById(R.id.text_view_chat_right);
            textView.setText(chatArrayList.get(position));

        } else {
            view = layoutInflater.inflate(R.layout.chat_layout_left, null, true);

            TextView textView = view.findViewById(R.id.text_view_chat_left);
            textView.setText(chatArrayList.get(position));

        }
        return view;

    }
}
