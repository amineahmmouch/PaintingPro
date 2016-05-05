package commacreations.apps.paintingpro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class ProductSearchActivity extends AppCompatActivity implements OnItemSelectedListener {

    private String[] _productCategories = null;
    private TextView _referenceValueTextView = null;
    private TextView _categoryValueTextView = null;
    private TextView _applicationValueTextView = null;
    private TextView _dilutedTextView = null;
    private TextView _covValueTextView = null;
    private TextView _emissionValueTextView = null;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        setToolBar();
        initialiseProductsDetailsTextViews();
        setupCategoryChoiceSpinner();
        setupAutoCompleteTextViewSearch("BTP");
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

    private void setupAutoCompleteTextViewSearch(String category) {
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        List<Product> products = Product.find(Product.class, "category = ?", category);
        //List<Product> products = Product.findWithQuery(Product.class, "Select * from Product where Category = ?", category);
        String[] productsReferencesArray = new String[products.size()];
        for (int i = 0; i < products.size(); i++) {
            productsReferencesArray[i] = products.get(i).reference;
            Log.i("products", "" + products.get(i).reference);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productsReferencesArray);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String reference = (String)parent.getItemAtPosition(position);
                Log.i("selection", reference);
                //List<Product> product = Product.find(Product.class, "Reference = ?", reference);
                List<Product> product = Product.findWithQuery(Product.class, "Select * from Product where Reference = ?", reference);
                showProductDetails(product);
            }
        });
    }

    private void initialiseProductsDetailsTextViews() {
        _referenceValueTextView = (TextView)findViewById(R.id.referenceValueTextView);
        _categoryValueTextView = (TextView)findViewById(R.id.categoryValueTextView);
        _applicationValueTextView = (TextView)findViewById(R.id.applicationValueTextView);
        _dilutedTextView = (TextView)findViewById(R.id.dilutedValueTextView);
        _covValueTextView = (TextView)findViewById(R.id.covValueTextView);
        _emissionValueTextView = (TextView)findViewById(R.id.emissionValueTextView);
    }

    private void showProductDetails(List<Product> product) {
        _referenceValueTextView.setText((String) product.get(0).reference);
        _categoryValueTextView.setText((String) product.get(0).category);
        _applicationValueTextView.setText((String) product.get(0).application);
        _dilutedTextView.setText((String) product.get(0).diluted);
        _covValueTextView.setText((String) product.get(0).cov);
        _emissionValueTextView.setText((String) product.get(0).emission);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using.
        Log.i("selected item", _productCategories[pos]);
        setupAutoCompleteTextViewSearch(_productCategories[pos]);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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