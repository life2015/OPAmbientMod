package com.retrox.aodmod.app.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class NewMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scrollView {
            verticalLayout {
                addView(ActiveStatusCard(this@NewMainActivity, this@NewMainActivity).rootView)
                addView(RunStatusCard(this@NewMainActivity, this@NewMainActivity).rootView)

            }
        }

    }
}