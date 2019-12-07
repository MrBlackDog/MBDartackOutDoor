package com.artack2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.badlogic.gdx.math.Vector3;
import com.google.location.lbs.gnss.gps.pseudorange.Ecef2LlaConverter;
import com.google.location.lbs.gnss.gps.pseudorange.Lla2EcefConverter;

import java.io.IOException;



//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity {

    Ecef2LlaConverter.GeodeticLlaValues geodeticLlaValues;
    int PERMISSION_REQUEST_CODE;
    private Context mContext     = this;

    TextView tvEnabledGPS;
    TextView tvStatusGPS;
    TextView tvLocationGPS;
    TextView tvEnabledNet;
    TextView tvStatusNet;
    TextView tvLocationNet;

    private LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //checkPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if(Cam.startPos != null)
            {
                Intent intent = new Intent(mContext, AndroidLauncher.class);
                mContext.startActivity(intent);
            }
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvLocationGPS.setText(formatLocation(location));
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
        }
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        geodeticLlaValues  =new Ecef2LlaConverter.GeodeticLlaValues( Math.toRadians(location.getLatitude()), Math.toRadians(location.getLongitude()),location.getAltitude());
       double[] positionEcefMeters = Lla2EcefConverter.convertFromLlaToEcefMeters(geodeticLlaValues);
        Cam.startPos = new Vector3( (float) positionEcefMeters[0], (float) positionEcefMeters[1], (float) positionEcefMeters[2]);
       return String.format(
                "Coordinates: x = %1$.4f, y = %2$.4f, z = %3$.4f",
                positionEcefMeters[0], positionEcefMeters[1],positionEcefMeters[2]);
       /* return String.format(
                "Coordinates: lat = %1$.4, lon = %2$.4f, alt = %2$.4f",
               location.getLatitude(), location.getLongitude(), location.getAltitude());*/
    }




    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvEnabledNet.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));

    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };

 /*   public void checkPermission(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                  /*  ||ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Permission request").setMessage(R.string.Request_Permission).setCancelable(false)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        MainActivity.this.permission();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                //  perm();
            }
        }
        // Permission has already been granted, continue
    }

  /*  public void permission(){
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
                builder.setTitle(R.string.Permission_Error);  // заголовок
                builder.setMessage(R.string.Permission_Explanation); // сообщение
                builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        MainActivity.this.RequestPermission();
                    }
                });
                builder.setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        System.exit(0);
                    }
                });
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
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA
                        //  Manifest.permission.ACCESS_FINE_LOCATION,
                        //Manifest.permission.ACCESS_COARSE_LOCATION},
                },PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) // Permissions получены
                return;
            else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA))
                // Permissions не получены, закрываем приложение
                permission();
            else
                System.exit(0);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);    }*/
}