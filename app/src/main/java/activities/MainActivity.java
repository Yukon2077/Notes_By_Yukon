package activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yukon.notes.R;

import java.util.ArrayList;
import java.util.List;

import adapters.TableAdapter;
import database.NotesSQLiteHelper;
import models.Table;
import util.Utils;

public class MainActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public List<Table> tableList;
    public RecyclerView recyclerView;
    public TableAdapter tableAdapter;
    public FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setDarkTheme(this);
        Utils.setThemeColor(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notesSQLiteHelper = new NotesSQLiteHelper(this);
        db = notesSQLiteHelper.getWritableDatabase();


        recyclerView = findViewById(R.id.recyclerview);
        tableAdapter = new TableAdapter(getAdapterData());
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTableDialog();
            }
        });
    }

    @SuppressLint("Range")
    public List<Table> getAdapterData() {
        tableList = new ArrayList<>();
        Cursor cursor = notesSQLiteHelper.getAllTables(db);
        if (cursor.moveToFirst()) {
            do {
                Integer id = cursor.getInt(cursor.getColumnIndex(NotesSQLiteHelper.COL_ID));
                String table_name = cursor.getString(cursor.getColumnIndex(NotesSQLiteHelper.COL_TABLE_NAME));
                String created_datetime = cursor.getString(cursor.getColumnIndex(NotesSQLiteHelper.COL_CREATED));
                String last_modified_datetime = cursor.getString(cursor.getColumnIndex(NotesSQLiteHelper.COL_LAST_MODIFIED));
                Table table = new Table(id, table_name, created_datetime, last_modified_datetime);
                tableList.add(table);
            } while (cursor.moveToNext());
        }
        return  tableList;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        tableAdapter.updateTableList(getAdapterData());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addTableDialog(){
        Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("New document");
        builder.setMessage("Enter name");
        EditText input = new EditText(context);
        FrameLayout container = new FrameLayout(context);
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
                if (table_name.contains("\"") | table_name.contains("'")) {
                    Toast.makeText(context, "Document name can't contain quotes", Toast.LENGTH_SHORT).show();
                    return;
                }
                String status = notesSQLiteHelper.addTable(db, table_name);
                if (status.equals("OK")) {
                    tableAdapter.addNewTable(table_name);
                } else {
                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                }


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
