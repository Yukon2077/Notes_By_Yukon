package activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yukon.notes.R;

import java.util.ArrayList;
import java.util.List;

import adapters.EntryAdapter;
import database.NotesSQLiteHelper;
import models.Entry;
import models.Table;
import util.Utils;

public class EntryActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public ActionBar actionBar;
    public RecyclerView recyclerView;
    public EntryAdapter entryAdapter;
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public static String CURRENT_TABLE;
    public List<Entry> entryList;
    public FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setThemeColor(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String tb_name = extras.getString("TB_NAME");
            actionBar.setTitle(tb_name);
            CURRENT_TABLE = tb_name;

        }

        notesSQLiteHelper = new NotesSQLiteHelper(this);
        db = notesSQLiteHelper.getWritableDatabase();

        entryAdapter = new EntryAdapter(getAdapterData());
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(entryAdapter);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(v.getContext(), WriteActivity.class);
                startActivity(create);
            }
        });

    }

    @SuppressLint("Range")
    public List<Entry> getAdapterData() {
        entryList = new ArrayList<>();
        Cursor cursor = notesSQLiteHelper.getAllEntries(db, CURRENT_TABLE);
        if (cursor.moveToFirst()) {
            do {
                Integer id = cursor.getInt(cursor.getColumnIndex(NotesSQLiteHelper.COL_ID));
                String entry_text = cursor.getString(cursor.getColumnIndex(NotesSQLiteHelper.COL_ENTRY));
                String created_datetime = cursor.getString(cursor.getColumnIndex(NotesSQLiteHelper.COL_CREATED));
                String last_modified_datetime = cursor.getString(cursor.getColumnIndex(NotesSQLiteHelper.COL_LAST_MODIFIED));
                Entry entry = new Entry(id, entry_text, created_datetime, last_modified_datetime);
                entryList.add(entry);
            } while (cursor.moveToNext());
        }
        return entryList;
    }

    @Override
    protected void onRestart() {
        entryAdapter.updateEntryList(getAdapterData());
        super.onRestart();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}