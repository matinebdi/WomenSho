package com.womenshop.model;

public class Accessory extends Product {

    public Accessory(String name, double purchasePrice, double sellPrice) {
        super(name, purchasePrice, sellPrice);
    }

    @Override
    public void applyDiscount(double percent) {
        if (percent <= 0 || percent > ACCESSORY_DISCOUNT * 100)
            throw new IllegalArgumentException("Discount must be between 1% and " + (int)(ACCESSORY_DISCOUNT * 100) + "%");
        setDiscountPrice(getSellPrice() * (1 - percent / 100));
    }

    @Override
    public double getMaxDiscount() { return ACCESSORY_DISCOUNT * 100; }

    @Override
    public String toString() {
        return "Accessory{name='" + getName() + "', stock=" + getNbItems() + "}";
    }
}