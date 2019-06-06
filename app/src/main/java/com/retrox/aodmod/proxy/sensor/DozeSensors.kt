package com.retrox.aodmod.proxy.sensor

import android.app.AndroidAppHelper
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.retrox.aodmod.MainHook

object DozeSensors {

    private val sensorManager: SensorManager by lazy {
        AndroidAppHelper.currentApplication().getSystemService(SensorManager::class.java)
    }

    private val sensorList = listOf<DozeSensor>( MotionCheck(), PickupCheck())

    private val sensorWakeLiveData = object : MutableLiveData<DozeSensorMessage>() {
        override fun onActive() {
            super.onActive()
            sensorList.forEach {
                it.check()
            }
        }

        override fun onInactive() {
            super.onInactive()
            sensorList.forEach {
                it.unCheck()
            }
        }
    }

    fun getSensorWakeLiveData(): LiveData<DozeSensorMessage> = sensorWakeLiveData

    enum class DozeSensorMessage {
        PICK_UP, MOTION_UP, PICK_DROP
    }

    class CustomProximityCheck : SensorEventListener, DozeSensor {
        override fun unCheck() {
            sensorManager.unregisterListener(this)
        }

        override fun check() {
            val sensor = sensorManager.getDefaultSensor(33171025) ?: return
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            if (event.values.isEmpty()) return
//            MainHook.logD("CustomProximityCheck: ${event.values.toList()}")
        }

    }

    class MotionCheck : SensorEventListener, DozeSensor {
        override fun unCheck() {
            sensorManager.unregisterListener(this)
        }

        override fun check() {
            val sensor = sensorManager.getDefaultSensor(33171028) ?: return
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            if (event.values.isEmpty()) return
//            MainHook.logD("MotionCheck: ${event.values.toList()}")

            if (event.values[0] == 1.0f /*&& event.values[1] == 2.0f*/) {
                sensorWakeLiveData.postValue(DozeSensors.DozeSensorMessage.MOTION_UP)
            }
        }

    }

    class PickupCheck : SensorEventListener, DozeSensor {
        override fun unCheck() {
            sensorManager.unregisterListener(this)
        }

        override fun check() {
            val sensor = sensorManager.getDefaultSensor(33171026) ?: return
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            if (event.values.isEmpty()) return
//            MainHook.logD("PickupCheck: ${event.values.toList()}")
            if (event.values[0] == 1.0f) {
                sensorWakeLiveData.postValue(DozeSensors.DozeSensorMessage.PICK_UP)
            } else {
                sensorWakeLiveData.postValue(DozeSensors.DozeSensorMessage.PICK_DROP)
            }
        }

    }

    private interface DozeSensor {
        fun check()
        fun unCheck()
    }
}

