package com.alfanthariq.tekteksil.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alfanthariq.tekteksil.R;

/**
 * Created by alfanthariq on 08/01/2018.
 */

public class QuestionAdapter extends CursorAdapter {

    public QuestionAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.question_list, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvBadge = (TextView) view.findViewById(R.id.tvBadge);
        TextView tvQuestion = (TextView) view.findViewById(R.id.tvQuestion);
        // Extract properties from cursor
        String badge = cursor.getString(cursor.getColumnIndexOrThrow("badge"));
        String quest = cursor.getString(cursor.getColumnIndexOrThrow("quest"));
        // Populate fields with extracted properties
        tvBadge.setText(badge);
        tvQuestion.setText(quest);
    }
}
