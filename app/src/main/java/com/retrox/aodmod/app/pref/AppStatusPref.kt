package com.retrox.aodmod.app.pref

/**
 * 这个Pref主要是被对面ContentProvider操作的
 * 用来反向传递息屏的一些状态
 */
object AppStatusPref {
    var alwaysOnHookTimes by shared("ALWAYSONHOOKTIMES", 0)
    var systemHookTimes by shared("SYSTEMHOOKTIMES", 0)

}