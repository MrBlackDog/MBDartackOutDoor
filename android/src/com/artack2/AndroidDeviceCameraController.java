package com.artack2;

import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;

import com.artack2.interfaces.DeviceCameraControl;


public class AndroidDeviceCameraController implements DeviceCameraControl {

    private final AndroidLauncher activity;
    private CameraSurface cameraSurface;
    private boolean cameraPrepared=false;

    AndroidDeviceCameraController(AndroidLauncher activity) {
        this.activity = activity;
    }

    @Override
    public synchronized void prepareCamera() {
        if (cameraSurface == null) {
            cameraSurface = new CameraSurface(activity);
        }
        if(!cameraPrepared) {
            cameraPrepared = true;
            activity.addContentView(cameraSurface, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public synchronized void startPreview() {
        // ... Начинаем превью. Теперь камера смартфона постоянно выводит
        // изображения на cameraSurface
        if (cameraSurface != null && cameraSurface.getCamera() != null) {
            System.out.println(cameraSurface.getCamera());
            cameraSurface.getCamera().startPreview();
        }
    }

    @Override
    public synchronized void stopPreview() {
        // stop previewing.
        if (cameraSurface != null) {
            ViewParent parentView = cameraSurface.getParent();
            if (parentView instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) parentView;
                viewGroup.removeView(cameraSurface);
                //cameraPrepared=false;
            }
            if (cameraSurface.getCamera() != null) {
                cameraSurface.getCamera().stopPreview();
            }
        }
    }

    @Override
    public synchronized float getVertViewAngle(){
        if (cameraSurface != null && cameraSurface.getCamera() != null) {
            return cameraSurface.getCamera().getParameters().getVerticalViewAngle();
        }
        return 0f;
    }

    @Override
    public synchronized float getHorizViewAngle(){
        if (cameraSurface != null && cameraSurface.getCamera() != null) {
            return cameraSurface.getCamera().getParameters().getHorizontalViewAngle();
        }
        return 0f;
    }

    @Override
    public void prepareCameraAsync() {
        Runnable r = this::prepareCamera;
        activity.post(r);
    }

    @Override
    public synchronized void startPreviewAsync() {
        Runnable r = this::startPreview;
        activity.post(r);
    }

    @Override
    public synchronized void stopPreviewAsync() {
        Runnable r = () -> stopPreview();
        activity.post(r);
    }

    @Override
    public boolean isReady() {
        return cameraSurface != null && cameraSurface.getCamera() != null;
    }
}
