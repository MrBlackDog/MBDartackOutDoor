package com.artack2;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
    private Camera camera;

    public CameraSurface(Context context ) {
        super( context );
        // We're implementing the Callback interface and want to get notified
        // about certain surface events.
        getHolder().addCallback( this );
        // We're changing the surface to a PUSH surface, meaning we're receiving
        // all buffer data from another component - the camera, in this case.
        getHolder().setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
    }

    public void surfaceCreated( SurfaceHolder holder ) {
        // Once the surface is created, simply open a handle to the camera hardware.
        camera = Camera.open();
        camera.setDisplayOrientation(90);
    }

    public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
        // This method is called when the surface changes, e.g. when it's size is set.
        // We use the opportunity to initialize the camera preview display dimensions.
        Camera.Parameters p = camera.getParameters();
        /*
        *  Нельзя выставлять таким образом, надо выбрать из списка заранее заданных previewSize
        * */
        List<int[]> frameRates = p.getSupportedPreviewFpsRange();
        //int last = frameRates.size() - 1;
        int minFps = (frameRates.get(0))[p.PREVIEW_FPS_MIN_INDEX];
        int maxFps = (frameRates.get(0))[p.PREVIEW_FPS_MAX_INDEX];
        p.setPreviewFpsRange(minFps, maxFps);
        //p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        // FOCUS_MODE_CONTINUOUS_VIDEO, если камера его поддерживает
        List<String> focusModes = p.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        camera.setParameters( p );

        // We also assign the preview display to this surface...
        try {
            camera.setPreviewDisplay( holder );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed( SurfaceHolder holder ) {
        // Once the surface gets destroyed, we stop the preview mode and release
        // the whole camera since we no longer need it.
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public Camera getCamera() {
        return camera;
    }

}
