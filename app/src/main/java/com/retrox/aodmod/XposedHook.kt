package com.retrox.aodmod

import android.util.Log
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedHook : IXposedHookZygoteInit, IXposedHookLoadPackage {
    init {
        Log.d("AODMOD", "init")
    }
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        MainHook.initZygote(startupParam)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        MainHook.handleLoadPackage(lpparam)
    }

    fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        MainHook.handleInitPackageResources(resparam)
    }

}