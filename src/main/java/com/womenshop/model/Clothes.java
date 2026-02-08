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
    public void applyDiscount() {
        setDiscountPrice(getSellPrice() * (1 - CLOTHES_DISCOUNT));
    }

    @Override
    public String toString() {
        return "Clothes{name='" + getName() + "', size=" + size + ", stock=" + getNbItems() + "}";
    }
}