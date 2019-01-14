package it2051229.grocery.entities;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.Scanner;

public class GroceryItem implements Serializable, Comparable<GroceryItem> {
    private String name;
    private String seller;
    private double price;

    private double quantity;
    private String unit;
    private Date priceUpdateDate;

    /**
     * Create a new grocery item
     */
    public GroceryItem(String name, String seller, double price, double quantity, String unit) {
        this.name = name;
        this.seller = seller;
        this.price = price;
        this.quantity = quantity;
        this.unit = unit;
        priceUpdateDate = new Date();
    }

    /**
     * Load the grocery item from a file (used for import)
     */
    public GroceryItem(Scanner inFile) throws Exception {
        name = inFile.nextLine();
        seller = inFile.nextLine();
        price = Double.parseDouble(inFile.nextLine());
        quantity = Double.parseDouble(inFile.nextLine());
        unit = inFile.nextLine();
        priceUpdateDate = Application.DATE_FORMAT.parse(inFile.nextLine());
    }

    /**
     * Write the grocery item to a file (Used for export)
     */
    public void write(PrintWriter outFile) {
        outFile.println(name);
        outFile.println(seller);
        outFile.println(price);
        outFile.println(quantity);
        outFile.println(unit);
        outFile.println(Application.DATE_FORMAT.format(priceUpdateDate));
    }

    /**
     * Return a string representation of a grocery item
     */
    @Override
    public String toString() {
        String details = "\n" + name + "\n";
        details += "@" + seller + "\n";
        details += "Price Date \u2192 " + Application.DATE_FORMAT.format(priceUpdateDate) + "\n";
        details += "Price \u2192 " + String.format("%.2f", price) + "/" + quantity + " " + unit + "\n";
        details += "Unit Price \u2192 " + String.format("%.2f", calculateCostPerUnit()) + "/" + unit + "\n";

        return details;
    }

    /**
     * Check if this grocery item is the same as the other
     */
    public boolean equals(GroceryItem other) {
        return name.equals(other.name)
                && seller.equals(other.seller)
                && price == other.price
                && quantity == other.quantity
                && unit.equals(other.unit);
    }

    /**
     * Calculate the cost for 1 unit
     */
    public double calculateCostPerUnit() {
        return price / quantity;
    }

    /**
     * Compare grocery items by name for sorting purposes
     */
    @Override
    public int compareTo(GroceryItem item) {
        return name.compareToIgnoreCase(item.name);
    }

    /**
     * Setter methods
     */
    public void setName(String name) { this.name = name; }

    public void setSeller(String seller) { this.seller = seller; }

    public void setPrice(double price) {
        if(price == this.price)
            return;

        priceUpdateDate = new Date();
        this.price = price;
    }

    public void setQuantity(double quantity) { this.quantity = quantity; }

    public void setUnit(String unit) { this.unit = unit; }

    /**
     * Getter methods
     */
    public String getName() { return name; }

    public String getSeller() { return seller; }

    public double getPrice() { return price; }

    public double getQuantity() { return quantity; }

    public String getUnit() { return unit; }

    public Date getPriceUpdateDate() { return priceUpdateDate; }
}
