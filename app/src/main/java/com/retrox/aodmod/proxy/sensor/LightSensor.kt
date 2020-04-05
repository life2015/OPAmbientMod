package com.retrox.aodmod.proxy.sensor

import android.app.AndroidAppHelper
import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.retrox.aodmod.MainHook
import kotlin.math.abs


/**
 * 大概分这几个档次
 * 700+ 明亮 alpha 1.0f
 * 300 - 700 适中
 * 100 - 300 偏暗
 * 0 - 100 黑暗 alpha 0.4f
 */
object LightSensor {

    val lightSensorLiveData = object : MutableLiveData<Pair<Float/* Suggested alpha */, Float/* Current Lux */>>() {
        override fun onActive() {
            super.onActive()
            registerListener(AndroidAppHelper.currentApplication().applicationContext)
        }

        override fun onInactive() {
            super.onInactive()
            unRegisterListener(AndroidAppHelper.currentApplication().applicationContext)
        }
    }

    private var lastLight = 1.0f

    private val lightSensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
//                MainHook.logD("当前亮度 ${it.values[0]} lux data: ${it.values.toList()}")
                val lightness = it.values[0]
                val alpha = when {
                    lightness > 700f -> 1.0f
                    lightness > 100f -> 0.4f + 0.6f/* 最大差值 乘以比例 */ * (lightness - 100f) / 600f
                    lightness > 0f -> 0.2f + 0.2f * (lightness - 0f) / 100f
                    else -> 0.2f
                }

                val factor = 1.3f
                val temp = alpha * factor
                val suggestLightAlpha = if (temp > 1.0f) 1.0f else temp

                if (abs(suggestLightAlpha - lastLight) > 0.1f) {
                    lastLight = suggestLightAlpha
                    lightSensorLiveData.postValue(suggestLightAlpha to lightness)
                }
            }
        }

    }


    private fun registerListener(context: Context) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
//        sensorManager.registerListener(
//            rotationVectorListener,
//            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR/*, true*/),
//            SensorManager.SENSOR_DELAY_NORMAL
//        )


        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT, false)
        MainHook.logD("Light maxRange ${sensor.maximumRange}")

        sensorManager.registerListener(
            lightSensorListener,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun unRegisterListener(context: Context) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
//        sensorManager.unregisterListener(rotationVectorListener)
        sensorManager.unregisterListener(lightSensorListener)
    }
}