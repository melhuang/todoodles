package com.codepath.simpletodo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ItemsAdapter extends ArrayAdapter<TodoItem> {
    public ItemsAdapter(Context context, ArrayList<TodoItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TodoItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }
        // Lookup view for data population
        ImageView priorityBar = (ImageView) convertView.findViewById(R.id.priorityBar);
        TextView itemTitle = (TextView) convertView.findViewById(R.id.itemTitle);
        TextView itemDueDate = (TextView) convertView.findViewById(R.id.itemDueDate);
        CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
        // Populate the data into the template view using the data object
        priorityBar.setBackgroundColor(colorForPriority(item.priority));
        itemTitle.setText(item.title);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        itemDueDate.setText(format1.format(item.dueDate.getTime()));
        checkbox.setChecked(item.completed);
        // Return the completed view to render on screen
        return convertView;
    }

    private int colorForPriority(Priority priority) {
        switch (priority) {
            case HIGH:
                return Color.parseColor("#ff6961");
            case MEDIUM:
                return Color.parseColor("#fdfd96");
            case LOW:
                return Color.parseColor("#77dd77");
            default:
                return Color.parseColor("#ffffff");
        }
    }

}
