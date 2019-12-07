package com.artack2.orientation;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.badlogic.gdx.math.Quaternion;

import java.util.ArrayList;
import java.util.List;

/**
 * Classes implementing this interface provide an orientation of the device
 * either by directly accessing hardware, using Android sensor fusion or fusing
 * sensors itself.
 *
 * @author Alexander Pacha
 * 
 */
public abstract class OrientationProvider implements SensorEventListener, com.artack2.interfaces.OrientationProvider {
    /**
     * Sync-token for syncing read/write to sensor-data from sensor manager and
     * fusion algorithm
     */
    final Object synchronizationToken = new Object();
    final Object synchronizationToken2 = new Object();

    /**
     * The list of sensors used by this provider
     */
    List<Sensor> sensorList = new ArrayList<>();

    /**
     * The quaternion that holds the current rotation
     */
    final Quaternion currentOrientationQuaternion;

    /**
     * The sensor manager for accessing android sensors
     */
    private SensorManager sensorManager;

    /**
     * Initialises a new OrientationProvider
     * 
     * @param sensorManager
     *            The android sensor manager
     */
    OrientationProvider(SensorManager sensorManager) {
        this.sensorManager = sensorManager;

        // Initialise with identity
        currentOrientationQuaternion = new Quaternion();
    }

    /**
     * Starts the sensor fusion (e.g. when resuming the activity)
     */
    public void start() {
        // enable our sensor when the activity is resumed, ask for
        // 10 ms updates.
        for (Sensor sensor : sensorList) {
            // enable our sensors when the activity is resumed, ask for
            // 20 ms updates (Sensor_delay_game)
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    /**
     * Stops the sensor fusion (e.g. when pausing/suspending the activity)
     */
    public void stop() {
        // make sure to turn our sensors off when the activity is paused
        for (Sensor sensor : sensorList) {
            sensorManager.unregisterListener(this, sensor);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not doing anything
    }

    /**
     * Get the current rotation of the device in the quaternion format (vector4f)
     */
    public void getQuaternion(Quaternion quaternion) {
        synchronized (synchronizationToken) {
            quaternion.set(currentOrientationQuaternion);
        }
    }
}
