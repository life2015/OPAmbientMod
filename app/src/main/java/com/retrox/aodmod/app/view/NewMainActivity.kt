package com.retrox.aodmod.app.view

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.retrox.aodmod.app.state.AppState
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class NewMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scrollView {
            backgroundColor = Color.parseColor("#F5F5F5")

            verticalLayout {
                addView(ActiveStatusCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(RunStatusCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(ToolCard(this@NewMainActivity, this@NewMainActivity).rootView)

            }
        }

    }

    override fun onResume() {
        super.onResume()
        AppState.refreshActiveState(this)
        AppState.refreshExpApps(this)
        AppState.refreshStatus("Activity Resume")
    }
}