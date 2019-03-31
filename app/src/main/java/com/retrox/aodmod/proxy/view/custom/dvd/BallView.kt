package com.retrox.aodmod.proxy.view.custom.dvd

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.retrox.aodmod.proxy.view.theme.ThemeManager

class BallView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {

    private val rect: Rect = Rect()
    private val paint = Paint()
    private val colorList = ThemeManager.getPresetThemes().map { Color.parseColor(it.gradientStart) }


    class Rect {
        var x1 = 0f
        var y1 = 0f
        var x2 = 300f
        var y2 = 100f
        var vx = 5f // X轴速度
        var vy = 5f // Y轴速度
        // 移动
        fun move() {
            //向角度的方向移动，偏移圆心
            x1 += vx
            y1 += vy

            x2 += vx
            y2 += vy
        }

        fun left(): Int {
            return x1.toInt()
        }

        fun right(): Int {
            return x2.toInt()
        }

        fun bottom(): Int {
            return y2.toInt()
        }

        fun top(): Int {
            return y1.toInt()
        }
    }

    init {
        paint.color = Color.parseColor("#9C88F8")
        paint.style = Paint.Style.FILL
        paint.alpha = 180
        paint.strokeWidth = 0f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
        paint.textSize = 150f
        // 圆心和半径测量的时候才设置

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }


    override fun onDraw(canvas: Canvas) {
        val startTime = System.currentTimeMillis()

        //        canvas.drawRect(rect.x1, rect.y1, rect.x2, rect.y2, paint);
        canvas.drawText("DVD", rect.x1, rect.y2, paint)
        collisionDetectingAndChangeSpeed(rect)
        rect.move()

        val stopTime = System.currentTimeMillis()
        val runTime = stopTime - startTime
        // 16毫秒执行一次
        postInvalidateDelayed(16)
    }


    // 判断球是否碰撞碰撞边界
    fun collisionDetectingAndChangeSpeed(rec: Rect) {
        val left = left
        val top = top
        val right = right
        val bottom = bottom

        val speedX = rec.vx
        val speedY = rec.vy

        // 碰撞左右，X的速度取反。 speed的判断是防止重复检测碰撞，然后黏在墙上了=。=
        if (rec.left() <= left && speedX < 0) {
            rec.vx = -rec.vx
            collide()
        } else if (rec.top() <= top && speedY < 0) {
            rec.vy = -rec.vy
            collide()
        } else if (rec.right() >= right && speedX > 0) {
            rec.vx = -rec.vx
            collide()
        } else if (rec.bottom() >= bottom && speedY > 0) {
            rec.vy = -rec.vy
            collide()
        }
    }

    fun collide() {
        paint.color = colorList.random()
    }

}
