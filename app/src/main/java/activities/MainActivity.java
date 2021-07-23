package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.yukon.notes.R;

import adapters.EntryAdapter;
import database.NotesSQLiteHelper;

public class MainActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public RecyclerView recyclerView;
    public EntryAdapter entryAdapter;
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public static String CURRENT_TABLE = "ENTRIES";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                notesSQLiteHelper.deleteEntry(db, (Integer) viewHolder.itemView.getTag());
                entryAdapter.swapCursor(notesSQLiteHelper.getAllItems(db, CURRENT_TABLE));
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.create:
                Intent create = new Intent(this, WriteActivity.class);
                startActivity(create);
                return true;
            case R.id.settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        db = notesSQLiteHelper.getReadableDatabase();
        entryAdapter.swapCursor(notesSQLiteHelper.getAllItems(db, CURRENT_TABLE));
        super.onRestart();
    }

    public void setAppTheme(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme_list","System Default");
        switch (theme) {
            case "Light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "Dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "System Default":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}