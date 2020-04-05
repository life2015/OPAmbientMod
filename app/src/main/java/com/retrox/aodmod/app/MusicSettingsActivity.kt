package com.retrox.aodmod.app

import androidx.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import com.retrox.aodmod.app.state.AppState
import org.jetbrains.anko.*
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import android.widget.Toast
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange


class MusicSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scrollView {
            verticalLayout {
                textView {
                    text = context.getString(R.string.aod_music_settings_what)
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = context.getString(R.string.aod_music_settings_what_desc)
                    gravity = Gravity.START
                    textColor = Color.BLACK
                    textSize = 16f

                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = context.getString(R.string.aod_music_supported)
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = context.getString(R.string.aod_music_supported_desc)
                    gravity = Gravity.START
                    textColor = Color.BLACK
                    textSize = 16f

                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                textView {
                    text = context.getString(R.string.aod_music_precautions)
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = context.getString(R.string.aod_music_precautions_desc)
                    gravity = Gravity.START
                    textColor = Color.BLACK
                    textSize = 16f

                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                toggleButton {
                    textOn = context.getString(R.string.aod_music_enabled)
                    textOff = context.getString(R.string.aod_music_disabled)
                    isChecked = AppPref.musicShowOnAod
                    onCheckedChange { buttonView, isChecked ->
                        AppPref.musicShowOnAod = isChecked
                        Toast.makeText(context, context.getString(R.string.aod_music_toast, AppPref.musicShowOnAod.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                button {
                    text = context.getString(R.string.aod_music_add_qq)
                    setOnClickListener {
                        val t = Intent("me.weishu.exp.ACTION_ADD_APP")
                        t.data = Uri.parse("package:" + "com.netease.cloudmusic" + "|" + "com.tencent.qqmusic")
                        t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        try {
                            startActivity(t)
                        } catch (e: ActivityNotFoundException) {
                            // TaiChi not installed or version below 4.3.4.
                        }

                    }
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.START
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }


                textView {
                    text = context.getString(R.string.aod_music_enabled_apps)
                    textColor = ContextCompat.getColor(context, R.color.colorOrange)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                verticalLayout {
                    AppState.expApps.observe(this@MusicSettingsActivity, Observer {
                        it?.let { list ->
                            removeAllViews()
                            list.forEach {
                                textView {
                                    text = it
                                    gravity = Gravity.START
                                    textSize = 16f
                                }.lparams(width = matchParent, height = wrapContent) {
                                    verticalMargin = dip(6)
                                    horizontalMargin = dip(8)
                                }
                            }

                        }
                    })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AppState.refreshExpApps(this)
        AppState.refreshActiveState(this)
    }
}