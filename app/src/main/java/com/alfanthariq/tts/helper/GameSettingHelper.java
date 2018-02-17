package com.alfanthariq.tts.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by alfanthariq on 13/01/2018.
 */

public class GameSettingHelper extends SQLiteAssetHelper {

    private static final String TAG = "GameSettingHelper";
    private static final String DATABASE_NAME = "settings.db";
    private static final int DATABASE_VERSION = 6;


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
                    null, null, "tgl_terbit DESC");
            c.moveToFirst();
            db.close();
            return c;
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
    }

    public Cursor getDownloaded(int tipe)
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String sqlTables = "available_tts";
            qb.setTables(sqlTables);
            String where = "AND is_sent > ?";
            int wClause = -1;
            switch (tipe){
                case 0: //semua
                    where = "AND is_sent > ?";
                    wClause = -1;
                    break;
                case 1: //Belum selesai
                    where = "AND is_sent = ?";
                    wClause = 0;
                    break;
                case 2: //Selesai
                    where = "AND is_sent = ?";
                    wClause = 1;
                    break;
            }
            Cursor c = qb.query(db, null, "is_downloaded = ? "+where, new String[] {Integer.toString(1), Integer.toString(wClause)},
                    null, null, "tgl_terbit DESC");
            c.moveToFirst();
            db.close();
            return c;
        }
        catch (SQLException mSQLException)
        {
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
            throw mSQLException;
        }
    }

    public long addAvailableTTS(int id, int ed_int, String ed_str, String tgl_terbit, String db_name){
        ContentValues cv = new ContentValues();
        cv.put("_id", id);
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
            throw mSQLException;
        }
    }

    public int deleteTTS(int id){
        try
        {
            ContentValues cv = new ContentValues();
            cv.put("is_downloaded",0);
            SQLiteDatabase db = getReadableDatabase();
            //int ret = db.delete("available_tts", "_id = ?", new String[]{Integer.toString(id)});
            int ret = db.update("available_tts", cv, "_id = ?", new String[]{Integer.toString(id)});
            db.close();
            return ret;
        }
        catch (SQLException mSQLException)
        {
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
            throw mSQLException;
        }
    }


}
