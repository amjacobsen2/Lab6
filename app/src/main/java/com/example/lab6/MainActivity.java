package com.example.lab6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.security.Permissions;

public class MainActivity extends AppCompatActivity {

    private final LatLng mDestinationLatLng = new LatLng(43.0757,-89.404);
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private static final int HIGH_ACCURACY_LOCATION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            //code to display marker
            mMap.addMarker(new MarkerOptions()
                .position(mDestinationLatLng)
                .title("Bascom Hall"));
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        displayMyLocation();
    }

    private void displayMyLocation() {
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            mFusedLocationProviderClient.getCurrentLocation(HIGH_ACCURACY_LOCATION, null)
                    .addOnCompleteListener(this, task -> {
                        Location mLastKnownLocation = task.getResult();
                        if (task.isSuccessful() && mLastKnownLocation != null) {
                            mMap.addPolyline(new PolylineOptions().add(
                                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()),
                                    mDestinationLatLng));
                            float zoomLevel = 14.5f; //This goes up to 21
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDestinationLatLng, zoomLevel));
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayMyLocation();
            }
        }
    }
}