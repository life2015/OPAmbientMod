package com.retrox.aodmod.proxy.view

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.R
import com.retrox.aodmod.app.util.logD
import com.retrox.aodmod.data.NowPlayingMediaData
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.state.AodMedia
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

fun Context.aodMusicView(lifecycleOwner: LifecycleOwner): View {
    val isPixelIconEnabled = XPref.getUsePixelMusicIcon()
    var isAnimating = false
    return verticalLayout {

        val mAnimatedIcon = ResourceUtils.getInstance(this).getDrawable(R.drawable.audioanim_animation).constantState?.newDrawable() as? AnimatedVectorDrawable
        mAnimatedIcon?.setBounds(0, 0, 24.toDp, 24.toDp)
        mAnimatedIcon?.registerAnimationCallback(object: Animatable2.AnimationCallback() {
            override fun onAnimationStart(drawable: Drawable?) {
                super.onAnimationStart(drawable)
                isAnimating = true
            }

            override fun onAnimationEnd(drawable: Drawable?) {
                super.onAnimationEnd(drawable)
                isAnimating = false
            }
        })
        val staticIcon = if(isPixelIconEnabled){
            ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_music_pixel_inset)
        }else {
            ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_music)
        }
        val imageIcon = imageView {
            setImageDrawable(staticIcon)
        }.lparams(width = dip(24), height = dip(24)) {
            gravity = Gravity.CENTER_HORIZONTAL
            bottomMargin = dip(16)
        }
        if(isPixelIconEnabled){
            imageIcon.scaleX = 2f
            imageIcon.scaleY = 2f
        }

        val musicText = textView("No recent music") {
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
            logD("Receive Media Data $it")
            if (!XPref.getMusicAodEnabled()) { // 修复音乐显示关不掉的bug
                return@Observer
            }
            if (it == null) {
                visibility = View.INVISIBLE
            }

            it?.let {
                visibility = View.VISIBLE
                musicText.text = "${it.name} - ${it.artist}"
                if(!isAnimating && isPixelIconEnabled) {
                    if (mAnimatedIcon != null) {
                        imageIcon.setImageDrawable(mAnimatedIcon)
                        //Gets stuck first time if not done in post
                        imageIcon.post {
                            mAnimatedIcon.reset()
                            mAnimatedIcon.start()
                        }
                    } else {
                        imageIcon.setImageDrawable(staticIcon)
                    }
                }
            }
        })

        //Hides the view initially if the value is null
        if(AodMedia.aodMediaLiveData.value == null){
            post {
                visibility = View.INVISIBLE
            }
        }




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
                logD("Dream Click Event!")
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

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
