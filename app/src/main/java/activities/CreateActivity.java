package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class CreateActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public String date,time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        date = getDate();
        time = getTime();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                    save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void save(){
        NotesSQLiteHelper notesSQLiteHelper = new NotesSQLiteHelper(this);
        try{
            SQLiteDatabase db = notesSQLiteHelper.getWritableDatabase();
            EditText editText = findViewById(R.id.editTextEntry);
            if (editText.getText().toString().trim().equals("")){
                return;
            }
            String entry = editText.getText().toString();
            notesSQLiteHelper.insertEntry(db,date,time,entry);
            db.close();
            finish();

        }catch (SQLException e){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();

        }

    }
    private String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}