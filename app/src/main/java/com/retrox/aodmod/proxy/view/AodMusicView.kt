package com.retrox.aodmod.proxy.view

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.state.AodMedia
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

fun Context.aodMusicView(lifecycleOwner: LifecycleOwner): View {
    return verticalLayout {

        val imageIcon = imageView {
            setImageDrawable(ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_music))
        }.lparams(width = dip(24), height = dip(24)) {
            gravity = Gravity.CENTER_HORIZONTAL
            bottomMargin = dip(16)
        }

        val musicText = textView("无最近播放") {
            //            gravity = Gravity.CENTER
            id = Ids.tv_music
            textColor = Color.WHITE
            textSize = 16f
            horizontalPadding = dip(44)
            gravity = Gravity.CENTER_HORIZONTAL
            setGoogleSans()

        }.lparams(width = matchParent, height = wrapContent)

        visibility = View.INVISIBLE // todo 这里可能会让一部分人有bug 但是我他妈不想修啊


        AodMedia.aodMediaLiveData.observe(lifecycleOwner, Observer {
            MainHook.logD("Receive Media Data $it")
            if (!XPref.getMusicAodEnabled()) { // 修复音乐显示关不掉的bug
                return@Observer
            }
            if (it == null) {
                visibility = View.INVISIBLE
            }

            it?.let {
                visibility = View.VISIBLE
                musicText.text = "${it.name} - ${it.artist}"
            }
        })


        val controlPanel = constraintLayout {
            id = Ids.cl_media_control

            imageView {
                id = Ids.iv_media_back
                setImageDrawable(ResourceUtils.getInstance(context).getDrawable(R.drawable.ic_music_previous))
            }.lparams(width = dip(24), height = dip(24)) {
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
            imageView {
                id = Ids.iv_media_pause
                setImageDrawable(ResourceUtils.getInstance(context).getDrawable(R.drawable.ic_music_pause))
            }.lparams(width = dip(24), height = dip(24)) {
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
            imageView {
                id = Ids.iv_media_next
                setImageDrawable(ResourceUtils.getInstance(context).getDrawable(R.drawable.ic_music_next))
            }.lparams(width = dip(24), height = dip(24)) {
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }

            setOnClickListener {
                MainHook.logD("Dream Click Event!")
            }

            visibility = View.GONE // todo 控制音乐播放能不能用的状态
        }.lparams(width = dip(200), height = wrapContent) {
            //            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
//            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
//            topToBottom = Ids.tv_music
            topMargin = dip(12)
        }

    }

}
