package it2051229.grocery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import it2051229.grocery.entities.Grocery;
import it2051229.grocery.entities.GroceryItem;

public class AddGroceryItemActivity extends AppCompatActivity {

    private Grocery grocery;

    /**
     * Initialize the stuff we need when adding a new grocery item
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grocery_item);

        grocery = (Grocery) getIntent().getSerializableExtra("grocery");

        // Initialize the stuff for auto complete
        Set<String> itemNamesSet = new HashSet<>();
        Set<String> sellersSet = new HashSet<>();
        Set<String> unitsSet = new HashSet<>();

        grocery.getItemNamesSellersUnits(itemNamesSet, sellersSet, unitsSet);

        String[] itemNames = new String[itemNamesSet.size()];
        String[] sellers = new String[sellersSet.size()];
        String[] units = new String[unitsSet.size()];

        itemNames = itemNamesSet.toArray(itemNames);
        sellers = sellersSet.toArray(sellers);
        units = unitsSet.toArray(units);

        AutoCompleteTextView itemNameTextView = findViewById(R.id.autoCompleteTextViewName);
        AutoCompleteTextView sellerTextView = findViewById(R.id.autoCompleteTextViewSeller);
        AutoCompleteTextView unitTextView = findViewById(R.id.autoCompleteTextViewUnit);

        itemNameTextView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, itemNames));
        sellerTextView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sellers));
        unitTextView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, units));
    }

    /**
     * Validate the inputs and attempt to add a new grocery item
     */
    public void buttonAddGroceryItemTapped(View view) {
        String name = ((AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewName)).getText().toString().trim();
        String seller = ((AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewSeller)).getText().toString().trim();
        String strPrice = ((EditText) findViewById(R.id.editTextPrice)).getText().toString();
        String strQuantity = ((EditText) findViewById(R.id.editTextQuantity)).getText().toString();
        String unit = ((AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewUnit)).getText().toString();

        // Validate all fields
        if(name.isEmpty() || seller.isEmpty() || strPrice.isEmpty() || strQuantity.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make sure prices and quantities makes sense
        double price;
        double quantity;

        try {
            price = Double.parseDouble(strPrice);
            quantity = Double.parseDouble(strQuantity);

            if(price < 0 || quantity < 0)
                throw new Exception();
        } catch(Exception e) {
            Toast.makeText(this, "Price and/or quantity should be a positive decimal value.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the new grocery item and finish this activity
        GroceryItem groceryItem = new GroceryItem(name, seller, price, quantity, unit);

        if(grocery.searchGroceryItem(groceryItem)) {
            Toast.makeText(this, "This grocery item already exists.", Toast.LENGTH_SHORT).show();
            return;
        }

        grocery.addGroceryItem(groceryItem);

        Intent intent = getIntent();
        intent.putExtra("grocery", grocery);
        intent.putExtra("groceryItem", groceryItem);
        setResult(RESULT_OK, intent);
        finish();
    }
}
