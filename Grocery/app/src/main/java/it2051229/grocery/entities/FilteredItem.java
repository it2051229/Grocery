package it2051229.grocery.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A filtered item is a grocery item that is placed in the filtered list. This can accommodate
 * additional properties such as price adjustments
 */
public class FilteredItem implements Serializable {
    private GroceryItem groceryItem;
    private List<PriceAdjustment> priceAdjustments;

    /**
     * Create a filtered item
     */
    public FilteredItem(GroceryItem groceryItem) {
        this.groceryItem = groceryItem;
        priceAdjustments = new ArrayList<>();
    }

    /**
     * Return the updated price of the filtered item considering the price adjustments
     */
    public double calculatePrice() {
        double price = groceryItem.getPrice();

        for(PriceAdjustment adjustment : priceAdjustments) {
            price += adjustment.calculateAddedOrReducedAmount(groceryItem.getPrice());
        }

        return price;
    }

    /**
     * Return the updated unit price of the filtered item
     */
    public double calculateCostPerUnit() {
        return calculatePrice() / groceryItem.getQuantity();
    }

    /**
     * Create a string representation of the grocery item including the price adjustments
     */
    @Override
    public String toString() {
        String details = groceryItem.toString();

        if(priceAdjustments.isEmpty())
            return details;

        for(PriceAdjustment adjustment : priceAdjustments) {
            double addedOrRemovedAmount = adjustment.calculateAddedOrReducedAmount(groceryItem.getPrice());

            if(addedOrRemovedAmount > 0)
                details += "+";

            details += String.format("%.2f", addedOrRemovedAmount);
            details += " @" + adjustment.toString() + "\n";
        }

        details += "Adjusted Price \u2192 " + String.format("%.2f", calculatePrice()) + "/" + groceryItem.getQuantity() + " " + groceryItem.getUnit() + "\n";
        details += "Adjusted Unit Price \u2192 " + String.format("%.2f", calculateCostPerUnit()) + "/" + groceryItem.getUnit() + "\n";

        return details;
    }

    /**
     * Getter methods
     */
    public GroceryItem getGroceryItem() {
        return groceryItem;
    }

    public List<PriceAdjustment> getPriceAdjustments() { return priceAdjustments; }
}
