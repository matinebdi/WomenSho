package com.womenshop.model;

public abstract class Product implements Discount, Comparable<Product> {
    private int id;
    private String name;
    private double purchasePrice;
    private double sellPrice;
    private double discountPrice;
    private int nbItems;
    private int totalSold;
    private int totalBought;

    // Static variables for global store stats
    public static double income = 0;
    public static double cost = 0;

    public Product(String name, double purchasePrice, double sellPrice) {
        if (purchasePrice < 0 || sellPrice < 0) throw new IllegalArgumentException("Negative price!");
        if (purchasePrice > sellPrice) throw new IllegalArgumentException("Purchase price > Sale price!");
        
        this.name = name;
        this.purchasePrice = purchasePrice;
        this.sellPrice = sellPrice;
        this.discountPrice = 0;
        this.nbItems = 0;
        this.totalSold = 0;
        this.totalBought = 0;
    }

    public void sell(int qty) {
        if (qty > this.nbItems) throw new IllegalArgumentException("Product Unavailable");
        double priceToUse = (discountPrice > 0) ? discountPrice : sellPrice;
        this.nbItems -= qty;
        this.totalSold += qty;
        income += (priceToUse * qty);
    }

    public void purchase(int qty) {
        double totalCost = qty * purchasePrice;
        this.nbItems += qty;
        this.totalBought += qty;
        cost += totalCost;
    }

    public static double computeCurrentCapital() {
        return income - cost;
    }

    @Override
    public void unApplyDiscount() {
        this.discountPrice = 0;
    }

    @Override
    public int compareTo(Product other) {
        return Double.compare(this.sellPrice, other.sellPrice);
    }

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public double getPurchasePrice() { return purchasePrice; }
    public double getSellPrice() { return sellPrice; }
    public double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(double dp) { this.discountPrice = dp; }
    public int getNbItems() { return nbItems; }
    public void setNbItems(int items) { this.nbItems = items; }
    public int getTotalSold() { return totalSold; }
    public void setTotalSold(int totalSold) { this.totalSold = totalSold; }
    public int getTotalBought() { return totalBought; }
    public void setTotalBought(int totalBought) { this.totalBought = totalBought; }
}