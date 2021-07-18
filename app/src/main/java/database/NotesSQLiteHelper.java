package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import activities.WriteActivity;

public class NotesSQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "NotesSQL";
    private static final int DB_VERSION = 1;

    public NotesSQLiteHelper(@Nullable Context context) {
        
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ENTRIES ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "DATE DATE, "
                + "TIME TIME, "
                + "ENTRY TEXT);");
        /*insertEntry(db, WriteActivity.getDate(), WriteActivity.getTime(),"Notes\n\n" +
                "An App to write and save notes for later.\n" +
                "");
        insertEntry(db, WriteActivity.getDate(), WriteActivity.getTime(),"Swipe left or right to delete entries");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);

    }

    public void insertEntry(SQLiteDatabase db,
                            String date,
                            String time,
                            String entry){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DATE",date);
        contentValues.put("TIME",time);
        contentValues.put("ENTRY",entry);
        db.insert("ENTRIES",null, contentValues);
    }


    public Cursor getAllItems(){
        SQLiteDatabase db = getReadableDatabase();
        return db.query("ENTRIES",null,null,null,null,null, "_ID DESC");
    }
}
