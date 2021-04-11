package com.retrox.aodmod.proxy.view.custom.oneplus

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.retrox.aodmod.R
import com.retrox.aodmod.app.util.logD
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.extensions.toPx
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.state.AodMedia
import org.jetbrains.anko.*

fun Context.aodMusicViewOnePlus(
    lifecycleOwner: LifecycleOwner,
    weatherView: View
): View {
    val isPixelIconEnabled = XPref.getUsePixelMusicIcon()
    var isAnimating = false

    val mAnimatedIcon = ResourceUtils.getInstance(this)
        .getDrawable(R.drawable.audioanim_animation).constantState?.newDrawable() as? AnimatedVectorDrawable
    mAnimatedIcon?.setBounds(0, 0, 24.toDp, 24.toDp)
    mAnimatedIcon?.registerAnimationCallback(object : Animatable2.AnimationCallback() {
        override fun onAnimationStart(drawable: Drawable?) {
            super.onAnimationStart(drawable)
            isAnimating = true
        }

        override fun onAnimationEnd(drawable: Drawable?) {
            super.onAnimationEnd(drawable)
            isAnimating = false
        }
    })
    val staticIcon = if (isPixelIconEnabled) {
        ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_music_pixel_inset)
    } else {
        ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_music)
    }

    return linearLayout {
        setPadding(dip(16), 0, dip(16), 0)
        this@linearLayout.visibility = View.INVISIBLE
        orientation = LinearLayout.HORIZONTAL
        bottomPadding = 8.toPx
        clipToPadding = true
        gravity = Gravity.CENTER_HORIZONTAL
        val imageIcon = imageView {
            setImageDrawable(staticIcon)
        }.lparams(width = dip(24), height = dip(24)) {
            gravity = Gravity.CENTER
            marginEnd = dip(16)
        }
        if (isPixelIconEnabled) {
            imageIcon.scaleX = 2f
            imageIcon.scaleY = 2f
        }

        verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL
            val musicText = textView("No recent music") {
                //            gravity = Gravity.CENTER
                id = Ids.tv_music
                textColor = Color.WHITE
                textSize = 16f
                bottomPadding = dip(4)
                gravity = Gravity.CENTER_HORIZONTAL
                setGoogleSans()
            }.lparams(width = wrapContent, height = wrapContent)

            AodMedia.aodMediaLiveData.observe(lifecycleOwner, Observer {
                logD("Receive Media Data $it")
                if (!XPref.getMusicAodEnabled()) { // 修复音乐显示关不掉的bug
                    return@Observer
                }
                if (it == null) {
                    if(XPref.getAodShowWeather()) weatherView.visibility = View.VISIBLE
                    this@linearLayout.visibility = View.INVISIBLE
                }

                it?.let {
                    weatherView.visibility = View.INVISIBLE
                    this@linearLayout.visibility = View.VISIBLE
                    if(it.overriddenFullString.isEmpty()) {
                        musicText.text = it.name
                    }else{
                        musicText.text = it.getMusicString()
                    }
                    if (!isAnimating && isPixelIconEnabled) {
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
            if (AodMedia.aodMediaLiveData.value == null) {
                post {
                    if(XPref.getAodShowWeather()) weatherView.visibility = View.VISIBLE
                    this@linearLayout.visibility = View.INVISIBLE
                }
            }

            val musicTextArtist = textView("") {
                id = Ids.tv_music_artist
                textColor = Color.WHITE
                textSize = 14f
                gravity = Gravity.CENTER_HORIZONTAL
                setGoogleSans()
            }.lparams(width = wrapContent, height = wrapContent)

            AodMedia.aodMediaLiveData.observe(lifecycleOwner, Observer {
                logD("Receive Media Data $it")
                if (!XPref.getMusicAodEnabled()) { // 修复音乐显示关不掉的bug
                    return@Observer
                }
                if (it == null) {
                    if(XPref.getAodShowWeather()) weatherView.visibility = View.VISIBLE
                    this@linearLayout.visibility = View.INVISIBLE
                }

                it?.let {
                    weatherView.visibility = View.INVISIBLE
                    this@linearLayout.visibility = View.VISIBLE
                    if(it.overriddenFullString.isEmpty()) {
                        musicTextArtist.text = it.artist
                    }else{
                        musicTextArtist.text = ""
                    }
                }
            })
        }
    }

}

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
