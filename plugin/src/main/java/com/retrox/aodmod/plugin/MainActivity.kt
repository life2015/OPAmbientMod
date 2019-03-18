package com.retrox.aodmod.plugin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button : TextView  = findViewById(R.id.tv_hello)
        button.setOnClickListener {
            val intent = Intent("com.retrox.aodplugin.plugin.boardcast").apply {
                `package` = "com.retrox.aodmod.plugin"
            }
            sendBroadcast(intent)
        }
    }
}
