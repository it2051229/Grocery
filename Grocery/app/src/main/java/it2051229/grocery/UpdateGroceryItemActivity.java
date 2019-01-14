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

public class UpdateGroceryItemActivity extends AppCompatActivity {
    private GroceryItem groceryItem;
    private Grocery grocery;

    /**
     * Initialize stuff we need for handling the updating of grocery item
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_grocery_item);

        Bundle extras = getIntent().getExtras();

        groceryItem = (GroceryItem) extras.getSerializable("groceryItem");
        grocery = (Grocery) extras.getSerializable("grocery");

        // Display the current details of the grocery
        AutoCompleteTextView itemNameTextView = findViewById(R.id.autoCompleteTextViewUpdateName);
        AutoCompleteTextView sellerTextView = findViewById(R.id.autoCompleteTextViewUpdateSeller);
        AutoCompleteTextView unitTextView = findViewById(R.id.autoCompleteTextViewUpdateUnit);

        itemNameTextView.setText(groceryItem.getName());
        sellerTextView.setText(groceryItem.getSeller());
        unitTextView.setText(groceryItem.getUnit());
        ((EditText)findViewById(R.id.editTextUpdatePrice)).setText(String.valueOf(groceryItem.getPrice()));
        ((EditText)findViewById(R.id.editTextUpdateQuantity)).setText(String.valueOf(groceryItem.getQuantity()));

        // Apply auto complete options to some of the fields
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

        itemNameTextView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, itemNames));
        sellerTextView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sellers));
        unitTextView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, units));
    }

    /**
     * Handle updating the grocery item
     */
    public void buttonUpdateGroceryItemTapped(View view) {
        String name = ((AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewUpdateName)).getText().toString().trim();
        String seller = ((AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewUpdateSeller)).getText().toString().trim();
        String strPrice = ((EditText) findViewById(R.id.editTextUpdatePrice)).getText().toString();
        String strQuantity = ((EditText) findViewById(R.id.editTextUpdateQuantity)).getText().toString();
        String unit = ((AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewUpdateUnit)).getText().toString();

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

        // Update the grocery item
        GroceryItem updatedGroceryItem = new GroceryItem(name, seller, price, quantity, unit);

        if(grocery.searchGroceryItem(updatedGroceryItem)) {
            Toast.makeText(this, "This grocery item already exists.", Toast.LENGTH_SHORT).show();
            return;
        }

        grocery.updateGroceryItem(groceryItem, updatedGroceryItem);

        Intent intent = getIntent();
        intent.putExtra("grocery", grocery);
        intent.putExtra("groceryItem", updatedGroceryItem);
        setResult(RESULT_OK, intent);
        finish();
    }
}
