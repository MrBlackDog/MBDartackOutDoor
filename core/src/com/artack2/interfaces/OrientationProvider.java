package com.artack2.interfaces;

import com.badlogic.gdx.math.Quaternion;

public interface OrientationProvider {

    /**
     * Starts the sensor fusion (e.g. when resuming the activity)
     */
    public void start();

    /**
     * Stops the sensor fusion (e.g. when pausing/suspending the activity)
     */
    public void stop();

    /**
     * Get the current rotation of the device in the quaternion format (vector4f)
     */
    public void getQuaternion(Quaternion quaternion);

}
