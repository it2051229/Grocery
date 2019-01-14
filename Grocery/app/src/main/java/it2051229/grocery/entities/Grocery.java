package it2051229.grocery.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Grocery implements Serializable {
    private List<GroceryItem> groceryItems;
    private List<FilteredItem> filteredItems;

    /**
     * Create the grocery object
     */
    public Grocery() {
        groceryItems = new ArrayList<>();
        filteredItems = new ArrayList<>();
    }

    /**
     * Return a list of unique item names, sellers, and units
     */
    public void getItemNamesSellersUnits(Set<String> itemNames, Set<String> sellers, Set<String> units) {
        for(GroceryItem item : groceryItems) {
            itemNames.add(item.getName());
            sellers.add(item.getSeller());
            units.add(item.getUnit());
        }
    }

    /**
     * Return a list of unique item units
     */
    public String[] getUnits() {
        Set<String> units = new HashSet<>();

        for(GroceryItem item : groceryItems) {
            units.add(item.getUnit());
        }

        String[] unitsArray = new String[units.size()];
        unitsArray = units.toArray(unitsArray);

        return unitsArray;
    }

    /**
     * Add a new grocery item to the list and sorts it
     */
    public void addGroceryItem(GroceryItem item) {
        groceryItems.add(item);
        Collections.sort(groceryItems);
    }

    /**
     * Delete the grocery item from the list
     */
    public void deleteGroceryItem(GroceryItem item) {
        for(int i = 0; i < groceryItems.size(); i++) {
            if(groceryItems.get(i).equals(item)) {
                groceryItems.remove(i);
                break;
            }
        }
    }

    /**
     * Update the grocery item
     */
    public void updateGroceryItem(GroceryItem oldItem, GroceryItem newItem) {
        for(int i = 0; i < groceryItems.size(); i++) {
            if(groceryItems.get(i).equals(oldItem)) {
                groceryItems.remove(i);
                groceryItems.add(newItem);
                Collections.sort(groceryItems);
                break;
            }
        }
    }

    /**
     * Check if the grocery item exists
     */
    public boolean searchGroceryItem(GroceryItem item) {
        for(GroceryItem grocyerItem : groceryItems)
            if(groceryItems.equals(item))
                return true;

        return false;
    }

    /**
     * Get all the grocery items that has the given keyword
     */
    public List<GroceryItem> getGroceryItemsContaining(String keyword) {
        List<GroceryItem> filteredGroceryItems = new ArrayList<>();
        keyword = keyword.toLowerCase();

        for(GroceryItem groceryItem : groceryItems) {
            if(groceryItem.getName().toLowerCase().contains(keyword)
                    || groceryItem.getSeller().toLowerCase().contains(keyword)) {
                filteredGroceryItems.add(groceryItem);
            }
        }

        return filteredGroceryItems;
    }

    /**
     * Getter methods
     */
    public List<GroceryItem> getGroceryItems() {
        return groceryItems;
    }

    public List<FilteredItem> getFilteredItems() { return filteredItems; }
}
