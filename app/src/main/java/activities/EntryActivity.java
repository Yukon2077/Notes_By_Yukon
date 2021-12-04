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
import android.view.Menu;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(entryAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0 ,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                notesSQLiteHelper.deleteEntry(db, CURRENT_TABLE, (Integer) viewHolder.itemView.getTag());
                entryAdapter.swapCursor(notesSQLiteHelper.getAllItems(db, CURRENT_TABLE));
                entryAdapter.removeEntry(viewHolder.getAdapterPosition());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }
        }).attachToRecyclerView(recyclerView);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(v.getContext(), WriteActivity.class);
                startActivity(create);
            }
        });
    }

    @Override
    protected void onRestart() {
        db = notesSQLiteHelper.getReadableDatabase();
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