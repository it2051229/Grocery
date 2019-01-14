package it2051229.grocery;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import it2051229.grocery.entities.Application;
import it2051229.grocery.entities.FilteredItem;
import it2051229.grocery.entities.Grocery;
import it2051229.grocery.entities.PriceAdjustment;

public class FilteredItemsActivity extends AppCompatActivity {

    private ArrayList<String> arrayListFilteredItems;
    private ArrayAdapter<String> arrayAdapterFilteredItems;

    private Grocery grocery;

    /**
     * Initialize stuff we need and display all filtered items.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_items);

        grocery = (Grocery) getIntent().getSerializableExtra("grocery");

        arrayListFilteredItems = new ArrayList<>();
        arrayAdapterFilteredItems = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListFilteredItems);

        ListView listViewFilteredItems = findViewById(R.id.listViewFilteredItems);
        listViewFilteredItems.setAdapter(arrayAdapterFilteredItems);

        // Create a context menu for the list view to be able to handle additional options
        registerForContextMenu(listViewFilteredItems);

        for(FilteredItem item : grocery.getFilteredItems())
            arrayAdapterFilteredItems.add(item.toString());
    }

    /**
     * Initialize the menu for this activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filtered_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle the event of the selected menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menuClearFilteredItems:
                menuClearFilteredItemsTapped();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Clear the filtered items
     */
    private void menuClearFilteredItemsTapped() {
        if(grocery.getFilteredItems().isEmpty())
            return;

        // Confirm for deletion of all filtered items
        new AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to remove all items on the filtered list?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    grocery.getFilteredItems().clear();
                    arrayAdapterFilteredItems.clear();

                    Application.saveData(FilteredItemsActivity.this, grocery);
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    /**
     * Show the options when a long click has been done on the list view
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add("Remove from Filtered List");
        menu.add("Add Cost");
        menu.add("Reduce Cost");
        menu.add("Reset Cost");
        menu.add("Cancel");
    }

    /**
     * Handle the context menu item that was selected
     */
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        int selectedIndex = info.position;

        FilteredItem filteredItem = grocery.getFilteredItems().get(selectedIndex);

        if(menuItem.getTitle().equals("Remove from Filtered List")) {
            removeFromFilteredListContextItemTapped(filteredItem, selectedIndex);
        } else if(menuItem.getTitle().equals("Add Cost")) {
            addCostContextItemTapped(filteredItem, selectedIndex);
        } else if(menuItem.getTitle().equals("Reduce Cost")) {
            reduceCostContextItemTapped(filteredItem, selectedIndex);
        } else if(menuItem.getTitle().equals("Reset Cost")) {
            resetCostContextItemTapped(filteredItem, selectedIndex);
        }

        return super.onContextItemSelected(menuItem);
    }

    /**
     * Remove all price adjustments set on the filtered item
     */
    private void resetCostContextItemTapped(FilteredItem filteredItem, int selectedIndex) {
        filteredItem.getPriceAdjustments().clear();
        grocery.getFilteredItems().set(selectedIndex, filteredItem);
        arrayListFilteredItems.set(selectedIndex, filteredItem.toString());
        arrayAdapterFilteredItems.notifyDataSetChanged();

        Application.saveData(this, grocery);
    }

    /**
     * Show the activity for adding a new cost to the grocery item
     */
    private void addCostContextItemTapped(FilteredItem filteredItem, int selectedIndex) {
        Intent intent = new Intent(this, AddOrReduceCostActivity.class);
        intent.putExtra("adjustmentMode", PriceAdjustment.ADDED_COST);
        intent.putExtra("filteredItem", filteredItem);
        intent.putExtra("selectedIndex", selectedIndex);
        startActivityForResult(intent, Application.ADD_OR_REDUCE_COST_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Show the activity for reducing the cost of a grocery item
     */
    private void reduceCostContextItemTapped(FilteredItem filteredItem, int selectedIndex) {
        Intent intent = new Intent(this, AddOrReduceCostActivity.class);
        intent.putExtra("adjustmentMode", PriceAdjustment.REDUCED_COST);
        intent.putExtra("filteredItem", filteredItem);
        intent.putExtra("selectedIndex", selectedIndex);
        startActivityForResult(intent, Application.ADD_OR_REDUCE_COST_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Remove the filtered item from filtered item list
     */
    private void removeFromFilteredListContextItemTapped(FilteredItem filteredItem, final int selectedIndex) {
        new AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Do you really want to remove the filtered item?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                // Delete from the grocery
                grocery.getFilteredItems().remove(selectedIndex);

                // Delete from the user interface
                arrayListFilteredItems.remove(selectedIndex);
                arrayAdapterFilteredItems.notifyDataSetChanged();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    /**
     * Handle the return call back of activities that returns results
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {
            case Application.ADD_OR_REDUCE_COST_ACTIVITY_REQUEST_CODE:
                processAddOrReduceActivityResult(resultCode, intent);
                break;
        }
    }

    /**
     * Update the display of the price adjusted filtered item
     */
    private void processAddOrReduceActivityResult(int resultCode, Intent intent) {
        if(resultCode != RESULT_OK)
            return;

        FilteredItem filteredItem = (FilteredItem) intent.getExtras().getSerializable("filteredItem");
        int listViewIndex = intent.getExtras().getInt("selectedIndex");
        arrayListFilteredItems.set(listViewIndex, filteredItem.toString());
        arrayAdapterFilteredItems.notifyDataSetChanged();
        grocery.getFilteredItems().set(listViewIndex, filteredItem);

        Application.saveData(this, grocery);
    }

    /**
     * Serialize the grocery to be loaded back on the parent activity
     */
    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("grocery", grocery);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
