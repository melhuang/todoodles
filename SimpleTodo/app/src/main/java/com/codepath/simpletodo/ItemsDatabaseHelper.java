package com.codepath.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by melissahuang on 6/26/16.
 */
public class ItemsDatabaseHelper extends SQLiteOpenHelper {
    // Singleton
    private static ItemsDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "itemsDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "ERROR";

    // Table Names
    private static final String TABLE_ITEMS = "items";

    // Istem Table Columns
    private static final String KEY_ITEM_TITLE = "title";
    private static final String KEY_ITEM_DUE_DATE = "dueDate";
    private static final String KEY_ITEM_PRIORITY = "priority";
    private static final String KEY_ITEM_COMPLETED = "completed";

    public static synchronized ItemsDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new ItemsDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private ItemsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS +
                "(" +
                KEY_ITEM_TITLE + " TEXT," +
                KEY_ITEM_DUE_DATE + " INTEGER," +
                KEY_ITEM_PRIORITY + " TEXT," +
                KEY_ITEM_COMPLETED + " INTEGER" +
                ")";

        db.execSQL(CREATE_ITEMS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
            onCreate(db);
        }
    }

    // Insert or update an item in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // user already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public long addOrUpdateItem(TodoItem item) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long itemId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_TITLE, item.title);
            values.put(KEY_ITEM_DUE_DATE, item.dueDate.getTimeInMillis());
            values.put(KEY_ITEM_PRIORITY, item.priority.toString());
            values.put(KEY_ITEM_COMPLETED, item.completed);

            // First try to update the item in case the item already exists in the database
            // This assumes item titles are unique
            int rows = db.update(TABLE_ITEMS, values, KEY_ITEM_TITLE + "= ?", new String[]{item.title});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String itemSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_ITEM_TITLE, TABLE_ITEMS, item.title);
                Cursor cursor = db.rawQuery(itemSelectQuery, new String[]{String.valueOf(item.title)});
                try {
                    if (cursor.moveToFirst()) {
                        itemId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // item with this title did not already exist, so insert new item
                itemId = db.insertOrThrow(TABLE_ITEMS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update item");
        } finally {
            db.endTransaction();
        }
        return itemId;
    }

    public ArrayList<TodoItem> getAllItems() {
        ArrayList<TodoItem> items = new ArrayList<TodoItem>();

        // SELECT * FROM ITEMS
        String ITEMS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_ITEMS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ITEMS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String dateString = cursor.getString(cursor.getColumnIndex(KEY_ITEM_DUE_DATE));
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(KEY_ITEM_DUE_DATE)));
                    TodoItem newItem = new TodoItem(
                            cursor.getString(cursor.getColumnIndex(KEY_ITEM_TITLE)),
                            date,
                            Priority.valueOf(cursor.getString(cursor.getColumnIndex(KEY_ITEM_PRIORITY))),
                            Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(KEY_ITEM_COMPLETED))));

                    items.add(newItem);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get items from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }

    public void deleteAllItems() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_ITEMS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all items");
        } finally {
            db.endTransaction();
        }
    }

    public boolean deleteItem(String title) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_ITEMS, KEY_ITEM_TITLE + "=" + title, null) > 0;
    }
}
