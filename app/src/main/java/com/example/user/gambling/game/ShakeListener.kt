package com.example.user.gambling.game

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ShakeListener(context: Context) : SensorEventListener {

    private val shakeThreshold = 800 //defines how strong the shake must be, to be detected

    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var shakeListener: OnShakeListener? = null
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastX = -1.0f
    private var lastY = -1.0f
    private var lastZ = -1.0f
    private var lastUpdate = 0L
    private var lastShake = 0L


    interface OnShakeListener {
        fun onShake()
        fun onShakeStop()
    }

    fun setOnShakeListener(listener: OnShakeListener) {
        shakeListener = listener
    }

    fun resume() {
        val supported = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        if (!supported) {
            sensorManager.unregisterListener(this, sensor)
            throw UnsupportedOperationException("Accelerometer not supported")
        }
    }

    fun pause() {
        sensorManager.unregisterListener(this, sensor)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val mySensor = event.sensor

            if (mySensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val curTime = System.currentTimeMillis()

                if (curTime - lastUpdate > 100) {
                    val diffTime = curTime - lastUpdate
                    lastUpdate = curTime

                    val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                    if (speed > shakeThreshold) {
                        shakeListener?.onShake()
                        lastShake = curTime
                    } else if (curTime - lastShake > 400) {
                        shakeListener?.onShakeStop()
                    }

                    lastX = x
                    lastY = y
                    lastZ = z
                }
            }
        }
    }
}