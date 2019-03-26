package com.retrox.aodmod.proxy.view

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.receiver.HeadSetReceiver
import org.jetbrains.anko.*

fun Context.aodHeadSetView(lifecycleOwner: LifecycleOwner) : FrameLayout {
    return frameLayout {

        linearLayout {
            orientation = LinearLayout.HORIZONTAL

            val image = imageView {
                setImageDrawable(ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_headset))
            }.lparams(width = dip(24), height = dip(24)) {
                gravity = Gravity.CENTER_VERTICAL
                horizontalMargin = dip(4)
            }

            val headSetTextView = textView() {
                //            gravity = Gravity.CENTER
                id = Ids.tv_music
                textColor = Color.WHITE
                textSize = 16f
                gravity = Gravity.CENTER_VERTICAL
                setGoogleSans()

            }.lparams(width = wrapContent, height = wrapContent) {
                horizontalMargin = dip(4)
                gravity = Gravity.CENTER_VERTICAL
            }

            var prevVol = -1 // 上一次音乐音量

            HeadSetReceiver.headSetConnectLiveEvent.observe(lifecycleOwner, Observer {
                it?.let {
                    if (it is HeadSetReceiver.ConnectionState.HeadSetConnection) {
                        val connection = it.connection
                        image.setImageDrawable(ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_headset))
                        when(connection) {
                            HeadSetReceiver.Connection.DISCONNECTED -> headSetTextView.text = "Headset Unplugged"
                            HeadSetReceiver.Connection.CONNECTED -> headSetTextView.text = "Headset Plugged"
                        }
                    } else if (it is HeadSetReceiver.ConnectionState.BlueToothConnection) {
                        val connection = it.connection
                        val name = it.deviceName
                        val device = it.device

                        image.setImageDrawable(ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_bluetooth))
                        when(connection) {
                            HeadSetReceiver.Connection.CONNECTING -> headSetTextView.text = "$name Connecting"
                            HeadSetReceiver.Connection.CONNECTED -> headSetTextView.text = "$name Connected"
                            HeadSetReceiver.Connection.DISCONNECTING -> headSetTextView.text = "$name Disconnecting"
                            HeadSetReceiver.Connection.DISCONNECTED -> headSetTextView.text = "$name Disconnected"
                        }
                    } else if (it is HeadSetReceiver.ConnectionState.VolumeChange) {
                        val vol = it.volValue
                        val percent = "${((vol / 30.toFloat()) * 100).toInt()}%"
                        if (vol == 0) {
                            image.setImageDrawable(ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_volume_off))
                            headSetTextView.text = "Volume $percent"
                        } else {
                            if (vol < prevVol && prevVol != -1) {
                                image.setImageDrawable(ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_volume_down))
                            } else {
                                image.setImageDrawable(ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_volume_up))
                            }
                            headSetTextView.text = "Volume $percent"
                            prevVol = vol
                        }

                    }
                }

            })
        }.lparams(wrapContent, wrapContent) {
            gravity = Gravity.CENTER_HORIZONTAL
        }
    }
}