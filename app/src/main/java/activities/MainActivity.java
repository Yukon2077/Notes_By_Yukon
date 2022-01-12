package activities;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yukon.notes.R;

import adapters.TableAdapter;
import database.NotesSQLiteHelper;
import util.Utils;

public class MainActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public Cursor cursor;
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

        cursor = notesSQLiteHelper.getAllTables(db);
        recyclerView = findViewById(R.id.recyclerview);
        tableAdapter = new TableAdapter(cursor);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        cursor = notesSQLiteHelper.getAllTables(db);
        tableAdapter.updateData(cursor);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new document");
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
                // Needs Exception handling
                notesSQLiteHelper.addTable(db, table_name);
                cursor = notesSQLiteHelper.getAllTables(db);
                tableAdapter.updateData(cursor);
                tableAdapter.notifyItemInserted(cursor.getCount());

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
