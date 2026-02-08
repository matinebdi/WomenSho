package com.womenshop.model;

public class Shoes extends Product {
    private int shoeSize;

    public Shoes(String name, double purchasePrice, double sellPrice, int shoeSize) {
        super(name, purchasePrice, sellPrice);
        setShoeSize(shoeSize);
    }

    public int getShoeSize() { return shoeSize; }

    public void setShoeSize(int shoeSize) {
        if (shoeSize < 36 || shoeSize > 50) {
            throw new IllegalArgumentException("Wrong shoe size !");
        }
        this.shoeSize = shoeSize;
    }

    @Override
    public void applyDiscount(double percent) {
        if (percent <= 0 || percent > SHOES_DISCOUNT * 100)
            throw new IllegalArgumentException("Discount must be between 1% and " + (int)(SHOES_DISCOUNT * 100) + "%");
        setDiscountPrice(getSellPrice() * (1 - percent / 100));
    }

    @Override
    public double getMaxDiscount() { return SHOES_DISCOUNT * 100; }

    @Override
    public String toString() {
        return "Shoes{name='" + getName() + "', shoeSize=" + shoeSize + ", stock=" + getNbItems() + "}";
    }
}