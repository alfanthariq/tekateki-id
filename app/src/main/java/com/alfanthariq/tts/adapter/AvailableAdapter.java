package com.alfanthariq.tts.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.tts.DefaultExceptionHandler;
import com.alfanthariq.tts.R;
import com.alfanthariq.tts.helper.GameSettingHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cyd.awesome.material.AwesomeButton;

/**
 * Created by alfanthariq on 13/01/2018.
 */

public class AvailableAdapter extends CursorAdapter {
    private String TAG = "AvailableAdapter";
    private AwesomeButton dlButton;
    private GameSettingHelper mDBHelper;
    private ListView listView;
    private Cursor myCursor;
    private Activity act;
    private Context context;

    // Progress Dialog
    public static final int progress_bar_type = 0;

    // File url to download
    private static String base_url = "https://api.alfanthariq.com/cdn/tts/";


    public AvailableAdapter(Context context, Cursor cursor, ListView listView, Activity act) {
        super(context, cursor, 0);
        this.listView = listView;
        this.act = act;
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(act));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        mDBHelper = new GameSettingHelper(context);
        return LayoutInflater.from(context).inflate(R.layout.available_list, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        Date date = null;
        myCursor = cursor;
        // Find fields to populate in inflated template
        TextView tvEdisiStr = (TextView) view.findViewById(R.id.tvEditionStr);
        TextView tvEdisiTgl = (TextView) view.findViewById(R.id.tvEditionDate);

        dlButton = (AwesomeButton) view.findViewById(R.id.btnDownload);
        dlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer pos = (Integer) view.getTag();
                cursor.moveToPosition(pos);
                //Toast.makeText(context, cursor.getString(cursor.getColumnIndexOrThrow("edition_str")), Toast.LENGTH_SHORT).show();
                String pathName = context.getFilesDir().getAbsolutePath()+File.separator+"tts"+File.separator;
                File file = new File(pathName);
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow("db_name"));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                new DownloadFileFromURL(act, file, fileName, id).execute(base_url+fileName);
            }
        });

        int pos = cursor.getPosition();
        dlButton.setTag(pos);

        // Extract properties from cursor

        String tglTerbit = cursor.getString(cursor.getColumnIndexOrThrow("tgl_terbit"));
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = iso8601Format.parse(tglTerbit);
        } catch (ParseException e) {

        }

        String edisi_str = cursor.getString(cursor.getColumnIndexOrThrow("edition_str"));
        String edisi_tgl = (String) android.text.format.DateFormat.format("dd MMMM yyyy",   date);
        // Populate fields with extracted properties
        tvEdisiStr.setText(edisi_str);
        tvEdisiTgl.setText(edisi_tgl);
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        File basePath;
        String fileName;
        int id;
        Activity act;
        Boolean berhasil;

        DownloadFileFromURL(Activity act, File path, String fileName, int id){
            this.basePath = path;
            this.fileName = fileName;
            this.id = id;
            this.act = act;
        }
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            berhasil = false;
            act.showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                HttpURLConnection conection = (HttpURLConnection) url.openConnection();
                conection.setInstanceFollowRedirects(false);
                int response = conection.getResponseCode();

                if(response == 200) {
                    // this will be useful so that you can show a tipical 0-100%
                    // progress bar
                    int lenghtOfFile = conection.getContentLength();

                    // download the file
                    //                BufferedInputStream input = new BufferedInputStream(url.openStream(),
                    //                        8192);

                    InputStream input = conection.getInputStream();

                    // Output stream
                    FileOutputStream output = new FileOutputStream(basePath + File.separator
                            + fileName);
                    //BufferedOutputStream bos = new BufferedOutputStream(output);

                    byte data[] = new byte[1024];
                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                    berhasil = true;
                } else {
                    System.out.println("Length response : "+response);
                }

                /*URL url = new URL(f_url[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.setRequestProperty("Accept-Encoding", "identity");
                c.connect();
                int lenghtOfFile = c.getContentLength();
                System.out.println("Length URL : "+f_url[0]);
                System.out.println("Length : "+lenghtOfFile);
                String path = basePath + File.separator
                        + fileName;
                File file = new File(path);
                FileOutputStream fos = new FileOutputStream(file);
                InputStream is = c.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = bis.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    fos.write(data, 0, count);
                }

                // closing streams
                fos.close();
                is.close();
                berhasil = true;*/
            } catch (Exception e) {
                berhasil = false;
                System.out.println(e.getMessage());
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            act.dismissDialog(progress_bar_type);

            if (berhasil) {
                mDBHelper.setDownloaded(id);
                myCursor = mDBHelper.getAvailable();
                AvailableAdapter.this.swapCursor(myCursor);
                Toast.makeText(context, "TTS berhasil di download", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Timeout. TTS gagal di download", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
