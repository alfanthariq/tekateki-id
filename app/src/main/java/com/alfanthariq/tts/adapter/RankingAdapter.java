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
import android.widget.TextView;

import com.alfanthariq.tts.DefaultExceptionHandler;
import com.alfanthariq.tts.R;
import com.alfanthariq.tts.model.RankingObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by alfanthariq on 30/01/2018.
 */

public class RankingAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<RankingObject> rankItems;

    public RankingAdapter(Activity activity, List<RankingObject> rankItems){
        this.activity = activity;
        this.rankItems = rankItems;
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(activity));
    }

    @Override
    public int getCount() {
        return rankItems.size();
    }

    @Override
    public Object getItem(int location) {
        return rankItems.get(location);
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
            convertView = inflater.inflate(R.layout.rank_list, null);

        TextView rank = (TextView) convertView.findViewById(R.id.tvRank);
        TextView nama = (TextView) convertView.findViewById(R.id.tvNama);
        TextView daerah = (TextView) convertView.findViewById(R.id.tvDaerah);
        TextView skor = (TextView) convertView.findViewById(R.id.tvSkor);
        CircleImageView ivFoto = (CircleImageView) convertView.findViewById(R.id.ivFoto);

        // getting movie data for the row
        RankingObject m = rankItems.get(position);

        rank.setText("Rank : "+Integer.toString(m.getRanking()));
        nama.setText(m.getFull_name());
        daerah.setText(m.getKota()+", "+m.getProv());
        skor.setText(Integer.toString(m.getTotal_skor()));
        String img64 = m.getImg64();
        Bitmap decodedImage;
        if (img64!=null) {
            if (!img64.equals("")) {
                try {
                    img64 = img64.replaceAll("\n", "");
                    byte[] imageBytes = Base64.decode(img64, Base64.DEFAULT);
                    decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    ivFoto.setImageBitmap(decodedImage);
                } catch (Exception e) {
                    ivFoto.setImageDrawable(activity.getResources().getDrawable(R.drawable.user_default));
                }
            } else {
                ivFoto.setImageDrawable(activity.getResources().getDrawable(R.drawable.user_default));
            }
        } else {
            ivFoto.setImageDrawable(activity.getResources().getDrawable(R.drawable.user_default));
        }

        return convertView;
    }
}
