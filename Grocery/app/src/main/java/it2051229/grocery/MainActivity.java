package it2051229.grocery;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import it2051229.grocery.entities.Application;
import it2051229.grocery.entities.FilteredItem;
import it2051229.grocery.entities.Grocery;
import it2051229.grocery.entities.GroceryItem;

public class MainActivity extends AppCompatActivity {

    private List<GroceryItem> groceryItems;
    private ArrayList<String> arrayListGroceryItems;
    private ArrayAdapter<String> arrayAdapterGroceryItems;

    private Grocery grocery;

    /**
     * Initialize stuff we need before the activity starts
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the directory for the grocery files
        Application.DIRECTORY.mkdirs();

        arrayListGroceryItems = new ArrayList<>();
        arrayAdapterGroceryItems = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListGroceryItems);

        ListView listViewGroceryItems = findViewById(R.id.listViewGroceryItems);
        listViewGroceryItems.setAdapter(arrayAdapterGroceryItems);

        // Create a context menu for the list view to be able to update, delete, or add grocery items to the cart
        registerForContextMenu(listViewGroceryItems);

        // Initialize the grocery data from internal file
        loadData();

        // Load the grocery items to the list view
        groceryItems = new ArrayList<>(grocery.getGroceryItems());

        for(GroceryItem item : groceryItems)
            arrayAdapterGroceryItems.add(item.toString());

        // Initialize the search text field to listen and filter the grocery items
        final EditText editTextSearch = findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            // Display all grocery items containing the keyword
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the displayed grocery items
                groceryItems.clear();
                arrayAdapterGroceryItems.clear();

                // Do a filter
                String keyword = editTextSearch.getText().toString().trim();
                groceryItems.addAll(grocery.getGroceryItemsContaining(keyword));

                for(GroceryItem item : groceryItems)
                    arrayAdapterGroceryItems.add(item.toString());
            }

            // Nothing to do here
            @Override
            public void afterTextChanged(Editable s) { }

            // Nothing to do here
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        });
    }

    /**
     * Show the options when a long click has been done on the
     * list view
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add("Add to Filtered List");
        menu.add("Update");
        menu.add("Delete");
        menu.add("Cancel");
    }

    /**
     * Handle the context menu item that was selected
     */
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        int selectedIndex = info.position;

        GroceryItem groceryItem = groceryItems.get(selectedIndex);

        if(menuItem.getTitle().equals("Add to Filtered List")) {
            addToFilteredListContextItemTapped(groceryItem);
        } else if(menuItem.getTitle().equals("Update")) {
            updateContextItemTapped(groceryItem, selectedIndex);
        } else if(menuItem.getTitle().equals("Delete")) {
            deleteContextItemTapped(groceryItem, selectedIndex);
        }

