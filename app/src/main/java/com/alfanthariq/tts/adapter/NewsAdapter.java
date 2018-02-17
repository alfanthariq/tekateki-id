package com.alfanthariq.tts.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfanthariq.tts.DefaultExceptionHandler;
import com.alfanthariq.tts.R;
import com.alfanthariq.tts.model.NewsObject;

import java.util.List;

/**
 * Created by alfanthariq on 02/02/2018.
 */

public class NewsAdapter extends BaseAdapter {
    private String TAG = "NewsAdapter";
    private Activity activity;
    private LayoutInflater inflater;
    private List<NewsObject> newsItems;

    public NewsAdapter(Activity activity, List<NewsObject> newsItems){
        this.activity = activity;
        this.newsItems = newsItems;
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(activity));
    }

    @Override
    public int getCount() {
        return newsItems.size();
    }

    @Override
    public Object getItem(int location) {
        return newsItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.news_list, null);

        TextView judul = (TextView) convertView.findViewById(R.id.tvJudul);
        ImageView foto = (ImageView) convertView.findViewById(R.id.ivFoto);

        // getting movie data for the row
        NewsObject m = newsItems.get(position);
        judul.setText(m.getJudul());
        String base64img = m.getImg64();
        Bitmap decodedImage;
        if (base64img!=null) {
            byte[] imageBytes = Base64.decode(base64img, Base64.DEFAULT);
            decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            foto.setImageBitmap(decodedImage);
        }
        return convertView;
    }
}
