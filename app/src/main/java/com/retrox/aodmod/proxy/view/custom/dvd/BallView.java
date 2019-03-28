package com.retrox.aodmod.proxy.view.custom.dvd;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class BallView extends View {


    class Rect {
        float x1 = 0, y1 = 0, x2 = 300, y2 = 100;
        float vx = 5; // X轴速度
        float vy = 5; // Y轴速度
        // 移动
        void move() {
            //向角度的方向移动，偏移圆心
            x1 += vx;
            y1 += vy;

            x2 += vx;
            y2 += vy;
        }

        int left() {
            return (int) (x1);
        }

        int right() {
            return (int) (x2);
        }

        int bottom() {
            return (int) (y2);
        }

        int top() {
            return (int) (y1);
        }
    }

    public Rect rect;
    public Paint paint = new Paint();

    private int mCount = 40;   // 小球个数
    private int maxRadius;  // 小球最大半径
    private int minRadius; // 小球最小半径
    private int minSpeed = 5; // 小球最小移动速度
    private int maxSpeed = 20; // 小球最大移动速度

    private int mWidth = 200;
    private int mHeight = 200;

    public BallView(Context context) {
        super(context);

        rect = new Rect();

        paint.setColor(Color.parseColor("#9C88F8"));
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(180);
        paint.setStrokeWidth(0);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        paint.setTextSize(150f);
        // 圆心和半径测量的时候才设置

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = resolveSize(mWidth, widthMeasureSpec);
        mHeight = resolveSize(mHeight, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
        maxRadius = mWidth / 12;
        minRadius = maxRadius / 2;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        long startTime = System.currentTimeMillis();

//        canvas.drawRect(rect.x1, rect.y1, rect.x2, rect.y2, paint);
        canvas.drawText("DVD", rect.x1, rect.y1, paint);
        collisionDetectingAndChangeSpeed(rect);
        rect.move();

        long stopTime = System.currentTimeMillis();
        long runTime = stopTime - startTime;
        // 16毫秒执行一次
        postInvalidateDelayed(16);
    }


    // 判断球是否碰撞碰撞边界
    public void collisionDetectingAndChangeSpeed(Rect rec) {
        int left = getLeft();
        int top = getTop();
        int right = getRight();
        int bottom = getBottom();

        float speedX = rec.vx;
        float speedY = rec.vy;

        // 碰撞左右，X的速度取反。 speed的判断是防止重复检测碰撞，然后黏在墙上了=。=
        if (rec.left() <= left && speedX < 0) {
            rec.vx = -rec.vx;
        } else if (rec.top() <= top && speedY < 0) {
            rec.vy = -rec.vy;
        } else if (rec.right() >= right && speedX > 0) {
            rec.vx = -rec.vx;
        } else if (rec.bottom() >= bottom && speedY > 0) {
            rec.vy = -rec.vy;
        }
    }

}