        return super.onContextItemSelected(menuItem);
    }

    /**
     * Add the grocery item to the filtered list
     */
    private void addToFilteredListContextItemTapped(GroceryItem groceryItem) {
        grocery.getFilteredItems().add(new FilteredItem(groceryItem));
        Toast.makeText(this, "Grocery Item added to Filtered List.", Toast.LENGTH_SHORT).show();
        Application.saveData(this, grocery);
    }

    /**
     * Handle the delete of item from the list view and the grocery
     */
    private void deleteContextItemTapped(final GroceryItem groceryItem, final int selectedIndex) {
        new AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Do you really want to delete the grocery item?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Delete from the grocery
                    grocery.deleteGroceryItem(groceryItem);

                    // Delete from the user interface
                    groceryItems.remove(selectedIndex);
                    arrayListGroceryItems.remove(selectedIndex);
                    arrayAdapterGroceryItems.notifyDataSetChanged();

                    Application.saveData(MainActivity.this, grocery);
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    /**
     * Show the activity for updating grocery item
     */
    private void updateContextItemTapped(GroceryItem groceryItem, int selectedIndex) {
        Intent intent = new Intent(this, UpdateGroceryItemActivity.class);
        intent.putExtra("groceryItem", groceryItem);
        intent.putExtra("selectedIndex", selectedIndex);
        intent.putExtra("grocery", grocery);
        startActivityForResult(intent, Application.UPDATE_GROCERY_ITEM_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Load the grocery object from an internal file
     */
    public void loadData() {
        try {
            FileInputStream fis = openFileInput("data.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);

            grocery = (Grocery) ois.readObject();

            fis.close();
            ois.close();
        } catch(Exception e) {
            Log.e("loadData()", e.getMessage());
            grocery = new Grocery();
        }
    }

    /**
     * Initialize the menu for this activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle the event of the selected menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menuAddGroceryItem:
                menuAddGroceryItemTapped();
                break;

            case R.id.menuFilteredList:
                menuFilteredListTapped();
                break;

            case R.id.menuExportGroceryItems:
                menuExportGroceryItemsTapped();
                break;

            case R.id.menuImportGroceryItems:
                menuImportGroceryItemsTapped();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Show the activity for filtered list of grocery items
     */
    private void menuFilteredListTapped() {
        Intent intent = new Intent(this, FilteredItemsActivity.class);
        intent.putExtra("grocery", grocery);
        startActivityForResult(intent, Application.VIEW_FILTERED_LIST_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Import the items from a file
     */
    private void menuImportGroceryItemsTapped() {
        // Stop if the SD card isn't found
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, "There are no external storage where to import data.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Confirm user since we're going to replace existing items
        new AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Warning all grocery items will be deleted and replaced. Do you wish to continue?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Find the file and make sure it exists
                    File file = new File(Application.DIRECTORY, "Exported Grocery Items.txt");

                    if(!file.exists()) {
                        Toast.makeText(MainActivity.this, "Failed to find 'Exported Grocery Items.txt' file.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Replace the existing data
                    try {
                        grocery = new Grocery();
                        arrayAdapterGroceryItems.clear();
                        arrayListGroceryItems.clear();
                        groceryItems.clear();

                        Scanner inFile = new Scanner(file);

                        // Load the grocery items
                        while(inFile.hasNextLine()) {
                            GroceryItem item = new GroceryItem(inFile);
                            grocery.addGroceryItem(item);
                        }

                        groceryItems.addAll(grocery.getGroceryItems());

                        for(GroceryItem item : groceryItems)
                            arrayAdapterGroceryItems.add(item.toString());

                        Application.saveData(MainActivity.this, grocery);
                        Toast.makeText(MainActivity.this, "Import successful.", Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        Log.e("Import Grocery Items", e.getMessage());
                        Toast.makeText(MainActivity.this, "Oh snap! Developer error.", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("No", null).show();
    }

    /**
     * Export the items from to a file
     */
    private void menuExportGroceryItemsTapped() {
        // Stop if there are no SD card
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, "There are no external storage where to export data.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the file
        File file = new File(Application.DIRECTORY, "Exported Grocery Items.txt");
        file.delete();

        try {
           PrintWriter outFile = new PrintWriter(new FileWriter(file));

            // Print out all the grocery items
            for(GroceryItem item : grocery.getGroceryItems()) {
                item.write(outFile);
            }

            outFile.close();
            Toast.makeText(this, "Grocery items exported to your external storage.", Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            Log.e("Export Grocery Items", e.getMessage());
            Toast.makeText(this, "Oh snap! Developer error.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show the activity for adding a new grocery item
     */
    private void menuAddGroceryItemTapped() {
        Intent intent = new Intent(this, AddGroceryItemActivity.class);
        intent.putExtra("grocery", grocery);
        startActivityForResult(intent, Application.ADD_GROCERY_ITEM_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Handle the return callbacks of activities that returns results
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {
            case Application.ADD_GROCERY_ITEM_ACTIVITY_REQUEST_CODE:
                processAddActivityResult(resultCode, intent);
                break;
            case Application.UPDATE_GROCERY_ITEM_ACTIVITY_REQUEST_CODE:
                processUpdateActivityResult(resultCode, intent);
                break;
            case Application.VIEW_FILTERED_LIST_ACTIVITY_REQUEST_CODE:
                processViewFilteredItemsActivityResult(resultCode, intent);
                break;
        }
    }

    /**
     * After the user is done managing the filtered items, we save any changes
     */
    private void processViewFilteredItemsActivityResult(int resultCode, Intent intent) {
        grocery = (Grocery) intent.getExtras().getSerializable("grocery");
    }

    /**
     * Handle the add activity return callback after
     * adding a new grocery item
     */
    private void processAddActivityResult(int resultCode, Intent intent) {
        if(resultCode != RESULT_OK)
            return;

        grocery = (Grocery) intent.getExtras().getSerializable("grocery");
        GroceryItem groceryItem = (GroceryItem) intent.getExtras().getSerializable("groceryItem");

        groceryItems.add(groceryItem);
        arrayListGroceryItems.add(groceryItem.toString());
        arrayAdapterGroceryItems.notifyDataSetChanged();

        Application.saveData(this, grocery);
    }

    /**
     * Handle the updating in the updated grocery item
     * in the list view
     */
    private void processUpdateActivityResult(int resultCode, Intent intent) {
        if(resultCode != RESULT_OK)
            return;

        grocery = (Grocery) intent.getExtras().getSerializable("grocery");
        GroceryItem groceryItem = (GroceryItem) intent.getExtras().getSerializable("groceryItem");
        int listViewIndex = intent.getExtras().getInt("selectedIndex");
        arrayListGroceryItems.set(listViewIndex, groceryItem.toString());

        ListView listViewGroceryItems = findViewById(R.id.listViewGroceryItems);
        TextView textView = (TextView) listViewGroceryItems.getChildAt(listViewIndex - listViewGroceryItems.getFirstVisiblePosition());
        textView.setText(groceryItem.toString());

        Application.saveData(this, grocery);
    }
}
