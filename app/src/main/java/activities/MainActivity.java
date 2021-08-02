package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yukon.notes.R;

import java.util.ArrayList;
import java.util.List;

import database.NotesSQLiteHelper;

public class MainActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public ListView listView;
    public ArrayAdapter adapter;
    public List<String> StringArray;
    public String selectedWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.recyclerview);
        StringArray = new ArrayList<>();
        notesSQLiteHelper = new NotesSQLiteHelper(this);
        db = notesSQLiteHelper.getWritableDatabase();
        Cursor cursor = notesSQLiteHelper.getAllTables(db);
        if(cursor.moveToFirst()){
            do{
                StringArray.add(cursor.getString(cursor.getColumnIndex("TABLE_NAME")));
            }while (cursor.moveToNext());
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,StringArray);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
                intent.putExtra("TB_NAME",StringArray.get(position));
                startActivity(intent);
            }
        });
        registerForContextMenu(listView);

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedWord = ((TextView) info.targetView).getText().toString();
        menu.setHeaderTitle(selectedWord);
        menu.setHeaderIcon(R.drawable.ic_menu);
        menu.add("Delete");
        /*menu.add("Rename");*/
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String title = String.valueOf(item.getTitle());
        switch (title){
            case "Delete":
                notesSQLiteHelper.deleteTable(db,selectedWord);
                StringArray.remove(selectedWord);
                adapter.notifyDataSetChanged();
                return true;
            case "Rename":
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
