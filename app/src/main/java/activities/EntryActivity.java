package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;
import com.yukon.notes.R;

import java.util.ArrayList;
import java.util.List;

import adapters.EntryAdapter;
import database.NotesSQLiteHelper;

import static database.NotesSQLiteHelper.DEFAULT_TABLE;

public class EntryActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public ActionBar actionBar;
    public RecyclerView recyclerView;
    public EntryAdapter entryAdapter;
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public static String CURRENT_TABLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        entryAdapter = new EntryAdapter(this, notesSQLiteHelper.getAllItems(db, CURRENT_TABLE));
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(entryAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                notesSQLiteHelper.deleteEntry(db, CURRENT_TABLE, (Integer) viewHolder.itemView.getTag());
                entryAdapter.swapCursor(notesSQLiteHelper.getAllItems(db, CURRENT_TABLE));
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void newEntry(View view) {
        Intent newEntry = new Intent(this, WriteActivity.class);
        startActivity(newEntry);
    }

    @Override
    protected void onRestart() {
        db = notesSQLiteHelper.getReadableDatabase();
        entryAdapter.swapCursor(notesSQLiteHelper.getAllItems(db, CURRENT_TABLE));
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.entry_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.create:
                Intent create = new Intent(this, WriteActivity.class);
                startActivity(create);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}