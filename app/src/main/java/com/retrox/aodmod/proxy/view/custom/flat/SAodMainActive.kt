package com.retrox.aodmod.proxy.view.custom.flat

import androidx.lifecycle.LifecycleOwner
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import org.jetbrains.anko.support.v4.viewPager
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.widget.ImageView
import android.widget.TextView
import com.retrox.aodmod.extensions.setGradientTest
import com.retrox.aodmod.proxy.view.Ids
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout


fun Context.sumSungAodMainViewActive(lifecycleOwner: LifecycleOwner): View {
    return frameLayout {
        backgroundColor = Color.BLACK

        viewPager {
            id = Ids.ly_main

            val view1 = mainViewTest(lifecycleOwner)
            val view2 = mainViewTest(lifecycleOwner)

            val adapter = TestPagerAdapter(context, listOf(view1, view2))
            setAdapter(adapter)

        }.lparams(width = matchParent, height = matchParent)
    }
}

fun Context.mainViewTest(lifecycleOwner: LifecycleOwner) : View {
    return constraintLayout {

        val clockView = flatStyleAodClock(lifecycleOwner).apply {
            id = Ids.ly_clock
        }.lparams(width = matchParent, height = wrapContent) {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            topMargin = dip(140)
            leftMargin = dip(36)
        }
        addView(clockView)

        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            applyRecursively {
                when(it) {
                    is TextView -> it.setGradientTest()
                    is ImageView -> it.imageTintList = ColorStateList.valueOf(Color.parseColor(ThemeManager.getCurrentColorPack().tintColor))
                }
            }
        }

    }
}

class TestPagerAdapter(var context: Context, var views: List<View>) : PagerAdapter() {

    override fun getCount(): Int {
        return views.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val view = views[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        val imageView = `object` as ImageView
        val view = `object` as View
        container.removeView(view)
    }
}