package com.artack2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.graphics.*;
import android.widget.Toast;

import com.artack2.interfaces.DeviceCameraControl;
import com.artack2.orientation.ImprovedOrientationSensor2Provider;
import com.artack2.orientation.OrientationProvider;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.math.Vector3;
import com.google.location.lbs.gnss.gps.pseudorange.Ecef2LlaConverter;
import com.google.location.lbs.gnss.gps.pseudorange.Lla2EcefConverter;
import com.navigine.naviginesdk.LocationPoint;
import com.navigine.naviginesdk.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class AndroidLauncher extends AndroidApplication  {
	int PERMISSION_REQUEST_CODE;
	Activity act;
	private RectF mPinPointRect = null;
	private Context   mContext     = this;

	private LocationManager locationManager;
	Ecef2LlaConverter.GeodeticLlaValues geodeticLlaValues;

	public void post(Runnable r){handler.post(r);}

	@Override
	public Context getContext() {
		return super.getContext();
	}

	/** Navigine */


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	checkPermission();
		DeviceCameraControl cameraControl = new AndroidDeviceCameraController(this);

		OrientationProvider orientationProvider = new ImprovedOrientationSensor2Provider((SensorManager) getSystemService(Context.SENSOR_SERVICE));

		// Создание своего листенера данных с датчиков (поэтому useAccelerometer и т.п. не нужны)
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.a = 8;
		config.r = 8;
		config.g = 8;
		config.b = 8;
		//NavigationThread navigation = NavigineSDK.getNavigation();
//		SubLocation subLocation =  NavigineSDK.getNavigation().getLocation().getSubLocation(2);
//		Icon.arrayList.addAll(subLocation.venues);

			initialize(new Main(cameraControl, orientationProvider), config);

			if (graphics.getView() instanceof SurfaceView) {
				SurfaceView glView = (SurfaceView) graphics.getView();
				// force alpha channel - I'm not sure we need this as the GL surface is already using alpha channel
				glView.setZOrderOnTop(true);
				glView.getHolder().setFormat(PixelFormat.RGBA_8888);
			}

			// we don't want the screen to turn off during the long image saving process
			graphics.getView().setKeepScreenOn(true);

	/*	if (NavigineSDK.initialize(this.getContext(), USER_HASH, SERVER_URL)) {
			NavigineSDK.loadLocationInBackground(LOCATION_NAME, 30,
					new Location.LoadListener() {
						@Override
						public void onFinished() {
							NavigationThread navigation = NavigineSDK.getNavigation();
							SubLocation subLocation =  navigation.getLocation().getSubLocation(2);
							Icon.arrayList.addAll(subLocation.venues);
							/*for(int i = 0; i < subLocation.venues.size(); ++i)
							{
								Venue v = subLocation.venues.get(i);
								Icon.arrayList.addAll(v);
							}
							//for subLocation.venues.
							initialize(new Main(cameraControl,orientationProvider), config);
							if (graphics.getView() instanceof SurfaceView) {
								SurfaceView glView = (SurfaceView) graphics.getView();
								// force alpha channel - I'm not sure we need this as the GL surface is already using alpha channel
								glView.setZOrderOnTop(true);
								glView.getHolder().setFormat(PixelFormat.RGBA_8888);
							}

							// we don't want the screen to turn off during the long image saving process
							graphics.getView().setKeepScreenOn(true);
							//Intent intent = new Intent(mContext, Main.class);
							//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							//mContext.startActivity(intent);
						}

						@Override
						public void onFailed(int error) {
							//mStatusLabel.setText("Error downloading location 'Navigine Demo' (error " + error + ")! " +
							//	"Please, try again later or contact technical support");
						}

						@Override
						public void onUpdate(int progress) {
							//mStatusLabel.setText("Downloading location: " + progress + "%");
						}
					});*

		//initialize(new Main(orientationProvider), config);
        //NavigineSDK.initialize(this.getContext(), USER_HASH, SERVER_URL);

//        SubLocation subLocation =  mNavigation.getLocation().subLocations.get(2);




		}*/
	/*	DeviceCameraControl cameraControl = new AndroidDeviceCameraController(this);

		OrientationProvider orientationProvider = new ImprovedOrientationSensor2Provider((SensorManager) getSystemService(Context.SENSOR_SERVICE));

		// Создание своего листенера данных с датчиков (поэтому useAccelerometer и т.п. не нужны)

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.a = 8;
		config.r = 8;
		config.g = 8;
		config.b = 8;

        initialize(new Main(cameraControl,orientationProvider), config);
        //initialize(new Main(orientationProvider), config);

		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			// force alpha channel - I'm not sure we need this as the GL surface is already using alpha channel
			glView.setZOrderOnTop(true);
			glView.getHolder().setFormat(PixelFormat.RGBA_8888);
		}

		// we don't want the screen to turn off during the long image saving process
		graphics.getView().setKeepScreenOn(true);*/
	}

	public void checkPermission() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
			if (this.getContext().checkSelfPermission( Manifest.permission.CAMERA)
					!= PackageManager.PERMISSION_GRANTED
                 ||this.getContext().checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED ||
					this.getContext().checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(AndroidLauncher.this);
				builder.setTitle("Permission request").setMessage(R.string.Request_Permission).setCancelable(false)
						.setNegativeButton("ОК",
								(dialog, id) -> permission());
				AlertDialog alert = builder.create();
				alert.show();
				//  perm();
			}
		}
		setCameraNormal();
		// Permission has already been granted, continue
	}

	public void permission() {
		AlertDialog.Builder builder;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
		} else {
			builder = new AlertDialog.Builder(this);
		}
		{
			Log.e("PERMISSIONS", "NOT GRANTED");
			// Permission не предоставлены
			// Должны ли показать пользователю объяснение?
			if (this.shouldShowRequestPermissionRationale( Manifest.permission.CAMERA) ||
					this.shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_FINE_LOCATION) ||
							this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION))
			{
				builder.setTitle(R.string.Permission_Error);  // заголовок
				builder.setMessage(R.string.Permission_Explanation); // сообщение
				builder.setPositiveButton("Agree", (dialog, arg1) -> RequestPermission());
				builder.setNegativeButton("Disagree", (dialog, arg1) -> System.exit(0));
				builder.setCancelable(false);
				AlertDialog alert = builder.create();
				alert.show();
				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
			} else {
				Log.e("PERMISSIONS", "NOEXPLANATION");
				// No explanation needed; request the permission
				RequestPermission();
				// REQUEST_RESULT is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}
	}

	public void RequestPermission(){
		this.requestPermissions(new String[]
				{
				Manifest.permission.CAMERA
				//  Manifest.permission.ACCESS_FINE_LOCATION,
				//Manifest.permission.ACCESS_COARSE_LOCATION},
				}, PERMISSION_REQUEST_CODE);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) // Permissions получены
				return;
			else if (this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
				// Permissions не получены, закрываем приложение
				permission();
			else
				System.exit(0);
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private void setCameraNormal() {
		Main.mode = Main.Mode.normal;
	}

}
