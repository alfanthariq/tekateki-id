package com.alfanthariq.tekteksil.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.tekteksil.GameActivity;
import com.alfanthariq.tekteksil.R;
import com.alfanthariq.tekteksil.helper.GameSettingHelper;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cyd.awesome.material.AwesomeButton;

/**
 * Created by alfanthariq on 18/01/2018.
 */

public class DownloadedAdapter extends CursorAdapter {
    private String TAG = "AvailableAdapter";
    private AwesomeButton playButton, delButton;
    private GameSettingHelper mDBHelper;
    private ListView listView;
    private Context ctx;
    private Activity act;

    public DownloadedAdapter(Context context, Cursor cursor, ListView listView, Activity act) {
        super(context, cursor, 0);
        this.listView = listView;
        this.ctx = context;
        this.act = act;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        mDBHelper = new GameSettingHelper(context);
        return LayoutInflater.from(context).inflate(R.layout.downloaded_list, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        Date date = null;
        // Find fields to populate in inflated template
        TextView tvEdisiStr = (TextView) view.findViewById(R.id.tvEditionStr);
        TextView tvEdisiTgl = (TextView) view.findViewById(R.id.tvEditionDate);
        ImageView imgLeft = (ImageView) view.findViewById(R.id.imgLeft);
        LinearLayout linLayImgLeft = (LinearLayout) view.findViewById(R.id.linLayImgLeft);

        playButton = (AwesomeButton) view.findViewById(R.id.btnPlay);
        delButton = (AwesomeButton) view.findViewById(R.id.btnDel);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer itemPos = (Integer) view.getTag();
                cursor.moveToPosition(itemPos);

                int isSent = cursor.getInt(cursor.getColumnIndexOrThrow("is_sent"));
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow("db_name"));
                String ed_str = cursor.getString(cursor.getColumnIndexOrThrow("edition_str"));
                String pathDB = context.getFilesDir().getAbsolutePath() + File.separator + "tts" + File.separator + fileName;
                File file = new File(pathDB);
                if (file.exists()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    Intent intent = new Intent(ctx, GameActivity.class);
                    intent.putExtra("dbname", fileName);
                    intent.putExtra("ed_str", ed_str);
                    intent.putExtra("id_tts", Integer.toString(id));
                    intent.putExtra("is_sent", Integer.toString(isSent));
                    ctx.startActivity(intent);
                    act.overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    Toast.makeText(context, "File tts tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer pos = (Integer) view.getTag();
                cursor.moveToPosition(pos);
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow("db_name"));
                String pathDB = context.getFilesDir().getAbsolutePath() + File.separator + "tts" + File.separator + fileName;
                final int id_tts = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                final File file = new File(pathDB);
                // Delete file
                new AlertDialog.Builder(context)
                        .setTitle("Menghapus data TTS ?")
                        .setMessage("Data TTS yang sudah dihapus tidak bisa dikembalikan lagi. \n" +
                                "Jawaban yang sudah anda kirimkan sudah tersimpan dan skor anda tidak akan berkurang. Lanjutkan menghapus ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (file.exists()) {
                                    if (file.delete()) {
                                        mDBHelper.deleteTTS(id_tts);
                                        Cursor myCursor = mDBHelper.getDownloaded();
                                        DownloadedAdapter.this.swapCursor(myCursor);
                                    } else {
                                        Toast.makeText(context, "Gagal menghapus file", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    mDBHelper.deleteTTS(id_tts);
                                    Cursor myCursor = mDBHelper.getDownloaded();
                                    DownloadedAdapter.this.swapCursor(myCursor);
                                }
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        int pos = cursor.getPosition();

        playButton.setTag(pos);
        delButton.setTag(pos);

        // Extract properties from cursor

        String tglTerbit = cursor.getString(cursor.getColumnIndexOrThrow("tgl_terbit"));
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = iso8601Format.parse(tglTerbit);
        } catch (ParseException e) {
            Log.e(TAG, "Parsing ISO8601 datetime failed", e);
        }

        String edisi_str = cursor.getString(cursor.getColumnIndexOrThrow("edition_str"));
        String edisi_tgl = (String) android.text.format.DateFormat.format("dd MMMM yyyy",   date);
        // Populate fields with extracted properties
        tvEdisiStr.setText(edisi_str);
        tvEdisiTgl.setText(edisi_tgl);

        final int isSent = cursor.getInt(cursor.getColumnIndexOrThrow("is_sent"));
        if (isSent==1) {
            imgLeft.setImageDrawable(new IconicsDrawable(context).icon(FontAwesome.Icon.faw_check).color(context.getResources().getColor(R.color.colorPrimaryDark)).sizeDp(15));
            linLayImgLeft.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        } else {
            linLayImgLeft.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }

    }
}
