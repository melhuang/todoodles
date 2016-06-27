package com.codepath.simpletodo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity {

    ArrayList<String> items;
    ItemsAdapter itemsAdapter;
    ListView lvItems;

    private final int REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView)findViewById(R.id.lvItems);
        lvItems.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        readItems();
        ArrayList<TodoItem> itemList = new ArrayList<TodoItem>();
        for(String item : items) {
            itemList.add(new TodoItem(item));
        }
        itemsAdapter = new ItemsAdapter(this, itemList);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter,
                                           View item, int pos, long id) {
                // the ArrayList items backs the list view, so fetch directly from the data structure
                launchEditView(items.get(pos), pos);
                return true;
            }
                }
        );

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
            }
        });
    }

    public void launchEditView(String item, int position) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra("text", item);
        i.putExtra("position", position);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            int position = data.getExtras().getInt("position");
            if (data.hasExtra("delete")) {
                deleteItem(position);
            } else {
                String newText = data.getExtras().getString("text");
                updateItem(newText, position);
            }
        }
    }

    private void deleteItem(int position) {
        items.remove(position);
        itemsAdapter.notifyDataSetChanged();
        writeItems();
    }

    public void updateItem(String newText, int position) {
        if (position >= items.size()) {
            items.add(newText); // add to end of list
        } else {
            items.set(position, newText);
        }
        itemsAdapter.notifyDataSetChanged();
        writeItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            launchEditView("", items.size());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

enum Priority {
    HIGH, MEDIUM, LOW
}

