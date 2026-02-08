package com.womenshop.model;

public class Clothes extends Product {
    private int size;

    public Clothes(String name, double purchasePrice, double sellPrice, int size) {
        super(name, purchasePrice, sellPrice);
        setSize(size);
    }

    public int getSize() { return size; }

    public void setSize(int size) {
        if (size < 34 || size > 54 || size % 2 != 0) {
            throw new IllegalArgumentException("wrong size !");
        }
        this.size = size;
    }

    @Override
    public void applyDiscount(double percent) {
        if (percent <= 0 || percent > CLOTHES_DISCOUNT * 100)
            throw new IllegalArgumentException("Discount must be between 1% and " + (int)(CLOTHES_DISCOUNT * 100) + "%");
        setDiscountPrice(getSellPrice() * (1 - percent / 100));
    }

    @Override
    public double getMaxDiscount() { return CLOTHES_DISCOUNT * 100; }

    @Override
    public String toString() {
        return "Clothes{name='" + getName() + "', size=" + size + ", stock=" + getNbItems() + "}";
    }
}