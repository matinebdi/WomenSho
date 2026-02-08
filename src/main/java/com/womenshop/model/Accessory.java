package com.womenshop.model;

public class Accessory extends Product {

    public Accessory(String name, double purchasePrice, double sellPrice) {
        super(name, purchasePrice, sellPrice);
    }

    @Override
    public void applyDiscount() {
        setDiscountPrice(getSellPrice() * (1 - ACCESSORY_DISCOUNT));
    }

    @Override
    public String toString() {
        return "Accessory{name='" + getName() + "', stock=" + getNbItems() + "}";
    }
}