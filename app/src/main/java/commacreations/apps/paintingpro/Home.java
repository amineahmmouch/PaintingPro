package commacreations.apps.paintingpro;

import android.app.Activity;
import android.app.AlertDialog;
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

public class Home extends Activity implements LocationListener {

    protected LocationManager _locationManager = null;
    protected Location _myLastLocation = null;

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

        initialiseLocationManager();
        addListenerOnScannerButton();
        addListenerOnCallAgencyButton();

    }

    public void initialiseLocationManager() {
        _locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void addListenerOnScannerButton() {
        Button sendInfosButton = (Button)findViewById(R.id.scannerButton);
        sendInfosButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(Home.this, ProductDetails.class);
                startActivity(i);
            }
        });
    }

    private void addListenerOnCallAgencyButton() {
        Button sendInfosButton = (Button)findViewById(R.id.scannerButton);
        sendInfosButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!_locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    showAlertMessageGpsIsDisabled();
                } else {
                    getNearestAgency(_myLastLocation);
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
        }
        callNearestAgency(agenciesPhones[index]);
    }

    private void callNearestAgency(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
        startActivity(callIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        _myLastLocation = new Location(location);
        Log.i("Coordinates : ", location.getLatitude() + " " + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

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
                .setNegativeButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}