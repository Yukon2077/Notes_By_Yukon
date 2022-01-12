package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yukon.notes.R;

import java.util.ArrayList;
import java.util.List;

import adapters.EntryAdapter;
import database.NotesSQLiteHelper;
import util.Utils;

public class EntryActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public ActionBar actionBar;
    public RecyclerView recyclerView;
    public EntryAdapter entryAdapter;
    public NotesSQLiteHelper notesSQLiteHelper;
    public SQLiteDatabase db;
    public static String CURRENT_TABLE;
    public FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setThemeColor(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String tb_name = extras.getString("TB_NAME");
            actionBar.setTitle(tb_name);
            CURRENT_TABLE = tb_name;

        }

        notesSQLiteHelper = new NotesSQLiteHelper(this);
        db = notesSQLiteHelper.getWritableDatabase();

        entryAdapter = new EntryAdapter(this, notesSQLiteHelper.getAllItems(db, CURRENT_TABLE));
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(entryAdapter);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(v.getContext(), WriteActivity.class);
                startActivity(create);
            }
        });
        registerForContextMenu(recyclerView);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                entryAdapter.edit();
                return true;
            case R.id.delete:
                entryAdapter.delete(this, CURRENT_TABLE);
                return true;
        }
        return false;
    }

    @Override
    protected void onRestart() {
        entryAdapter.swapCursor(notesSQLiteHelper.getAllItems(db, CURRENT_TABLE));
        entryAdapter.notifyDataSetChanged();
        super.onRestart();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}