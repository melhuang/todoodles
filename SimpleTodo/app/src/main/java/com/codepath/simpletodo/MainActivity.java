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
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity {

    ArrayList<TodoItem> itemList;
    ArrayList<String> items;
    ItemsAdapter itemsAdapter;
    ListView lvItems;
    ItemsDatabaseHelper databaseHelper;

    private final int REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView)findViewById(R.id.lvItems);
        lvItems.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        databaseHelper = ItemsDatabaseHelper.getInstance(this);
        readItems();
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
                launchEditView(itemList.get(pos), pos);
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

    public void launchEditView(TodoItem item, int position) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("item", item);
        i.putExtras(b);
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
                TodoItem item = (TodoItem) data.getExtras().getSerializable("item");
                updateItem(item, position);
            }
        }
    }

    private void deleteItem(int position) {
        itemList.remove(position);
        itemsAdapter.notifyDataSetChanged();
        databaseHelper.deleteItem(itemList.get(position).title);
    }

    public void updateItem(TodoItem item, int position) {
        if (position >= itemList.size()) {
            itemList.add(item); // add to end of list
        } else {
            itemList.set(position, item);
        }
        itemsAdapter.notifyDataSetChanged();
        databaseHelper.addOrUpdateItem(item);
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
            TodoItem newItem = new TodoItem("", Calendar.getInstance(), Priority.MEDIUM, false);
            launchEditView(newItem, itemList.size());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void readItems() {
        itemList = databaseHelper.getAllItems();
    }
}

enum Priority {
    HIGH, MEDIUM, LOW
}

