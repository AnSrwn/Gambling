package com.example.user.gambling.game

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * This class is used to detect if the device is being shacked or not.
 * You can override the functions in the interface OnShakeListener to specify what to do in the
 * specific situations.
 * You can change the sensitivity of the ShakeListener by changing shakeThreshold.
 */
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


    /**
     * Override the following functions to specify what should happen if the device is being
     * shaken (onShake), when the shake stopped (onShakeStop) and when the ShakeLister resume.
     */
    interface OnShakeListener {
        fun onShake()
        fun onShakeStop()
        fun onResume()
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
        shakeListener?.onResume()
    }

    fun pause() {
        sensorManager.unregisterListener(this, sensor)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    /**
     * This method gets the recent x, y and z values and calculates the speed of the moving device.
     * Depending on the speed it is estimated if the device is shaken or if the the shake stopped,
     * and the related function is called.
     * @param event provides the recent x, y and z values, every time they changed.
     */
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
                    } else if (curTime - lastShake > 1000) {
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