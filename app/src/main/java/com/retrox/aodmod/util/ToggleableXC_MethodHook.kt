package com.retrox.aodmod.util

import com.retrox.aodmod.pref.XPref

class ToggleableXC_MethodHook(val innerHook: XC_MethodHook) : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        if(!XPref.getModuleState()) return
        innerHook.afterHookedMethod(param)
        super.afterHookedMethod(param)
    }

    override fun beforeHookedMethod(param: MethodHookParam) {
        if(!XPref.getModuleState()) return
        innerHook.beforeHookedMethod(param)
        super.beforeHookedMethod(param)
    }

}