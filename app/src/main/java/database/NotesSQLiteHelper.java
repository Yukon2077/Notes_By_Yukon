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
    public static final String DEFAULT_TABLE = "ENTRIES";

    public NotesSQLiteHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE TB_LIST(_id INTEGER PRIMARY KEY AUTOINCREMENT, TABLE_NAME TEXT);" );
        addTable(db, DEFAULT_TABLE);
        addEntry(db, DEFAULT_TABLE, WriteActivity.getDate(), WriteActivity.getTime(),"Swipe left or right to delete entries");
        addEntry(db, DEFAULT_TABLE, WriteActivity.getDate(), WriteActivity.getTime(),"That's it, I guess.");
        addEntry(db, DEFAULT_TABLE, WriteActivity.getDate(), WriteActivity.getTime(),"An App to write and save notes for later.");
        addEntry(db, DEFAULT_TABLE, WriteActivity.getDate(), WriteActivity.getTime(),"Notes");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cursor = getAllTables(db);
        if(cursor.moveToFirst()){
            do{
                deleteTable(db, cursor.getString(cursor.getColumnIndex("TABLE_NAME")));
            }while (cursor.moveToNext());
        }
        db.execSQL("DROP TABLE IF EXISTS TB_LIST");
        deleteTable(db, "TB_LIST");
        onCreate(db);
    }

    public void addTable(SQLiteDatabase db, String table_name){
        db.execSQL("CREATE TABLE " + "\"" + table_name + "\"" +"("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "DATE DATE, "
                + "TIME TIME, "
                + "ENTRY TEXT);");
        ContentValues contentValues = new ContentValues();
        contentValues.put("TABLE_NAME",table_name);
        db.insert("TB_LIST",null, contentValues);
    }

    public void addEntry(SQLiteDatabase db,
                         String table_name,
                         String date,
                         String time,
                         String entry){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DATE",date);
        contentValues.put("TIME",time);
        contentValues.put("ENTRY",entry);
        db.insert("\"" + table_name + "\"",null, contentValues);
    }

    public Cursor getAllTables(SQLiteDatabase db){
        return db.query("TB_LIST",
                null,
                null,
                null,
                null,
                null,
                "_id ASC");
    }

    public Cursor getAllItems(SQLiteDatabase db, String table_name){
        return  db.query("\"" + table_name + "\"",
                null,
                null,
                null,
                null,
                null,
                "_id DESC");

    }

    public void deleteEntry(SQLiteDatabase db, String table_name, Integer id){
        db.delete("\"" + table_name + "\"",
                "_id = ?",
                new String[]{ String.valueOf( id ) });
    }

    public void deleteTable(SQLiteDatabase db, String table_name){
        db.execSQL("DROP TABLE IF EXISTS " + "\"" + table_name + "\"");

    }

}
