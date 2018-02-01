package com.alfanthariq.tekteksil.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alfanthariq on 13/01/2018.
 */

public class GameSettingHelper extends SQLiteAssetHelper {

    private static final String TAG = "GameSettingHelper";
    private static final String DATABASE_NAME = "settings.db";
    private static final int DATABASE_VERSION = 4;


    public GameSettingHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(); // replace db lama
    }

    public Cursor getAvailable()
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String sqlTables = "available_tts";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, null, "is_downloaded = ?", new String[] {Integer.toString(0)},
                    null, null, null);
            c.moveToFirst();
            db.close();
            return c;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getAvailable >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getDownloaded()
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String sqlTables = "available_tts";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, null, "is_downloaded = ?", new String[] {Integer.toString(1)},
                    null, null, null);
            c.moveToFirst();
            db.close();
            return c;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getAvailable >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public String getLastTglTerbit()
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String sqlTables = "available_tts";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, null, null, null,
                    null, null, "tgl_terbit DESC");
            c.moveToFirst();
            String tgl;
            if (c.getCount()>0) {
                tgl = c.getString(c.getColumnIndex("tgl_terbit"));
            } else {
                tgl = "2005-01-01";
            }
            db.close();
            return tgl;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getAvailable >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public long addAvailableTTS(int ed_int, String ed_str, String tgl_terbit, String db_name){
        ContentValues cv = new ContentValues();
        cv.put("edition_int", ed_int);
        cv.put("edition_str", ed_str);
        cv.put("tgl_terbit", tgl_terbit);
        cv.put("is_downloaded", 0);
        cv.put("db_name", db_name);
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            long ret = db.insert("available_tts", null, cv);
            db.close();
            return ret;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "addAvailableTTS >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public int deleteTTS(int id){
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            int ret = db.delete("available_tts", "_id = ?", new String[]{Integer.toString(id)});
            db.close();
            return ret;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "deleteTTS >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public int setKirim(int id){
        ContentValues cv = new ContentValues();
        cv.put("is_sent",1);
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            int ret = db.update("available_tts", cv, "_id = ?", new String[]{Integer.toString(id)});
            db.close();
            return ret;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "setKirim >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public int setDownloaded(int id){
        ContentValues cv = new ContentValues();
        cv.put("is_downloaded",1);
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            int ret = db.update("available_tts", cv, "_id = ?", new String[]{Integer.toString(id)});
            db.close();
            return ret;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "setDownloaded >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public long addProvinsi(int id, String nama){
        ContentValues cv = new ContentValues();
        cv.put("_id", id);
        cv.put("nama", nama);
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            long ret = db.replace("provinsi", null, cv);
            db.close();
            return ret;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "addProvinsi >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getProvinsi()
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String sqlTables = "provinsi";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, null, null, null,
                    null, null, "nama ASC");
            c.moveToFirst();
            db.close();
            return c;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getProvinsi >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public long addKabkota(int id, int id_prov, String nama){
        ContentValues cv = new ContentValues();
        cv.put("_id", id);
        cv.put("id_prov", id_prov);
        cv.put("nama_kabkota", nama);
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            long ret = db.insert("kabkota", null, cv);
            db.close();
            return ret;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "addKabkota >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getKabkota()
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String sqlTables = "kabkota";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, null, null, null,
                    null, null, "nama_kabkota ASC");
            c.moveToFirst();
            db.close();
            return c;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getKabkota >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public int getProvCount()
    {
        int count=0;
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String sqlTables = "provinsi";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, null, null, null,
                    null, null, null);
            count = c.getCount();
            db.close();
            return count;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getProvCount >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public int clearKabkota(){
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            int ret = db.delete("kabkota", null, null);
            db.close();
            return ret;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "clearKabkota >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }


}
