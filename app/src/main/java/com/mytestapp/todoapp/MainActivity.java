package com.mytestapp.todoapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EditItemDialogFragment.EditItemDialogListener {
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    TodoItemDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        databaseHelper = TodoItemDatabaseHelper.getInstance(this);
        readItems();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onFinishEditDialog(String text, int pos, String id) {
        if (pos < 0) {
            return;
        }

        ToDoItem newItem = new ToDoItem();
        newItem.text = text;
        newItem.id = id;
        if (text.isEmpty()) {
            databaseHelper.deleteItem(newItem);
        } else {
            databaseHelper.updateItem(newItem);
        }

        items.remove(pos);
        if (!text.isEmpty()) {
            items.add(pos, text);
        }
        itemsAdapter.notifyDataSetChanged();
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        ToDoItem newItem = new ToDoItem();
                        newItem.text = items.get(pos).toString();
                        newItem.id = databaseHelper.getItemId(newItem);
                        databaseHelper.deleteItem(newItem);

                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();

                        return true;
                    }
                });
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        ToDoItem newItem = new ToDoItem();
                        newItem.text = items.get(pos).toString();

                        FragmentManager fm = getSupportFragmentManager();
                        EditItemDialogFragment editDialog = EditItemDialogFragment.newInstance(adapter.getItemAtPosition(pos).toString(),
                                pos, databaseHelper.getItemId(newItem));
                        editDialog.show(fm, "fragment_edit");
                    }
                });
    }

    // Read Items from SQLite Database
    private void readItems() {
        List<ToDoItem> dbItemList = databaseHelper.getAllToDoItems();
        items = new ArrayList<String>();
        for (ToDoItem dbItem : dbItemList) {
            items.add(dbItem.text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        if (!itemText.isEmpty()) {
            itemsAdapter.add(itemText);
            etNewItem.setText("");
            ToDoItem newItem = new ToDoItem();
            newItem.text = itemText;
            databaseHelper.addItem(newItem);
        }
    }
}
