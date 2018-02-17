package com.alfanthariq.tts.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alfanthariq.tts.DefaultExceptionHandler;
import com.alfanthariq.tts.GameActivity;
import com.alfanthariq.tts.R;
import com.alfanthariq.tts.adapter.QuestionAdapter;
import com.alfanthariq.tts.helper.GameDataHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenurunFragment extends Fragment {

    private ListView listQuestion;
    private GameDataHelper mDbHelper;
    private Cursor itemData;

    public MenurunFragment() {
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
        View v = inflater.inflate(R.layout.fragment_menurun, container, false);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(getActivity()));

        listQuestion = (ListView) v.findViewById(R.id.list);
        mDbHelper = new GameDataHelper(getContext(), ((GameActivity) getActivity()).DBNAME);
        itemData = mDbHelper.getQuestionList(1);
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

                    ((GameActivity) getActivity()).setOrientation(1);
                    ((GameActivity) getActivity()).setBlock(col, row);
                    ((GameActivity) getActivity()).mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    GameActivity.Utils.showKeyboard(getActivity());
                    //Toast.makeText(getContext(), "ID : "+Integer.toString(id), Toast.LENGTH_SHORT).show();
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
