package com.example.talrota.todolist;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String>{

    public CustomAdapter(Context context, int resource) {
        super(context, resource);

    }

    public CustomAdapter(MainActivity mainActivity, int simple_list_item_1, ArrayList<String> todoArray) {
        super(mainActivity, simple_list_item_1, todoArray);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (position % 2 == 1) {
            view.setBackgroundColor(Color.parseColor("#ff6666"));
        } else {
            view.setBackgroundColor(Color.parseColor("#80aaff"));
        }

        return view;
    }
}
