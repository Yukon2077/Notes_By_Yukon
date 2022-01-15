 package database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import models.Table;
import util.Utils;

 public class NotesSQLiteHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "NotesSQL";
    public static final String INDEX_TABLE = "TB_LIST";
    public static final String DEFAULT_TABLE = "Tutorial";
    public static final String COL_ID = "_id";
    public static final String COL_TABLE_NAME = "TABLE_NAME";
    public static final String COL_CREATED = "CREATED_DATETIME";
    public static final String COL_LAST_MODIFIED = "LAST_MODIFIED_DATETIME";
    public static final String COL_ENTRY = "ENTRY";

    public NotesSQLiteHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +
                INDEX_TABLE + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TABLE_NAME + " TEXT UNIQUE, " +
                COL_CREATED + " DATETIME, " +
                COL_LAST_MODIFIED + " DATETIME );" );
        addTable(db, DEFAULT_TABLE);
        addEntry(db, DEFAULT_TABLE, "Notes");
        addEntry(db, DEFAULT_TABLE, "An App to write and save notes.\nMade by Yukon.");
        addEntry(db, DEFAULT_TABLE, "In the previous page, click + to add files\nClick the 3 dots to delete/rename files");
        addEntry(db, DEFAULT_TABLE, "In the this page, click + to add entries\nSwipe left or right to delete entries");
        addEntry(db, DEFAULT_TABLE, "You can change Dark Mode and Color in Settings");
    }

    @SuppressLint("Range")
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cursor = getAllTables(db);
        if(cursor.moveToFirst()){
            do{
                deleteTable(db, cursor.getString(cursor.getColumnIndex(COL_TABLE_NAME)));
            }while (cursor.moveToNext());
        }
        db.execSQL("DROP TABLE IF EXISTS " + INDEX_TABLE + ";");
        deleteTable(db, INDEX_TABLE);
        onCreate(db);
    }

    public String addTable(SQLiteDatabase db, String table_name){
        List<String> tableNames = getTableNames(db);

        for (int i = 0; i < tableNames.size(); i++) {
            if(tableNames.get(i).equalsIgnoreCase(table_name)) {
                return "Table already exists";
            }
        }
        db.execSQL("CREATE TABLE IF NOT EXISTS" + "\"" + table_name + "\"" +"("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CREATED + " DATETIME, "
                + COL_LAST_MODIFIED + " DATETIME, "
                + COL_ENTRY + " TEXT);");
        String date = Utils.getCurrentDateTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TABLE_NAME,table_name);
        contentValues.put(COL_CREATED, date);
        contentValues.put(COL_LAST_MODIFIED, date);
        db.insert(INDEX_TABLE,null, contentValues);
        return "OK";
    }

    public void addEntry(SQLiteDatabase db, String table_name, String entry){
        String date = Utils.getCurrentDateTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CREATED, date);
        contentValues.put(COL_LAST_MODIFIED, date);
        contentValues.put(COL_ENTRY, entry);
        db.insert("\"" + table_name + "\"",null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(COL_LAST_MODIFIED, date);
        db.update(INDEX_TABLE, contentValues,COL_TABLE_NAME + " = ?", new String[]{ table_name });
    }

    public Cursor getAllTables(SQLiteDatabase db) {
        return db.query(INDEX_TABLE,
                null,
                null,
                null,
                null,
                null,
                COL_LAST_MODIFIED + " DESC"); /* Need to have sorting for created_date_time */
    }

    public Cursor getAllItems(SQLiteDatabase db, String table_name) {
        return  db.query("\"" + table_name + "\"",
                null,
                null,
                null,
                null,
                null,
                COL_LAST_MODIFIED + " DESC"); /* Need to have sorting for created_date_time */
    }

    @SuppressLint("Range")
    public String getEntry(SQLiteDatabase db, String table_name, int id) {
        Cursor cursor = db.query("\"" + table_name + "\"",
                null,
                COL_ID + " = ?",
                new String[] {String.valueOf(id)},
                null,
                null,
                COL_ID + " ASC");
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COL_ENTRY));
    }

    public void deleteEntry(SQLiteDatabase db, String table_name, Integer id){
        db.delete("\"" + table_name + "\"",
                COL_ID + " = ?",
                new String[]{ String.valueOf( id ) });
        String date = Utils.getCurrentDateTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_LAST_MODIFIED, date);
        db.update(INDEX_TABLE, contentValues, COL_TABLE_NAME + " = ?", new String[]{ table_name });
    }

    public void deleteTable(SQLiteDatabase db, String table_name){
        db.execSQL("DROP TABLE IF EXISTS " + "\"" + table_name + "\";");
        db.delete(INDEX_TABLE,
                COL_TABLE_NAME + " = ?",
                new String[]{ table_name });
    }

    public void renameTable(SQLiteDatabase db, String table_name, String new_name){
        db.execSQL("ALTER TABLE " + "\"" + table_name + "\"" + " RENAME TO " + "\"" + new_name + "\"" + ";");
        String date = Utils.getCurrentDateTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TABLE_NAME,new_name);
        contentValues.put(COL_LAST_MODIFIED, date);
        db.update(INDEX_TABLE, contentValues,COL_TABLE_NAME + " = ?", new String[]{ table_name });
    }

    public void updateEntry(SQLiteDatabase db, String table_name, Integer id, String entry) {
        String date = Utils.getCurrentDateTime();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ENTRY,entry);
        contentValues.put(COL_LAST_MODIFIED, date);
        db.update("\"" + table_name + "\"", contentValues,COL_ID + " = ?", new String[]{ String.valueOf(id) } );


        contentValues = new ContentValues();
        contentValues.put(COL_LAST_MODIFIED, date);
        db.update(INDEX_TABLE, contentValues,COL_TABLE_NAME + " = ?", new String[]{ table_name });
    }

     @SuppressLint("Range")
     public List<String> getTableNames(SQLiteDatabase db) {
         List<String> tableList = new ArrayList<>();
         Cursor cursor = this.getAllTables(db);
         if (cursor.moveToFirst()) {
             do {
                 String table_name = cursor.getString(cursor.getColumnIndex(NotesSQLiteHelper.COL_TABLE_NAME));
                 tableList.add(table_name);
             } while (cursor.moveToNext());
         }
         return  tableList;
     }

 }
