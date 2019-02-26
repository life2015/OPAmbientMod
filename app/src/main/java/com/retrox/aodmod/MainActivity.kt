package com.retrox.aodmod

import android.media.session.MediaSessionManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val service = getSystemService(MediaSessionManager::class.java)
//        MainHook.logD(service.getActiveSessions(null).toString())
    }
}
