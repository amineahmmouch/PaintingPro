package commacreations.apps.paintingpro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

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

        checkFirstTimeLaunchToAddProductsToDatabase();
        addListenerOnScannerButton();
        addListenerOnProductSearchButton();
        addListenerOnCallAgencyButton();

    }

    private void checkFirstTimeLaunchToAddProductsToDatabase() {
        boolean firstRun = false;

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        firstRun = settings.getBoolean("FIRST_RUN", false);
        if (!firstRun) {
            // do the thing for the first time
            settings = getSharedPreferences("PREFS_NAME", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FIRST_RUN", true);
            editor.commit();

            addProductsToLocalDatabase();
        }
    }

    // Add products from local json file to local database.
    private void addProductsToLocalDatabase() {
        LocalJsonReader localJsonReader = new LocalJsonReader();
        try {
            InputStream is = getAssets().open("products.json");
            ArrayList<HashMap<String, String>> productsList = localJsonReader.getDataFromJsonFile(is);
            for (int i = 0; i < productsList.size(); i++) {
                Product product = new Product((String) productsList.get(i).get("reference"),
                        (String) productsList.get(i).get("category"),
                        (String) productsList.get(i).get("application"),
                        (String) productsList.get(i).get("diluted"),
                        (String) productsList.get(i).get("cov"),
                        (String) productsList.get(i).get("emission"));
                product.save();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
                overridePendingTransition(R.anim.right_in, R.anim.stable);
            }
        });
    }

    private void addListenerOnProductSearchButton() {
        Button productSearchButton = (Button)findViewById(R.id.productSearchButton);
        productSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(HomeActivity.this, ProductSearchActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.stable);
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