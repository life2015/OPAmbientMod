package com.retrox.aodmod.proxy.view.custom.oneplus

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.receiver.HeadSetReceiver
import org.jetbrains.anko.*

fun Context.aodHeadSetView(lifecycleOwner: LifecycleOwner): FrameLayout {
    return frameLayout {

        linearLayout {
            orientation = LinearLayout.HORIZONTAL

            val image = imageView {
                id = Ids.iv_headSet
                setImageDrawable(ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_headset))
            }.lparams(width = dip(24), height = dip(24)) {
                gravity = Gravity.BOTTOM
                rightMargin = dip(4)
            }

            val headSetTextView = textView() {
                //            gravity = Gravity.CENTER
                id = Ids.tv_headSetStatus
                textColor = Color.WHITE
                textSize = 16f
                gravity = Gravity.BOTTOM
                setGoogleSans()

            }.lparams(width = wrapContent, height = wrapContent) {
                leftMargin = dip(4)
                gravity = Gravity.CENTER_VERTICAL
            }

            var prevVol = -1 // 上一次音乐音量
            if(!XPref.isSettings()) {
                HeadSetReceiver.headSetConnectLiveEvent.observeNewOnly(lifecycleOwner, Observer {
                    it?.let {
                        if (it is HeadSetReceiver.ConnectionState.HeadSetConnection) {
                            val connection = it.connection
                            image.setImageDrawable(
                                ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_headset)
                            )
                            when (connection) {
                                HeadSetReceiver.Connection.DISCONNECTED -> headSetTextView.text =
                                    ResourceUtils.getInstance(context).getString(R.string.headset_disconnected)
                                HeadSetReceiver.Connection.CONNECTED -> headSetTextView.text =
                                    ResourceUtils.getInstance(context).getString(R.string.headset_connected)
                            }
                        } else if (it is HeadSetReceiver.ConnectionState.BlueToothConnection) {
                            val connection = it.connection
                            val name = it.deviceName
                            val device = it.device

                            image.setImageDrawable(
                                ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_bluetooth)
                            )
                            when (connection) {
                                HeadSetReceiver.Connection.CONNECTING -> headSetTextView.text =
                                    ResourceUtils.getInstance(context).getString(R.string.bt_headset_connecting, name)
                                HeadSetReceiver.Connection.CONNECTED -> headSetTextView.text =
                                    ResourceUtils.getInstance(context).getString(R.string.bt_headset_connected, name)
                                HeadSetReceiver.Connection.DISCONNECTING -> headSetTextView.text =
                                    ResourceUtils.getInstance(context).getString(R.string.bt_headset_disconnecting, name)
                                HeadSetReceiver.Connection.DISCONNECTED -> headSetTextView.text =
                                    ResourceUtils.getInstance(context).getString(R.string.bt_headset_disconnected, name)
                            }
                        } else if (it is HeadSetReceiver.ConnectionState.VolumeChange) {
                            val vol = it.volValue
                            val percent = "${((vol / it.maxValue.toFloat()) * 100).toInt()}%"
                            if (vol == 0) {
                                image.setImageDrawable(
                                    ResourceUtils.getInstance(this)
                                        .getDrawable(R.drawable.ic_volume_off)
                                )
                                headSetTextView.text = ResourceUtils.getInstance(context).getString(R.string.volume_level, percent)
                            } else {
                                if (vol < prevVol && prevVol != -1) {
                                    image.setImageDrawable(
                                        ResourceUtils.getInstance(this)
                                            .getDrawable(R.drawable.ic_volume_down)
                                    )
                                } else {
                                    image.setImageDrawable(
                                        ResourceUtils.getInstance(this)
                                            .getDrawable(R.drawable.ic_volume_up)
                                    )
                                }
                                headSetTextView.text = ResourceUtils.getInstance(context).getString(R.string.volume_level, percent)
                                prevVol = vol
                            }

                        } else if (it is HeadSetReceiver.ConnectionState.ZenModeChange) {
                            val zenMode = it.newMode
                            val resourceUtils = ResourceUtils.getInstance(this)
                            val zenStatePair = when (zenMode) {
                                1 -> resourceUtils.getDrawable(R.drawable.ic_notifications_off) to "Silent"
                                2 -> resourceUtils.getDrawable(R.drawable.ic_vibration) to "Vibrate"
                                3 -> resourceUtils.getDrawable(R.drawable.ic_notifications_active) to "Ring"
                                else -> null
                            }

                            zenStatePair?.let { (drawable, text) ->
                                image.setImageDrawable(drawable)
                                headSetTextView.text = text
                            }
                        }
                    }

                })
            }
        }.lparams(wrapContent, wrapContent) {
            gravity = Gravity.CENTER_HORIZONTAL
        }
    }
}