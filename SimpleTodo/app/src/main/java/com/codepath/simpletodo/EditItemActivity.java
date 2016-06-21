package com.codepath.simpletodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class EditItemActivity extends Activity {

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        String item = getIntent().getStringExtra("text");
        position = getIntent().getIntExtra("position", 0);
        populateEditField(item);
    }

    private void populateEditField(String item) {
        EditText etNewItem = (EditText) findViewById(R.id.etEditItem);
        etNewItem.append(item);
    }

    public void onSave(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etEditItem);
        String newItem = etNewItem.getText().toString();
        Intent data = new Intent();
        data.putExtra("text", newItem);
        data.putExtra("code", 10);
        data.putExtra("position", position);
        setResult(RESULT_OK, data);
        this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
