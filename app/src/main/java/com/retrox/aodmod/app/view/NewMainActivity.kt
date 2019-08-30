package com.retrox.aodmod.app.view

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.retrox.aodmod.app.SleepModeActivity
import com.retrox.aodmod.app.state.AppState
import com.retrox.aodmod.extensions.checkPermission
import org.jetbrains.anko.*

class NewMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setTurnScreenOn(true) 测试电话亮屏复现

        scrollView {
            backgroundColor = Color.parseColor("#F5F5F5")

            verticalLayout {
                addView(ActiveStatusCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(MotionAwakeStatCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(PermissionCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(RunStatusCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(ThemeCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(WeatherCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(SettingsCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(ToolCard(this@NewMainActivity, this@NewMainActivity).rootView)



                button {
                    text = "测试Settings"
                    setOnClickListener {
                        val state = Settings.Secure.getInt(contentResolver, "night_display_activated", 0)
                        val state2 = Settings.System.getInt(contentResolver, "sysui_do_not_disturb", 0)

                        toast("night: $state  do not disturb $state2")
                    }
                }
            }

            checkAndroidVersion()

            checkPermission {
                ActivityCompat.requestPermissions(
                    this@NewMainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 66
                )
            }
        }

    }

    private fun checkAndroidVersion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            val dialog = alert {
                title = "系统版本太低"
                message = "此模块只能用于一加官方的Android P上，其他系统会翻车，这是一个悬崖勒马的对话框。强行使用出现bug应当自己负责，无需反馈。"
                isCancelable = false
                positiveButton("退出") {
                    finish()
                }
                negativeButton("强行使用") {
                    it.dismiss()
                }
            }.show()
        }
    }

    override fun onResume() {
        super.onResume()
        AppState.refreshActiveState(this)
        AppState.refreshExpApps(this)
        AppState.refreshStatus("Activity Resume")
        AppState.refreshMotionAwakeState(this)
    }
}