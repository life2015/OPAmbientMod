package com.retrox.aodmod.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

public class VerticalImageSpan extends DynamicDrawableSpan {
    Drawable mDrawable;
    String theme = null;

    public VerticalImageSpan(Context context, Bitmap bitmap, String theme) {
        super(DynamicDrawableSpan.ALIGN_BASELINE);
        this.theme = theme;
        setBitmap(context, bitmap);
    }

    public void setBitmap(Context context, Bitmap bitmap) {
        mDrawable = new BitmapDrawable(context.getResources(), bitmap);
        int width = mDrawable.getIntrinsicWidth();
        int height = mDrawable.getIntrinsicHeight();
        mDrawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
    }

    @Override
    public Drawable getDrawable() {
        return mDrawable;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, Paint paint) {
        Drawable b = mDrawable;
        canvas.save();

        int transY = bottom - b.getBounds().bottom;
        int newLineCount = countLines(text.toString());
        if(newLineCount == 1) transY = transY + 8;
        else {
            if(theme.equals("Pixel")) {
                transY = transY - 8;
            }else{
                transY = transY + 8;
            }
        }
        if (mVerticalAlignment == ALIGN_BASELINE) {
            int textLength = text.length();
            for (int i = 0; i < textLength; i++) {
                if (Character.isLetterOrDigit(text.charAt(i))) {
                    transY -= paint.getFontMetricsInt().descent;
                    break;
                }
            }
        }

        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }

    private static int countLines(String str){
        String[] lines = str.split("\r\n|\r|\n");
        return  lines.length;
    }
}