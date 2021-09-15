 package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

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
        db.execSQL("CREATE TABLE TB_LIST(_id INTEGER PRIMARY KEY AUTOINCREMENT, TABLE_NAME TEXT UNIQUE );" );
        addTable(db, DEFAULT_TABLE);
        addEntry(db, DEFAULT_TABLE, WriteActivity.getDate(), WriteActivity.getTime(),"In the this page, click + to add entries\nSwipe left or right to delete entries");
        addEntry(db, DEFAULT_TABLE, WriteActivity.getDate(), WriteActivity.getTime(),"In the previous page, click + to add files\nHold a file to open context menu to delete files");
        addEntry(db, DEFAULT_TABLE, WriteActivity.getDate(), WriteActivity.getTime(),"An App to write and save notes.\nMade by Yukon.");
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
        db.delete("TB_LIST",
                "TABLE_NAME = ?",
                new String[]{ table_name });

    }

    public void renameTable(SQLiteDatabase db, String table_name, String new_name){
        db.execSQL("ALTER TABLE " + "\"" + table_name + "\"" + " RENAME TO " + "\"" + new_name + "\"" + ";");
        ContentValues contentValues = new ContentValues();
        contentValues.put("TABLE_NAME",new_name);
        db.update("TB_LIST",contentValues,"TABLE_NAME = ?", new String[]{ table_name });
    }

    public void swapEntries(SQLiteDatabase db, String table_name, Integer sourceID, Integer destinationID) {
        String sourceEntry, sourceDate, sourceTime, destinationEntry, destinationDate, destinationTime;
        Cursor sourceCursor = db.query("\"" + table_name + "\"",
                null,
                "_id = ?",
                 new String[]{ String.valueOf(sourceID)},
                null,
                null,
                "_id DESC");
        sourceCursor.moveToFirst();
        sourceDate = sourceCursor.getString(sourceCursor.getColumnIndex("DATE"));
        sourceTime = sourceCursor.getString(sourceCursor.getColumnIndex("TIME"));
        sourceEntry = sourceCursor.getString(sourceCursor.getColumnIndex("ENTRY"));


        Cursor destinationCursor = db.query("\"" + table_name + "\"",
                null,
                "_id = ?",
                 new String[]{ String.valueOf(destinationID)},
                null,
                null,
                "_id DESC");
        destinationCursor.moveToFirst();
        destinationDate = destinationCursor.getString(destinationCursor.getColumnIndex("DATE"));
        destinationTime = destinationCursor.getString(destinationCursor.getColumnIndex("TIME"));
        destinationEntry = destinationCursor.getString(destinationCursor.getColumnIndex("ENTRY"));

        ContentValues sourceContentValues = new ContentValues();
        sourceContentValues.put("ENTRY", sourceEntry);
        sourceContentValues.put("TIME", sourceTime);
        sourceContentValues.put("DATE", sourceDate);

        ContentValues destinationContentValues = new ContentValues();
        destinationContentValues.put("ENTRY", destinationEntry);
        destinationContentValues.put("TIME", destinationTime);
        destinationContentValues.put("DATE", destinationDate);

        db.update("\"" + table_name + "\"", sourceContentValues,"_id = ?", new String[]{ String.valueOf(destinationID) } );
        db.update("\"" + table_name + "\"", destinationContentValues,"_id = ?", new String[]{ String.valueOf(sourceID) } );
    }

    public void swapTables(SQLiteDatabase db, String sourceName, String destinationName) {


        ContentValues sourceContentValues = new ContentValues();
        sourceContentValues.put("TABLE_NAME", sourceName);

        ContentValues tempCV = new ContentValues();
        tempCV.put("TABLE_NAME", "TempContentValue" );

        ContentValues destinationContentValues = new ContentValues();
        destinationContentValues.put("TABLE_NAME", destinationName);

        db.update("TB_LIST", tempCV,"TABLE_NAME = ?", new String[]{destinationName} );
        db.update("TB_LIST", destinationContentValues,"TABLE_NAME = ?", new String[]{sourceName} );
        db.update("TB_LIST", sourceContentValues,"TABLE_NAME = ?", new String[]{ "TempContentValue" } );

    }

}
