package com.retrox.aodmod.plugin

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log

class MyService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AodPlugin", "On Start Command")
        val handler = Handler()

        val runnable = object : Runnable {
            override fun run() {
                Log.d("AodPlugin", "Handler OK")
                handler.postDelayed(this, 5000L)
            }
        }
        handler.postDelayed(runnable, 5000L)
        return Service.START_STICKY
    }

    val messenger = Messenger(Handler()).apply {
        send(Message.obtain().apply { obj = "fa" })
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("AodPlugin", "Service Onbind OK")
        return messenger.binder
    }
}