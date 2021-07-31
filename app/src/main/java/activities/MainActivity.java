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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Toolbar toolbar;
    public ActionBar actionBar;
    public RecyclerView recyclerView;
    public EntryAdapter entryAdapter;
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public static String CURRENT_TABLE = DEFAULT_TABLE;
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public Menu navMenu;
    public Integer i;
    public List<String> tables = new ArrayList<>();
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor spEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        sharedPreferences = getSharedPreferences("com.yukon.notes.sp",MODE_PRIVATE);
        spEdit = sharedPreferences.edit();
        String title = sharedPreferences.getString("LAST_TABLE",DEFAULT_TABLE);
        Integer j = sharedPreferences.getInt("LAST_ID",1);
        actionBar.setTitle(title);
        CURRENT_TABLE = title;


        notesSQLiteHelper = new NotesSQLiteHelper(this);
        db = notesSQLiteHelper.getWritableDatabase();
        Cursor cursor = notesSQLiteHelper.getAllTables(db);
        if(cursor.moveToFirst()){
            do{
                tables.add(cursor.getString(cursor.getColumnIndex("TABLE_NAME")));
            }while (cursor.moveToNext());
        }

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

        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        navMenu = navigationView.getMenu();
        for (i = 0; i < tables.size(); i++)
        {
            String text = tables.get(i);
            navMenu.add(R.id.group, i,1,text);
        }
        navMenu.setGroupCheckable(R.id.group,true,true);
        navMenu.findItem(j).setChecked(true);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
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

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
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
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_add:
                addTableDialog(this);
                break;
            default:
                if(item!=null){
                    String text = String.valueOf(item.getTitle());
                    CURRENT_TABLE = text;
                    actionBar.setTitle(text);
                    db = notesSQLiteHelper.getReadableDatabase();
                    entryAdapter.swapCursor(notesSQLiteHelper.getAllItems(db, CURRENT_TABLE));
                    spEdit.putString("LAST_TABLE",text);
                    spEdit.putInt("LAST_ID",item.getItemId());
                    spEdit.apply();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
        }
        return true;
    }

    public void addTableDialog(Context context){
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
                i = i + 1;
                navMenu.add(R.id.group, i,1,table_name);
                navMenu.setGroupCheckable(R.id.group,true,true);

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