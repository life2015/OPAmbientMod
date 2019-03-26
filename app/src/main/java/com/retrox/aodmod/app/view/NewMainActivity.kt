package com.retrox.aodmod.app.view

import android.Manifest
import android.app.AndroidAppHelper
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.retrox.aodmod.app.state.AppState
import com.retrox.aodmod.extensions.checkPermission
import com.retrox.aodmod.weather.WeatherProvider
import org.jetbrains.anko.*

class NewMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setTurnScreenOn(true) 测试电话亮屏复现

        scrollView {
            backgroundColor = Color.parseColor("#F5F5F5")

            verticalLayout {
                addView(ActiveStatusCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(PermissionCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(RunStatusCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(WeatherCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(SettingsCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(ToolCard(this@NewMainActivity, this@NewMainActivity).rootView)


//                button {
//                    text = "测试weather"
//                    setOnClickListener {
//                        val weather = WeatherProvider
//                        weather.queryWeatherInformation(context)
//                    }
//                }
            }

            checkAndroidVersion()

            checkPermission {
                ActivityCompat.requestPermissions(
                    this@NewMainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 66
                );
            }
        }

    }

    private fun checkAndroidVersion() {
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