package com.mytestapp.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {
    private int pos;
    private String id;
    private final int RESULT_OK = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        String itemText = getIntent().getStringExtra("text");
        pos = getIntent().getIntExtra("position", -2);
        id = getIntent().getStringExtra("id");
        EditText etEditedText = (EditText) findViewById(R.id.etEditItem);
        etEditedText.setText(itemText);
        etEditedText.setSelection(etEditedText.getText().length());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onSaveItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etEditItem);
        String itemText = etNewItem.getText().toString();
        Intent data = new Intent();
        data.putExtra("text", itemText.toString());
        data.putExtra("position", pos);
        data.putExtra("id", id);
        setResult(RESULT_OK, data);
        finish();
    }
}
