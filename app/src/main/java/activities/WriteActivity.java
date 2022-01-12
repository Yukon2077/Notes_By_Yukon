package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yukon.notes.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import database.NotesSQLiteHelper;
import util.Utils;

public class WriteActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public String entry = "";
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public EditText editText;
    public Integer id;
    public FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setThemeColor(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        editText = findViewById(R.id.editTextEntry);

        notesSQLiteHelper = new NotesSQLiteHelper(this);
        db = notesSQLiteHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getInt("ID");
            entry = notesSQLiteHelper.getEntry(db, EntryActivity.CURRENT_TABLE, id);
            editText.setText(entry);
        }

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (!savable()) {
                    finish();
                } else {
                    warnNotSaved();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public Boolean savable(){
        String text = editText.getText().toString().trim();
        if (text.equals("") || text.equals(entry)) {
            return false;
        }
        return true;
    }

    public void save(){

        if (!savable()) {
            finish();
            return;
        }
        entry = editText.getText().toString().trim();
        if( id == null ) {
            notesSQLiteHelper.addEntry(db, EntryActivity.CURRENT_TABLE, entry);
        } else{
            notesSQLiteHelper.updateEntry( db, EntryActivity.CURRENT_TABLE, id, entry);
        }
        db.close();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!savable()) {
            super.onBackPressed();
        } else {
            warnNotSaved();
        }
    }

    public void warnNotSaved(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save & Exit");
        builder.setMessage("Do you want to save?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(true);
        builder.show();

    }
}