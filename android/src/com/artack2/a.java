package com.artack2;



import android.os.AsyncTask;
import android.widget.Toast;

import com.navigine.naviginesdk.NavigationThread;
import com.navigine.naviginesdk.NavigineSDK;



import static com.navigine.naviginesdk.LocationView.TAG;

class a extends AsyncTask<Void, Void, Boolean>
{

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
      //  Toast.makeText("Begin", LOCATION_ID, Toast.LENGTH_SHORT).show();
    }
    private static final int LOCATION_ID = 123;

    @Override
    protected Boolean doInBackground(Void... params)
    {
        return NavigineSDK.loadLocation(LOCATION_ID, 30) ?
                Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        if (result.booleanValue())
        {
            // Location is successully loaded
            // Do whatever you want here, e.g. you can start navigation
            NavigationThread navigation = NavigineSDK.getNavigation();
            if (navigation != null)
                navigation.setMode(NavigationThread.MODE_NORMAL);
        }
        else
        {
            // Error downloading location
            // Try again later or contact technical support
           // Log.d(TAG, "Error downloading location!");
        }
    }
}
//( new a()).execute();