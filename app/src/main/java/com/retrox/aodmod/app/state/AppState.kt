package com.retrox.aodmod.app.state

import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.provider.Settings
import com.retrox.aodmod.app.XposedUtils

object AppState {
    val isActive = MutableLiveData<Boolean>()
    val expApps = MutableLiveData<List<String>>()
    val needRefreshStatus = MutableLiveData<String>()
    val isMotionAwakeEnabled = MutableLiveData<Boolean>()
    val sleepModeState = MutableLiveData<Boolean>()
    val aodModeState = MutableLiveData<String>()

    fun refreshExpApps(context: Context) {
        expApps.postValue(XposedUtils.getExpApps(context))
    }
    fun refreshActiveState(context: Context) {
        isActive.postValue(XposedUtils.isExpModuleActive(context))
    }

    fun refreshStatus(reason: String = "") {
        needRefreshStatus.postValue(reason)
    }

    fun refreshMotionAwakeState(context: Context) {
        val enabled = Settings.System.getInt(context.contentResolver, "prox_wake_enabled", 0) != 0
        isMotionAwakeEnabled.value = enabled
    }
}