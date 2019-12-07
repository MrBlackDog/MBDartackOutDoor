package com.artack2.interfaces;

public interface DeviceCameraControl {

    // Synchronous interface
    void prepareCamera();

    void startPreview();

    void stopPreview();

    // Asynchronous interface - need when called from a non platform thread (GDX OpenGl thread)
    void startPreviewAsync();

    void stopPreviewAsync();

    boolean isReady();

    void prepareCameraAsync();

    float getVertViewAngle();

    float getHorizViewAngle();

}