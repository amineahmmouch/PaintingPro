package commacreations.apps.paintingpro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ProductSearchActivity extends AppCompatActivity implements OnItemSelectedListener {

    private String[] _productCategories = null;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        setToolBar();
        setupCategoryChoiceSpinner();
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
        getSupportActionBar().setTitle("Recherche produit");
    }

    private void setupCategoryChoiceSpinner() {
        Spinner productCategorySpinner = (Spinner) findViewById(R.id.productCategorySpinner);
        productCategorySpinner.setPrompt("Choisissez une catégorie :");
        _productCategories = getResources().getStringArray(R.array.product_categories);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.product_categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        productCategorySpinner.setAdapter(adapter);
        productCategorySpinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using.
        Log.i("selected item", _productCategories[pos]);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}