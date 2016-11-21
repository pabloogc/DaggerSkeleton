package com.bq.daggerskeleton.sample.rotation;

public class RotationUtils {
    public static int getSensorDeviceOrientationCompensation(
            boolean isFacingFront,
            int deviceAbsoluteRotation,
            int sensorOrientation) {
        // Round sensor orientation value
        int roundedOrientation = ((deviceAbsoluteRotation) + 45) / 90 * 90;
        int rotation;
        if (isFacingFront) {
            rotation = (sensorOrientation + roundedOrientation + 360) % 360;
        } else {  // back-facing camera
            rotation = (sensorOrientation - roundedOrientation) % 360;
        }
        return rotation;
    }
}
