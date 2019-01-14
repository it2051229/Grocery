package it2051229.grocery.entities;

import java.io.Serializable;

/**
 * To be used on items in the comparison list to adjust the price based
 * on fixed cost or percentage
 */
public class PriceAdjustment implements Serializable {
    public static final int ADDED_COST = 1;
    public static final int REDUCED_COST = 2;

    public static final int FIXED_COST = 1;
    public static final int PERCENTAGE = 2;

    private String name;
    private double value;

    private int addOrReduce;
    private int fixedOrPercentage;

    /**
     * Create a new price adjustment object
     */
    public PriceAdjustment(int addOrReduce, int fixedOrPercentage, String name, double value) {
        this.name = name;
        this.addOrReduce = addOrReduce;
        this.fixedOrPercentage = fixedOrPercentage;
        this.value = value;
    }

    /**
     * Return a string representation of the price adjustment
     */
    @Override
    public String toString() {
        String details = "";

        if(fixedOrPercentage == PERCENTAGE)
            details += String.format("%.2f", (value)) + "% ";

        return details + name;
    }

    /**
     * Given the cost, calculate how much is to reduce or add.
     */
    public double calculateAddedOrReducedAmount(double cost) {
        if(addOrReduce == ADDED_COST) {
            if(fixedOrPercentage == FIXED_COST) {
                return value;
            } else if(fixedOrPercentage == PERCENTAGE){
                return (cost / 100) * value;
            }
        } else if(addOrReduce == REDUCED_COST) {
            if(fixedOrPercentage == FIXED_COST) {
                return -value;
            } else if(fixedOrPercentage == PERCENTAGE){
                return -((cost / 100) * value);
            }
        }

        return 0;
    }
}
