package org.store;

public class ProductPrice {
    private final Product product;
    private final double price;

    public ProductPrice(Product product, double price) {
        this.product = product;
        this.price = price;
    }

    public Product getProduct() { return product; }
    public double getPrice() { return price; }
}
