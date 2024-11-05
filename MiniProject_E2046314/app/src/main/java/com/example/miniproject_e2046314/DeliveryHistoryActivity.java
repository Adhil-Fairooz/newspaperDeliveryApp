package com.example.miniproject_e2046314;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class DeliveryHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_history);
        DatabaseHelper dbhelper = new DatabaseHelper(this);
        ListView lvDelHistory = findViewById(R.id.LVHistory);
        SimpleCursorAdapter SCA = dbhelper.populateListviewFromDB();
        lvDelHistory.setAdapter(SCA);

    }


}