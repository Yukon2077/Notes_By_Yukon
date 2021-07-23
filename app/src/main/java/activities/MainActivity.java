package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.yukon.notes.R;

import adapters.EntryAdapter;
import database.NotesSQLiteHelper;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    EntryAdapter entryAdapter;
    NotesSQLiteHelper notesSQLiteHelper;
    String entry;
    Integer id;
    public static String CURRENT_TABLE="ENTRIES";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NotesSQLiteHelper.TB_NAME = "Hello";

        notesSQLiteHelper = new NotesSQLiteHelper(this);
        entryAdapter = new EntryAdapter(this, notesSQLiteHelper.getAllItems(CURRENT_TABLE));
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
                removeItem((Integer) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.yukon.notes.Last_Entry",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        entry = sharedPreferences.getString("last_entry","");
        id = sharedPreferences.getInt("last_id",-1);
        if( !entry.equals("")){
            Intent intent = new Intent(this,WriteActivity.class);
            intent.putExtra("ENTRY",entry);
            intent.putExtra("ID",id);
            intent.putExtra("FROM_WHERE","MAIN");
            startActivity(intent);
        }

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
        entryAdapter.swapCursor(notesSQLiteHelper.getAllItems(CURRENT_TABLE));
        super.onRestart();
    }

    public void removeItem(Integer id){
        SQLiteDatabase db = notesSQLiteHelper.getWritableDatabase();
        db.delete("ENTRIES","_id = ?", new String[]{String.valueOf(id)});
        entryAdapter.swapCursor(notesSQLiteHelper.getAllItems(CURRENT_TABLE));
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