package it2051229.grocery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import it2051229.grocery.entities.FilteredItem;
import it2051229.grocery.entities.PriceAdjustment;

public class AddOrReduceCostActivity extends AppCompatActivity {

    private int adjustmentMode;
    private int fixedOrPercentage;
    private FilteredItem filteredItem;

    /**
     * Initialize the stuff we for price adjustment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_reduce_cost);

        Bundle extras = getIntent().getExtras();

        adjustmentMode = extras.getInt("adjustmentMode");
        filteredItem = (FilteredItem) extras.getSerializable("filteredItem");
        fixedOrPercentage = PriceAdjustment.FIXED_COST;

        ((TextView) findViewById(R.id.textViewGroceryItem)).setText(filteredItem.toString());

        if(adjustmentMode == PriceAdjustment.ADDED_COST) {
            setTitle("Add Cost");
            ((TextView)findViewById(R.id.textViewDescription)).setText("Cost Description (Shipping, tax, etc.)");
            ((Button)findViewById(R.id.buttonAddOrReduce)).setText("Add Cost");
        } else if(adjustmentMode == PriceAdjustment.REDUCED_COST) {
            setTitle("Reduce Cost");
            ((TextView)findViewById(R.id.textViewDescription)).setText("Cost Description (Discount, sale, etc.)");
            ((Button)findViewById(R.id.buttonAddOrReduce)).setText("Reduce Cost");
        }
    }

    /**
     * Update the user interface telling the user to enter a fixed cost value
     */
    public void radioButtonFixedCostTapped(View view) {
        ((TextView)findViewById(R.id.textViewValue)).setText("Fixed Cost Value");
        fixedOrPercentage = PriceAdjustment.FIXED_COST;
    }

    /**
     * Update the user interface telling the user to enter a percentage cost value
     */
    public void radioButtonPercentageCostTapped(View view) {
        ((TextView)findViewById(R.id.textViewValue)).setText("Percentage Cost Value");
        fixedOrPercentage = PriceAdjustment.PERCENTAGE;
    }

    /**
     * Add or reduce the cost based on the entered input
     */
    public void buttonAddOrReduceCostTapped(View view) {
        String costDescription = ((EditText)findViewById(R.id.editTextDescription)).getText().toString().trim();
        String strCostValue = ((EditText)findViewById(R.id.editTextValue)).getText().toString().trim();

        // Validate the entered values
        if(costDescription.isEmpty() || strCostValue.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        double costValue;

        // Make sure cost values is positive
        try {
            costValue = Double.parseDouble(strCostValue);

            if(costValue <= 0)
                throw new Exception();
        } catch(Exception e) {
            Toast.makeText(this, "Value should be a positive value.", Toast.LENGTH_SHORT).show();
            return;
        }

        // For percentage make sure its up to 100% only
        if(fixedOrPercentage == PriceAdjustment.PERCENTAGE && costValue > 100) {
            Toast.makeText(this, "Percentage should not exceed 100%", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the price adjustment for the filtered item
        PriceAdjustment adjustment = new PriceAdjustment(adjustmentMode, fixedOrPercentage, costDescription, costValue);
        filteredItem.getPriceAdjustments().add(adjustment);

        // We're done
        Intent intent = getIntent();
        intent.putExtra("filteredItem", filteredItem);
        setResult(RESULT_OK, intent);
        finish();
    }
}

