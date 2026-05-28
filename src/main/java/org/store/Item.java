package org.store;

public class Item {
    private final Product product;
    private final double quantity;

    public Item(Product product, double quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public double getQuantity() { return quantity; }
}
