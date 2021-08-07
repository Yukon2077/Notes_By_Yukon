package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.yukon.notes.R;
import java.util.ArrayList;
import java.util.List;
import adapters.TableAdapter;
import database.NotesSQLiteHelper;

public class MainActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public ArrayAdapter adapter;
    public List<String> StringArray;
    public RecyclerView recyclerView;
    public TableAdapter tableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StringArray = new ArrayList<>();
        notesSQLiteHelper = new NotesSQLiteHelper(this);
        db = notesSQLiteHelper.getWritableDatabase();
        Cursor cursor = notesSQLiteHelper.getAllTables(db);
        if(cursor.moveToFirst()){
            do{
                StringArray.add(cursor.getString(cursor.getColumnIndex("TABLE_NAME")));
            }while (cursor.moveToNext());
        }
        recyclerView = findViewById(R.id.recyclerview);
        tableAdapter = new TableAdapter(StringArray);
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));

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
                addTableDialog();
                return true;
            case R.id.settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String title = String.valueOf(item.getTitle());
        switch (title){
            case "Delete":
                notesSQLiteHelper.deleteTable(db,StringArray.get(tableAdapter.getTablePosition()));
                StringArray.remove(StringArray.get(tableAdapter.getTablePosition()));
                tableAdapter.notifyDataSetChanged();
                return true;
            case "Rename":
                renameTableDialog(StringArray.get(tableAdapter.getTablePosition()));
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void addTableDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New File");
        builder.setMessage("Enter a name");
        EditText input = new EditText(this);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String table_name = String.valueOf(input.getText());
                notesSQLiteHelper.addTable(db, table_name);
                StringArray.add(table_name);
                tableAdapter.notifyDataSetChanged();

            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.show();

    }

    public void renameTableDialog(String table_name){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename File");
        builder.setMessage("Enter a new name");
        EditText input = new EditText(this);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);
        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String new_name = String.valueOf(input.getText());
                if(table_name.toLowerCase().equals(new_name.toLowerCase())){
                    Toast.makeText(getApplicationContext(),"New name can't be same as old name",Toast.LENGTH_SHORT).show();
                    return;
                }
                notesSQLiteHelper.renameTable(db, table_name, new_name);
                StringArray.set(StringArray.indexOf(table_name),new_name);
                adapter.notifyDataSetChanged();

            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.show();

    }


}
