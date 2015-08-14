package com.example.shahab.roodio;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.logging.LogRecord;

/**
 * Created by shahab on 7/19/15.
 */

public class MainActivity extends Activity implements SensorEventListener,LocationListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    boolean mInitialized = false;
    private static final int CAR_THRESHOLD = 0;
    private static final int RUN_THRESHOLD = 0;
    private static final int JOG_THRESHOLD = 0;
    private final float NOISE = (float) 10.0;
    LocationManager locationManager;
    Location location;
    float netSpeed = 0;
    float gpsSpeed = 0;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                //No location service available
            }
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                        this);
            }
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                        this);
            }


    }


    @Override
    public void onLocationChanged(Location location) {
        String provider = location.getProvider();
        if (provider.equals("network")) {
            TextView speed = (TextView) findViewById(R.id.netSpeed);
            speed.setText("0");
            if (location.hasSpeed()) {
                netSpeed = location.getSpeed();
                speed.setText(Float.toString(netSpeed));
            } else {
                speed.setText("hasNoSpeed!");
            }
            TextView latNetwork = (TextView) findViewById(R.id.latNet);
            TextView longNetwork = (TextView) findViewById(R.id.longNet);
            latNetwork.setText(Double.toString(location.getLatitude()));
            longNetwork.setText(Double.toString(location.getLongitude()));
        }
        else {
            TextView speed = (TextView) findViewById(R.id.gpsSpeed);
            speed.setText("0");
            if (location.hasSpeed()) {
                gpsSpeed = location.getSpeed();
                speed.setText(Float.toString(gpsSpeed));
            } else {
                speed.setText("hasNoSpeed!");
            }
            TextView latGPS = (TextView) findViewById(R.id.latGPS);
            TextView longGPS = (TextView) findViewById(R.id.longGPS);
            latGPS.setText(Double.toString(location.getLatitude()));
            longGPS.setText(Double.toString(location.getLongitude()));
        }
        float speed = 0;
        TextView mood = (TextView) findViewById(R.id.mood);
        if (gpsSpeed > netSpeed) {
            speed = gpsSpeed;
        }
        else {
            speed = netSpeed;
        }
        //converting meter per second to mile per hour
        speed = Double.doubleToLongBits(speed * 2.23694);
        if (speed == 0.0)
        {
            mood.setText("Standing");
        }
        else if (speed < Double.doubleToLongBits(5.0))
        {
            mood.setText("Jogging");
        }
        else if (speed < Double.doubleToLongBits(10.0))
        {
            mood.setText("Running");
        }
        else
        {
            mood.setText("Driving");
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        TextView tvX = (TextView) findViewById(R.id.x_axis);
        TextView tvY = (TextView) findViewById(R.id.y_axis);
        TextView tvZ = (TextView) findViewById(R.id.z_axis);
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (!mInitialized) {
            last_x = x;
            last_y = y;
            last_z = z;
            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");
            mInitialized = true;

        } else {
            float deltaX = Math.abs(last_x - x);
            float deltaY = Math.abs(last_y - y);
            float deltaZ = Math.abs(last_z - z);
            if (deltaX < NOISE) deltaX = (float) 0.0;
            if (deltaY < NOISE) deltaY = (float) 0.0;
            if (deltaZ < NOISE) deltaZ = (float) 0.0;
            last_x = x;
            last_y = y;
            last_z = z;
            tvX.setText(Float.toString(deltaX));
            tvY.setText(Float.toString(deltaY));
            tvZ.setText(Float.toString(deltaZ));

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

    }

}


