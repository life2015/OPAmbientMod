package com.retrox.aodmod.app

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.jetbrains.anko.button
import org.jetbrains.anko.verticalLayout


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        MainHook.logD(service.getActiveSessions(null).toString())

        verticalLayout {
            button {
                text = "激活模块" + XposedUtils.isExpModuleActive(this@MainActivity)
                setOnClickListener {
                    val t = Intent("me.weishu.exp.ACTION_MODULE_MANAGE")
                    t.data = Uri.parse("package:" + "com.retrox.aodmod")
                    t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        startActivity(t)
                    } catch (e: ActivityNotFoundException) {
                        // TaiChi not installed.
                    }
                }
            }

            button("检查添加app") {
                setOnClickListener {
                    val apps = getExpApps(this@MainActivity)
                    Log.d("AOD", apps.toString())
                }
            }

            button("Sleep On") {
                setOnClickListener {
                    val intent = Intent("com.aodmod.sleep.on")
                    sendBroadcast(intent)
                }
            }

            button("Sleep OFF") {
                setOnClickListener {
                    val intent = Intent("com.aodmod.sleep.off")
                    sendBroadcast(intent)
                }
            }
        }
    }

    fun getExpApps(context: Context): List<String> {
        val result: Bundle?
        try {
            result = context.contentResolver.call(Uri.parse("content://me.weishu.exposed.CP/"), "apps", null, null)
        } catch (e: Throwable) {
            return emptyList()
        }

        return if (result == null) {
            emptyList()
        } else result.getStringArrayList("apps") ?: return emptyList()
    }
}
