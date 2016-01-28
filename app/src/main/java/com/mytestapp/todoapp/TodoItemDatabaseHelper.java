package com.mytestapp.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhanikshah on 1/26/16.
 */
public class TodoItemDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "ToDoListDatabaseHelper";
    private static final String DATABASE_NAME = "ToDoListDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ITEM = "item";

    private static final String KEY_ITEM_ID = "_id";
    private static final String KEY_ITEM_TEXT = "text";

    private static TodoItemDatabaseHelper sInstance;

    public static synchronized TodoItemDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodoItemDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private TodoItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_ITEM +
                "(" +
                KEY_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_ITEM_TEXT + " TEXT" +
                ")";

        db.execSQL(CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
            onCreate(db);
        }
    }

    public void addItem(ToDoItem item) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_TEXT, item.text);
            db.insertOrThrow(TABLE_ITEM, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add ToDoItem to database");
        } finally {
            db.endTransaction();
        }
    }

    public String getItemId(ToDoItem item) {
        SQLiteDatabase db = getWritableDatabase();
        String id = "";

        String itemSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                KEY_ITEM_ID, TABLE_ITEM, KEY_ITEM_TEXT);
        Cursor cursor = db.rawQuery(itemSelectQuery, new String[]{String.valueOf(item.text)});
        try {
            if (cursor.moveToFirst()) {
                id = cursor.getString(cursor.getColumnIndex(KEY_ITEM_ID));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return id;
    }

    public List<ToDoItem> getAllToDoItems() {
        List<ToDoItem> items = new ArrayList<>();

        // SELECT * FROM ITEMS
        String ITEMS_SELECT_QUERY =
                String.format("SELECT * FROM %s ",
                        TABLE_ITEM);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ITEMS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    ToDoItem newItem = new ToDoItem();
                    newItem.text = cursor.getString(cursor.getColumnIndex(KEY_ITEM_TEXT));
                    items.add(newItem);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }

    public int updateItem(ToDoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_TEXT, item.text);

        return db.update(TABLE_ITEM, values, KEY_ITEM_ID + " = ?",
                new String[]{String.valueOf(item.id)});
    }

    public int deleteItem(ToDoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_ID, item.id);

        return db.delete(TABLE_ITEM, KEY_ITEM_ID + " = ?",
                new String[]{String.valueOf(item.id)});
    }

    public void deleteAllItems() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_ITEM, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all items");
        } finally {
            db.endTransaction();
        }
    }
}
