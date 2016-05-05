package commacreations.apps.paintingpro;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private String _productReference = "";
    private String _agencyPhoneNumber = "+33445667788";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        setToolBar();
        getProductDetails();
        addListenerOnPurchaseProductButton();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked.
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Détails produit");
    }

    // Get product details from local json file.
    private void getProductDetails() {
        LocalJsonReader localJsonReader = new LocalJsonReader();
        try {
            InputStream is = getAssets().open("products.json");
            showProductDetails(localJsonReader.getDataFromJsonFile(is));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Show first product details as an example.
    private void showProductDetails(ArrayList<HashMap<String, String>> productsList) {
        TextView referenceValueTextView = (TextView)findViewById(R.id.referenceValueTextView);
        TextView categoryValueTextView = (TextView)findViewById(R.id.categoryValueTextView);
        TextView applicationValueTextView = (TextView)findViewById(R.id.applicationValueTextView);
        TextView dilutedTextView = (TextView)findViewById(R.id.dilutedValueTextView);
        TextView covValueTextView = (TextView)findViewById(R.id.covValueTextView);
        TextView emissionValueTextView = (TextView)findViewById(R.id.emissionValueTextView);
        _productReference = (String) productsList.get(0).get("reference");
        referenceValueTextView.setText((String) productsList.get(0).get("reference"));
        categoryValueTextView.setText((String) productsList.get(0).get("category"));
        applicationValueTextView.setText((String) productsList.get(0).get("application"));
        dilutedTextView.setText((String) productsList.get(0).get("diluted"));
        covValueTextView.setText((String) productsList.get(0).get("cov"));
        emissionValueTextView.setText((String) productsList.get(0).get("emission"));
    }

    private void addListenerOnPurchaseProductButton() {
        Button purchaseProductButton = (Button)findViewById(R.id.purchaseProductButton);
        purchaseProductButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phoneNumber = getMyPhoneNumber();
                if (phoneNumber == null) {
                    phoneNumber = "+334422222222";
                }
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + _agencyPhoneNumber));
                smsIntent.putExtra("sms_body", "Référence : " + _productReference + "\n" + "Numéro de téléphone : " + phoneNumber);
                try {
                    startActivity(smsIntent);
                } catch (ActivityNotFoundException e) {
                    // SMS functionnality doesnt exist.
                    showAlertMessageThisFunctionnalityDoesntExist();
                }
            }
        });
    }

    private String getMyPhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.left_out);
    }

}
