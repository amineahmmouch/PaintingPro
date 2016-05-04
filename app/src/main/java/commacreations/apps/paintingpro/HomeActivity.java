package commacreations.apps.paintingpro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;

import static android.location.LocationManager.GPS_PROVIDER;

public class HomeActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    protected LocationManager _locationManager = null;
    protected LocationClient _locationClient = null;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Create the LocationRequest object.
        _locationClient = new LocationClient(this, this, this);

        _locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        _locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);

        addListenerOnScannerButton();
        addListenerOnCallAgencyButton();

    }

    public void getUserLocation() {
        Location currentLocation = _locationClient.getLastLocation();
        String msg = Double.toString(currentLocation.getLatitude()) + "," +
                Double.toString(currentLocation.getLongitude());
        Log.i("currentLocation", msg);
        getNearestAgency(currentLocation);
    }

    private void addListenerOnScannerButton() {
        Button scannerButton = (Button)findViewById(R.id.scannerButton);
        scannerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(HomeActivity.this, ProductDetailsActivity.class);
                startActivity(i);
            }
        });
    }

    private void addListenerOnCallAgencyButton() {
        Button callAgencyButton = (Button)findViewById(R.id.callAgencyButton);
        callAgencyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!_locationManager.isProviderEnabled(GPS_PROVIDER)) {
                    showAlertMessageGpsIsDisabled();
                } else {
                    getUserLocation();
                }
            }
        });
    }

    // Get the nearest agency phone number. I supposed that i have an array of locations.
    private void getNearestAgency(Location location) {
        String[] agenciesCoordinates = getResources().getStringArray(R.array.agencies_coordinates);
        String[] agenciesPhones = getResources().getStringArray(R.array.agencies_phones);
        float lastDistance = 1000000000;
        int index = 0;
        for(int i = 0; i < agenciesCoordinates.length; i++) {
            String[] parts = agenciesCoordinates[i].split(",");
            Location agencyLocation = new Location("Service Provider");
            agencyLocation.setLatitude(Double.parseDouble(parts[0]));
            agencyLocation.setLongitude(Double.parseDouble(parts[1]));
            float newDistance = location.distanceTo(agencyLocation);
            if (lastDistance > newDistance) {
                lastDistance = newDistance;
                index = i;
            }
            Log.i("Distance", "" + newDistance);
        }
        Log.i("index", "" + index);
        callNearestAgency(agenciesPhones[index]);
    }

    private void callNearestAgency(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
        try {
            startActivity(callIntent);
        } catch (ActivityNotFoundException e) {
            // CALL functionnality doesnt exist.
            showAlertMessageThisFunctionnalityDoesntExist();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connect the client.
        _locationClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnect the client.
        _locationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status.
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        // Display the connection status.
        //Toast.makeText(this, "Disconnected. Please re-connect.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Display the error code on failure.
        //Toast.makeText(this, "Connection Failure : " + connectionResult.getErrorCode(),Toast.LENGTH_SHORT).show();
    }

    private void showAlertMessageGpsIsDisabled() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Votre GPS est désactivé, voulez vous l'activez?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlertMessageThisFunctionnalityDoesntExist() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cette fonctionnalite n'existe pas dans votre appareil.")
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}