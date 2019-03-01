package com.retrox.aodmod.app.state

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.retrox.aodmod.app.XposedUtils

object AppState {
    val isActive = MutableLiveData<Boolean>()
    val expApps = MutableLiveData<List<String>>()
    val sleepModeState = MutableLiveData<Boolean>()
    val aodModeState = MutableLiveData<String>()

    fun refreshExpApps(context: Context) {
        expApps.postValue(XposedUtils.getExpApps(context))
    }
    fun refreshActiveState(context: Context) {
        isActive.postValue(XposedUtils.isExpModuleActive(context))
    }
}