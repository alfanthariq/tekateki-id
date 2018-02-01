package com.alfanthariq.tekteksil.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alfanthariq.tekteksil.GameActivity;
import com.alfanthariq.tekteksil.R;
import com.alfanthariq.tekteksil.adapter.QuestionAdapter;
import com.alfanthariq.tekteksil.helper.GameDataHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class MendatarFragment extends Fragment {

    private ListView listQuestion;
    private GameDataHelper mDbHelper;
    private Cursor itemData;

    public MendatarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mendatar, container, false);
        listQuestion = (ListView) v.findViewById(R.id.list);
        mDbHelper = new GameDataHelper(getContext(), ((GameActivity) getActivity()).DBNAME);
        itemData = mDbHelper.getQuestionList(0);
        try {
            final QuestionAdapter qAdapter = new QuestionAdapter(getContext(), itemData);
            listQuestion.setAdapter(qAdapter);
            listQuestion.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView<?> parent, View view, int position, long arg3)
                {
                    Cursor cur = (Cursor) qAdapter.getItem(position);
                    cur.moveToPosition(position);
                    int id = cur.getInt(cur.getColumnIndexOrThrow("_id"));
                    int col = cur.getInt(cur.getColumnIndexOrThrow("col"));
                    int row = cur.getInt(cur.getColumnIndexOrThrow("row"));

                    ((GameActivity) getActivity()).setOrientation(0);
                    ((GameActivity) getActivity()).setBlock(col, row);
                    ((GameActivity) getActivity()).mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    GameActivity.Utils.showKeyboard(getActivity());
                }
            });
        } catch (Exception e){

        }
        return v;
    }

    @Override
    public void onDestroyView() {
        itemData.close();
        mDbHelper.close();
        super.onDestroyView();
    }
}
