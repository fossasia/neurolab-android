package io.neurolab.utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import io.neurolab.R;

public class LocationTracker {

    private String[] mapPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public static final int GPS_PERMISSION = 103;
    private static final int UPDATE_INTERVAL_IN_MILLISECONDS = 400;
    private static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private LocationManager locationManager;
    private Context context;
    private Location bestLocation;
    private String provider = LocationManager.GPS_PROVIDER;
    private AlertDialog gpsAlert;

    public LocationTracker(Context context, LocationManager locationManager) {
        this.context = context;
        this.locationManager = locationManager;

        makeGPSDialog();
    }

    private void makeGPSDialog() {
        gpsAlert = new AlertDialog.Builder(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert)
                .setTitle(R.string.gps_header)
                .setMessage(R.string.gps_info_msg)
                .setPositiveButton(R.string.ok_button, (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> Toast.makeText(context, "Recording without Location", Toast.LENGTH_SHORT).show())
                .create();
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            bestLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {/**/}

        @Override
        public void onProviderEnabled(String s) { /**/}

        @Override
        public void onProviderDisabled(String s) {
            if (!gpsAlert.isShowing()) {
                gpsAlert.show();
            }
        }
    };

    /**
     * @return the best location fetched
     */
    @SuppressLint("MissingPermission")
    public Location getDeviceLocation() {
        if (bestLocation == null) {
            if (PermissionUtils.checkRuntimePermissions(context, mapPermissions)) {
                locationManager.requestLocationUpdates(provider,
                        UPDATE_INTERVAL_IN_MILLISECONDS, MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        locationListener);
                return locationManager.getLastKnownLocation(provider);
            } else {
                return defaultLocation();
            }
        } else {
            return bestLocation;
        }
    }

    private Location defaultLocation() {
        Location l = new Location("");
        l.setLatitude(0.0);
        l.setLongitude(0.0);
        return l;
    }

    /**
     * Set location updates
     */
    @SuppressLint("MissingPermission")
    public void startCaptureLocation() {
        if (PermissionUtils.checkRuntimePermissions(context, mapPermissions)) {
            locationManager.requestLocationUpdates(provider, UPDATE_INTERVAL_IN_MILLISECONDS, MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    locationListener);
        } else {
            PermissionUtils.requestRuntimePermissions(context, mapPermissions, GPS_PERMISSION);
        }
    }
}

