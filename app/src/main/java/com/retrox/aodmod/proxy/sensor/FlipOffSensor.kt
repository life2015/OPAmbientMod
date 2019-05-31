package com.retrox.aodmod.proxy.sensor

import android.app.AndroidAppHelper
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.retrox.aodmod.MainHook
import de.robv.android.xposed.XposedHelpers

/**
 * 翻转息屏Sensor管理
 * 后来升级到口袋检测
 */
object FlipOffSensor {
    const val Flip_ON = "Flip_ON"
    const val Flip_OFF = "Flip_OFF"

    private const val z_divider = 0.3f
    private var farDivider = 80
    private var proximityMaxRange = 5.0f

    @Volatile
    private var flipStateInner = true // 为了避免读写冲突

    val flipSensorLiveData = object : MutableLiveData<FlipSensorData>() {
        override fun onActive() {
            super.onActive()
            value = FlipSensorData(Flip_ON)
            flipStateInner = true
            MainHook.logD("Flip Live Active")
            registerListener(AndroidAppHelper.currentApplication().applicationContext)
        }

        override fun onInactive() {
            super.onInactive()
            MainHook.logD("Flip Live InActive")
            unRegisterListener(AndroidAppHelper.currentApplication().applicationContext)
        }
    }

    // 测试翻转 暂时不检测这个
    private val rotationVectorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                // MainHook.logD("Sensor Event: ${it.values.toList().toString()}")
                val zValue = event.values.toList().getOrNull(2) ?: 1.0f
                if (zValue > z_divider && !flipStateInner) { // 扣着的时候要翻回来 发送开屏信号
                    flipStateInner = true
                    flipSensorLiveData.postValue(FlipSensorData(Flip_ON, event.values.toList()))
                }
                if (zValue < z_divider && flipStateInner) {
                    flipStateInner = false
                    flipSensorLiveData.postValue(FlipSensorData(Flip_OFF, event.values.toList())) // 扣回去的时候 关屏
                }
            }
        }

    }

    // 测试距离传感器
    private val proximityListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                MainHook.logD("proximity ${event.values.toList()}")

                val near = it.values[0] < proximityMaxRange // 第一个值小于 maxrange(一加6上是 5.0f) 说明太近(0f) 反之亦然
                if (!near && !flipStateInner) { // 距离变大 farVariable变小 发送开屏信号
                    flipStateInner = true
                    flipSensorLiveData.postValue(FlipSensorData(Flip_ON, event.values.toList()))
                }
                if (near && flipStateInner) {
                    flipStateInner = false
                    flipSensorLiveData.postValue(
                        FlipSensorData(
                            Flip_OFF,
                            event.values.toList()
                        )
                    ) // 距离变小 farVariable变大 关屏
                }

            }
        }

    }

    private fun registerListener(context: Context) {
        val sensorManager = context.getSystemService(SensorManager::class.java)

        val op7proximityList = sensorManager.getSensorList(Sensor.TYPE_PROXIMITY) // 一加7的距离传感器基本上没法用啊
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY, false) ?: op7proximityList.firstOrNull()

        if (sensor == null) return

        proximityMaxRange = sensor.maximumRange
        sensorManager.registerListener(
            proximityListener,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun unRegisterListener(context: Context) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
//        sensorManager.unregisterListener(rotationVectorListener)
        sensorManager.unregisterListener(proximityListener)
    }

    data class FlipSensorData(val suggestState: String, val sensorValues: List<Float>? = null)
}