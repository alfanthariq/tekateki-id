package com.alfanthariq.tts.adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by alfanthariq on 04/01/2018.
 */

public class TextDrawable extends Drawable {

    private final String text;
    private final Paint paint;
    private final int width, height;

    public TextDrawable(String text, int width, int height) {
        this.text = text;
        this.paint = new Paint();
        this.width = width;
        this.height = height;
        paint.setColor(Color.BLACK);
        paint.setTextSize(12f);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text, -(width/3), -(height/4), paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
