package com.retrox.aodmod.app.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.retrox.aodmod.app.state.AppState
import org.jetbrains.anko.*

class NewMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scrollView {
            backgroundColor = Color.parseColor("#F5F5F5")

            verticalLayout {
                addView(ActiveStatusCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(RunStatusCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(SettingsCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(ToolCard(this@NewMainActivity, this@NewMainActivity).rootView)
            }

            checkAndroidVersion()

        }

    }

    fun checkAndroidVersion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            val dialog = alert {
                title = "系统版本太低"
                message = "此模块只能用于一加官方的Android P上，其他系统会翻车，这是一个悬崖勒马的对话框。"
                isCancelable = false
                positiveButton("好的") {
                    finish()
                }
                negativeButton("不好") {
                    finish()
                }
            }.show()
        }
    }

    override fun onResume() {
        super.onResume()
        AppState.refreshActiveState(this)
        AppState.refreshExpApps(this)
        AppState.refreshStatus("Activity Resume")
    }
}