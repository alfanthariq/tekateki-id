package com.alfanthariq.tts.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;

/**
 * Created by alfanthariq on 04/01/2018.
 */

public class GameDataHelper extends SQLiteAssetHelper {

    private static final String TAG = "GameDataHelper";
    private static final String DATABASE_NAME = "db001.db";
    private static final int DATABASE_VERSION = 1;


    public GameDataHelper(Context context, String DBName) {
        //super(context, DBName, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);
        super(context, DBName, context.getFilesDir().getAbsolutePath()+File.separator+"tts", null, DATABASE_VERSION);
    }

    public Cursor getItems()
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

            //String [] sqlSelect = {"0 _id", "FirstName", "LastName"};
            String sqlTables = "items";

            qb.setTables(sqlTables);
            Cursor c = qb.query(db, null, null, null,
                    null, null, null);

            c.moveToFirst();
            db.close();
            return c;
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
    }

    public Cursor getItemsById(int id)
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

            //String [] sqlSelect = {"0 _id", "FirstName", "LastName"};
            String sqlTables = "items";

            qb.setTables(sqlTables);
            Cursor c = qb.query(db, null, "_id = ?", new String[] {Integer.toString(id)},
                    null, null, null);

            c.moveToFirst();
            db.close();
            return c;
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
    }

    public Cursor getQuestion(int col, int row, int orientation)
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            String sqlQuery = "SELECT q.quest \n" +
                    "FROM items as i \n" +
                    "LEFT JOIN questions as q ON q._id = i.id_question_hor OR q._id = i.id_question_ver\n" +
                    "WHERE i.row="+Integer.toString(row)+
                    " AND i.col="+Integer.toString(col)+
                    " AND q.orientation="+Integer.toString(orientation);
            Cursor c = db.rawQuery(sqlQuery, null);
            c.moveToFirst();
            db.close();
            return c;
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
    }

    public Cursor getQuestionList(int orientation)
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            String where;
            if (orientation==99) {
                where = "";
            } else {
                where = "WHERE q.orientation="+Integer.toString(orientation);
            }
            String sqlQuery = "SELECT q.*, i.row, i.col, i._id as id_items\n" +
                    "FROM questions as q\n" +
                    "LEFT JOIN items as i ON q._id = i.id_question_hor OR q._id = i.id_question_ver\n"+where+
                    " ORDER BY badge ASC";
            Cursor c = db.rawQuery(sqlQuery, null);
            c.moveToFirst();
            db.close();
            return c;
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
    }

    public int updateValue(String val, int col, int row){
        ContentValues cv = new ContentValues();
        cv.put("text_value",val);
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            int ret = db.update("items", cv, "col = ? AND row = ?", new String[]{Integer.toString(col), Integer.toString(row)});
            db.close();
            return ret;
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
    }

    public Cursor getSettings()
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String sqlTables = "setting";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, null, null, null,
                    null, null, null);
            c.moveToFirst();
            db.close();
            return c;
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
    }

    public int updateSetting(long milis, int is_done){
        ContentValues cv = new ContentValues();
        if (milis>-1) {
            cv.put("milis", milis);
        }
        if (is_done!=99) {
            cv.put("is_done", is_done);
        }
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            int ret = db.update("setting", cv, null, null);
            db.close();
            return ret;
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
    }

    public int getTableCount(){
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            String sqlQuery = "SELECT count(*) as jml FROM sqlite_master";
            Cursor c = db.rawQuery(sqlQuery, null);
            c.moveToFirst();
            db.close();
            return c.getInt(c.getColumnIndex("jml"));
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
    }
}
