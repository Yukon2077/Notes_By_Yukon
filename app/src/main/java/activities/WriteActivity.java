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
    public String date, time, entry;
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public EditText editText;
    public Integer id;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        id = -1;
        date = getDate();
        time = getTime();
        entry = "";

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        editText = findViewById(R.id.editTextEntry);

        sharedPreferences = this.getSharedPreferences("com.yukon.notes.Last_Entry",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getInt("ID");
            entry = extras.getString("ENTRY");
            editText.setText(entry);
            if (extras.getString("FROM_WHERE").equals("MAIN")){
                entry = "";
            }
        }

    }

    @Override
    protected void onPause() {
        editor.putString("last_entry",editText.getText().toString().trim());
        editor.putInt("last_id",id);
        editor.apply();
        super.onPause();
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
                if (editText.getText().toString().trim().equals(entry)) {
                    editText.setText("");
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

    public void save(){

        if (editText.getText().toString().trim().equals(entry)) {
            editText.setText("");
            finish();
            return;
        }
        notesSQLiteHelper = new NotesSQLiteHelper(this);
        try{
            db = notesSQLiteHelper.getWritableDatabase();
            entry = editText.getText().toString();
            if( id == null || id == -1) {
                notesSQLiteHelper.insertEntry(db, date, time, entry);
            } else{
                ContentValues contentValues = new ContentValues();
                contentValues.put("ENTRY",entry);
                db.update("ENTRIES",contentValues,"_id = ?", new String[]{ String.valueOf(id) } );

            }
            editText.setText("");
            db.close();
            finish();
        }catch (SQLException e){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();

        }

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
        if (editText.getText().toString().trim().equals(entry)) {
            editText.setText("");
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
                editText.setText("");
                finish();
            }
        });
        builder.setCancelable(true);
        builder.show();

    }
}