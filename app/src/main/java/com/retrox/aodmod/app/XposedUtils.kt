package com.retrox.aodmod.app

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle

object XposedUtils {
    const val TAICHI_PACKAGE_NAME = "me.weishu.exp"
    const val EDXPOSED_PACKAGE_NAME = "org.meowcat.edxposed.manager"

    fun isExpModuleActive(context: Context?): Boolean {
        var isExp = false
        requireNotNull(context) { "context must not be null!!" }
        try {
            val contentResolver = context.contentResolver
            val uri = Uri.parse("content://me.weishu.exposed.CP/")
            var result: Bundle? = null
            try {
                result = contentResolver.call(uri, "active", null, null)
            } catch (e: RuntimeException) {
                // TaiChi is killed, try invoke
                try {
                    val intent = Intent("me.weishu.exp.ACTION_ACTIVE")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e1: Throwable) {
                    return false
                }
            }
            if (result == null) {
                result = contentResolver.call(uri, "active", null, null)
            }
            if (result == null) {
                return false
            }
            isExp = result.getBoolean("active", false)
        } catch (ignored: Throwable) {
        }
        return isExp
    }

    fun getExpApps(context: Context): List<String> {
        val result: Bundle?
        result = try {
            context.contentResolver
                .call(Uri.parse("content://me.weishu.exposed.CP/"), "apps", null, null)
        } catch (e: Throwable) {
            return emptyList()
        }
        return if (result == null) {
            emptyList()
        } else result.getStringArrayList("apps") ?: return emptyList()
    }

    //This will get hooked by the module itself when using EdXposed to return true
    fun isEdXposedModuleActive(): Boolean {
        return false
    }

    fun isTaiChiInstalled(context: Context?): Boolean {
        return try {
            context?.packageManager?.getApplicationInfo(TAICHI_PACKAGE_NAME, 0)?.enabled ?: false
        }catch (e: PackageManager.NameNotFoundException){
            false
        }
    }

    fun isEdXposedInstalled(context: Context?): Boolean {
        return try {
            context?.packageManager?.getApplicationInfo(EDXPOSED_PACKAGE_NAME, 0)?.enabled ?: false
        }catch (e: PackageManager.NameNotFoundException){
            false
        }
    }
}