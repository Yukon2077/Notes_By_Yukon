 package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import activities.EntryActivity;
import activities.WriteActivity;

public class NotesSQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "NotesSQL";
    private static final int DB_VERSION = 1;
    public static final String LIST_OF_ALL_TABLES = "TB_LIST";
    public static final String DEFAULT_TABLE = "Tutorial";

    public NotesSQLiteHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE TB_LIST(_id INTEGER PRIMARY KEY AUTOINCREMENT, TABLE_NAME TEXT UNIQUE, " +
                "CREATED_DATETIME DATETIME, LAST_MODIFIED_DATETIME DATETIME );" );
        addTable(db, DEFAULT_TABLE);
        addEntry(db, DEFAULT_TABLE, "Notes");
        addEntry(db, DEFAULT_TABLE, "An App to write and save notes.\nMade by Yukon.");
        addEntry(db, DEFAULT_TABLE, "In the previous page, click + to add files\nClick the 3 dots to delete/rename files");
        addEntry(db, DEFAULT_TABLE, "In the this page, click + to add entries\nSwipe left or right to delete entries");
        addEntry(db, DEFAULT_TABLE, "You can change Dark Mode and Color in Settings");



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
        db.execSQL("CREATE TABLE IF NOT EXISTS" + "\"" + table_name + "\"" +"("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "CREATED_DATETIME DATETIME, "
                + "LAST_MODIFIED_DATETIME DATETIME, "
                + "ENTRY TEXT);");
        ContentValues contentValues = new ContentValues();
        contentValues.put("TABLE_NAME",table_name);
        contentValues.put("CREATED_DATETIME", getCurrentDateTime());
        contentValues.put("LAST_MODIFIED_DATETIME", getCurrentDateTime());
        db.insert("TB_LIST",null, contentValues);
    }

    public void addEntry(SQLiteDatabase db,
                         String table_name,
                         String entry){
        ContentValues contentValues = new ContentValues();
        contentValues.put("CREATED_DATETIME", getCurrentDateTime());
        contentValues.put("LAST_MODIFIED_DATETIME", getCurrentDateTime());
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
                "_id ASC"); /*Need to have sorting*/
    }

    public Cursor getAllItems(SQLiteDatabase db, String table_name){
        return  db.query("\"" + table_name + "\"",
                null,
                null,
                null,
                null,
                null,
                "_id DESC"); /*Need to have sorting*/

    }

    public void deleteEntry(SQLiteDatabase db, String table_name, Integer id){
        db.delete("\"" + table_name + "\"",
                "_id = ?",
                new String[]{ String.valueOf( id ) });
    }

    public void deleteTable(SQLiteDatabase db, String table_name){
        db.execSQL("DROP TABLE IF EXISTS " + "\"" + table_name + "\";");
        db.delete("TB_LIST",
                "TABLE_NAME = ?",
                new String[]{ table_name });
    }

    public void renameTable(SQLiteDatabase db, String table_name, String new_name){
        db.execSQL("ALTER TABLE " + "\"" + table_name + "\"" + " RENAME TO " + "\"" + new_name + "\"" + ";");
        ContentValues contentValues = new ContentValues();
        contentValues.put("TABLE_NAME",new_name);
        contentValues.put("LAST_MODIFIED_DATETIME", getCurrentDateTime());
        db.update("TB_LIST",contentValues,"TABLE_NAME = ?", new String[]{ table_name });
    }

    public void updateEntry(SQLiteDatabase db, String table_name, Integer id, String entry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ENTRY",entry);
        contentValues.put("LAST_MODIFIED_DATETIME", getCurrentDateTime());
        db.update("\"" + table_name + "\"",contentValues,"_id = ?", new String[]{ String.valueOf(id) } );
    }

    private String getCurrentDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

}
