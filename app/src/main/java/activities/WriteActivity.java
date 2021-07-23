package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.yukon.notes.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import database.NotesSQLiteHelper;

public class WriteActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public String date, time, entry = "";
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public EditText editText;
    public Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        date = getDate();
        time = getTime();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        editText = findViewById(R.id.editTextEntry);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getInt("ID");
            entry = extras.getString("ENTRY");
            editText.setText(entry);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_menu,menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.save:
                save();
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
        notesSQLiteHelper = new NotesSQLiteHelper(this);
            db = notesSQLiteHelper.getWritableDatabase();
            entry = editText.getText().toString();
            if( id == null ) {
                notesSQLiteHelper.addEntry(db, MainActivity.CURRENT_TABLE, date, time, entry);
            } else{
                ContentValues contentValues = new ContentValues();
                contentValues.put("ENTRY",entry);
                db.update(MainActivity.CURRENT_TABLE,contentValues,"_id = ?", new String[]{ String.valueOf(id) } );
            }
            db.close();
            finish();
    }

    public static String getTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());
        Date time = new Date();
        return timeFormat.format(time);
    }

    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
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