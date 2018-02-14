package com.alfanthariq.tekteksil.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.alfanthariq.tekteksil.DefaultExceptionHandler;
import com.alfanthariq.tekteksil.R;

/**
 * Created by alfanthariq on 03/01/2018.
 */

public class GridItemView extends AppCompatEditText {

    String TAG = "GridItemView";

    public interface OnClickListener {
        void OnClick(GridItemView v);
    }

    int bgColor = R.color.colorWhite;
    int idX = 0; //default
    int idY = 0; //default
    int oTag = 0; //default
    String horRelation, verRelation = "";
    boolean isBlack = false;
    private OnClickListener clickedListener;

    public GridItemView(Context context, int x, int y, int tag, boolean is_black, String hor_relation, String ver_relation) {
        super(context);
        idX = x;
        idY = y;
        oTag = tag;
        isBlack = is_black;
        horRelation = hor_relation;
        verRelation = ver_relation;
        init();
    }

    public GridItemView(Context context) {
        super(context);
        init();
    }

    public GridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /*@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GridItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/

    private void init() {

    }


    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onDraw(Canvas canvas) {
        /*if (touchOn) {
            canvas.drawColor(getResources().getColor(R.color.colorPrimary));
        } else {
            canvas.drawColor(Color.WHITE);
        }*/
        if (!isBlack) {
            canvas.drawColor(getResources().getColor(bgColor));
        } else {
            canvas.drawColor(Color.BLACK);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                invalidate();
                if(clickedListener != null){
                    clickedListener.OnClick(this);
                    performClick();
                }
                return true;
        }
        return false;
    }

    public void setOnClickListener(OnClickListener listener){
        clickedListener = listener;
    }

    public int getIdX(){
        return idX;
    }

    public int getIdY(){
        return idY;
    }

    public String getHorRelation() {
        return horRelation;
    }

    public String getVerRelation() {
        return verRelation;
    }

    public boolean getIsBlack(){
        return isBlack;
    }

    public void setBackColor(int color){
        this.bgColor = color;
        this.invalidate();
    }
}
