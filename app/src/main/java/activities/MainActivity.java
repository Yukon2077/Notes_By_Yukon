package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme(this);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notesSQLiteHelper = new NotesSQLiteHelper(this);
        entryAdapter = new EntryAdapter(this, notesSQLiteHelper.getAllItems());
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
        entryAdapter.swapCursor(notesSQLiteHelper.getAllItems());
        setAppTheme(this);
        super.onRestart();
    }

    public void removeItem(Integer id){
        SQLiteDatabase db = notesSQLiteHelper.getWritableDatabase();
        db.delete("ENTRIES","_id = ?", new String[]{String.valueOf(id)});
        entryAdapter.swapCursor(notesSQLiteHelper.getAllItems());
    }

    public static void setAppTheme(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean theme = sharedPreferences.getBoolean("theme_checkbox",false);
        /*Toast.makeText(getApplicationContext(), theme + "",
                Toast.LENGTH_LONG).show();*/

        if (theme) {
            context.setTheme(R.style.Theme_Notes_Dark);

        } else {
            context.setTheme(R.style.Theme_Notes_Light);
        }


    }

}