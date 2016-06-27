package com.codepath.simpletodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;


public class EditItemActivity extends Activity {

    int position;
    TodoItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        item = (TodoItem) getIntent().getExtras().getSerializable("item");
        position = getIntent().getIntExtra("position", 0);
        setupNumberPicker();
        populateFields(item);
    }

    private void setupNumberPicker() {
        NumberPicker picker = (NumberPicker) findViewById(R.id.numberPicker);
        picker.setMinValue(0);
        picker.setMaxValue(2);
        picker.setDisplayedValues(new String[]{"High", "Medium", "Low"});
    }

    private void populateFields(TodoItem item) {
        EditText etNewItem = (EditText) findViewById(R.id.etEditItem);
        etNewItem.append(item.title);

        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        Calendar date = item.dueDate;
        datePicker.updateDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));

        NumberPicker picker = (NumberPicker) findViewById(R.id.numberPicker);
        picker.setValue(item.priority.ordinal());
    }

    public void onSave(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etEditItem);
        item.title = etNewItem.getText().toString();
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        item.dueDate = getCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        item.priority = Priority.values()[numberPicker.getValue()];

        Intent data = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("item", item);
        data.putExtras(b);
        data.putExtra("code", 10);
        data.putExtra("position", position);
        setResult(RESULT_OK, data);

        this.finish();
    }

    private Calendar getCalendar(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);

        return date;
    }

    public void onDelete(View v) {
        Intent data = new Intent();
        data.putExtra("delete", true);
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
