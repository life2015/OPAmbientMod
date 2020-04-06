package com.retrox.aodmod.app.view

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.retrox.aodmod.R
import com.retrox.aodmod.app.state.AppState
import com.retrox.aodmod.extensions.Num2CN
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
                    text = context.getString(R.string.test_settings)
                    setOnClickListener {
                        val state = Settings.Secure.getInt(contentResolver, "night_display_activated", 0)
                        val state2 = Settings.System.getInt(contentResolver, "sysui_do_not_disturb", 0)

//                        val nextAlarm =
                        val nextAlarm = Settings.System.getString(contentResolver, Settings.System.NEXT_ALARM_FORMATTED)
                        toast("night: $state  do not disturb $state2 nextalarm: $nextAlarm")
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

        val result = Num2CN().convert(24, true).reduce { acc, s ->
            acc + s
        }

    }

    private fun checkAndroidVersion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            val dialog = alert {
                title = getString(R.string.dialog_invalid_system)
                message = getString(R.string.dialog_invalid_system_content)
                isCancelable = false
                positiveButton(getString(R.string.dialog_invalid_system_exit)) {
                    finish()
                }
                negativeButton(getString(R.string.dialog_invalid_system_ignore)) {
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