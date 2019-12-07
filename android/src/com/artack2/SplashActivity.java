package com.artack2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.navigine.naviginesdk.DeviceInfo;
import com.navigine.naviginesdk.Location;
import com.navigine.naviginesdk.NavigationThread;
import com.navigine.naviginesdk.NavigineSDK;
import com.navigine.naviginesdk.SubLocation;
import com.navigine.naviginesdk.Venue;

import java.util.ArrayList;
import java.util.Locale;

public class SplashActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback
{
  private static final String TAG = "NAVIGINE.Demo";
  private Context mContext     = this;

  // NavigationThread instance
  private NavigationThread mNavigation            = null;
  private Location      mLocation                 = null;
  private DeviceInfo mDeviceInfo               = null; //

  private TextView mStatusLabel = null;
  public static ArrayList<Venue> list;
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    // Setting up NavigineSDK parameters
    NavigineSDK.setParameter(mContext, "debug_level", 2);
    NavigineSDK.setParameter(mContext, "actions_updates_enabled",  false);
    NavigineSDK.setParameter(mContext, "location_updates_enabled", true);
    NavigineSDK.setParameter(mContext, "location_loader_timeout",  60);
    NavigineSDK.setParameter(mContext, "location_update_timeout",  300);
    NavigineSDK.setParameter(mContext, "location_retry_timeout",   300);
    NavigineSDK.setParameter(mContext, "post_beacons_enabled",     true);
    NavigineSDK.setParameter(mContext, "post_messages_enabled",    true);
    
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_splash);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                         WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    mStatusLabel = (TextView)findViewById(R.id.splash__status_label);
    
    ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                                                           Manifest.permission.ACCESS_COARSE_LOCATION,
                                                           Manifest.permission.READ_EXTERNAL_STORAGE,
                                                           Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
  }

  @Override
  public void onBackPressed()
  {
    moveTaskToBack(true);
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[],
                                         int[] grantResults)
  {
    boolean permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)   == PackageManager.PERMISSION_GRANTED &&
                                 ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    boolean permissionStorage  = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED &&
                                 ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    switch (requestCode)
    {
      case 101:
        if (!permissionLocation || (D.WRITE_LOGS && !permissionStorage))
          finish();
        else
        {
          if (NavigineSDK.initialize(mContext, D.USER_HASH, D.SERVER_URL))
          {
            NavigineSDK.loadLocationInBackground(D.LOCATION_NAME, 30,
              new Location.LoadListener()
              {
                @Override
                public void onFinished()
                {
                  mNavigation = NavigineSDK.getNavigation();
                  mNavigation.setDeviceListener
                          (
                                  new DeviceInfo.Listener()
                                  {
                                    @Override public void onUpdate(DeviceInfo info) { //handleDeviceUpdate(info);
                                      mDeviceInfo = info;
                                      Cam.startPos.x=  mDeviceInfo.x;
                                      Cam.startPos.y=  mDeviceInfo.y;
                                      Cam.startPos.z=  0;
                                       }
                                  }
                          );
                 /* Cam.startPos.x=  mDeviceInfo.x;
                  Cam.startPos.y=  mDeviceInfo.y;
                  Cam.startPos.z=  0;*/
                  Icon.arrayList = new ArrayList<>(NavigineSDK.getNavigation().getLocation().getSubLocation(2927).venues.size());
                //  Icon.arrayList.add(NavigineSDK.getNavigation().getLocation().getSubLocation(2927).venues.get(3));
                 // Icon.arrayList.add(NavigineSDK.getNavigation().getLocation().getSubLocation(2927).venues.get(4));
                   Icon.arrayList.addAll(NavigineSDK.getNavigation().getLocation().getSubLocation(2927).venues);
                  Intent intent = new Intent(mContext, AndroidLauncher.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  mContext.startActivity(intent);
                }
                @Override
                public void onFailed(int error)
                {
                  mStatusLabel.setText("Error downloading location 'Navigine Demo' (error " + error + ")! " +
                                       "Please, try again later or contact technical support");
                }
                @Override
                public void onUpdate(int progress)
                {
                  mStatusLabel.setText("Downloading location: " + progress + "%");
                }
              });
          }
          else
          {
            mStatusLabel.setText("Error initializing NavigineSDK! Please, contact technical support");
          }
        }
        break;
    }
  }
}
