package org.store;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Store {

    private final Map<Product, Double> prices = new EnumMap<>(Product.class);
    private final Map<Product, List<DiscountTier>> discounts = new EnumMap<>(Product.class);

    public Store(Product product, double unitPrice) {
        prices.put(product, unitPrice);
    }

    public Store(List<ProductPrice> productPrices) {
        for (ProductPrice pp : productPrices) {
            prices.put(pp.getProduct(), pp.getPrice());
        }
    }

    public void setDiscount(Product product, double threshold, double discountRate) {
        discounts.computeIfAbsent(product, p -> new ArrayList<>())
                 .add(new DiscountTier(threshold, discountRate));
    }

    public double getCartPrice(Cart cart) {
        Map<Product, Double> totals = new EnumMap<>(Product.class);
        for (Item item : cart.getItems()) {
            totals.merge(item.getProduct(), item.getQuantity(), Double::sum);
        }

        double total = 0.0;
        for (Map.Entry<Product, Double> entry : totals.entrySet()) {
            Product product = entry.getKey();
            double quantity = entry.getValue();
            double unitPrice = prices.getOrDefault(product, 0.0);
            double gross = quantity * unitPrice;
            double discountRate = bestDiscount(product, quantity);
            total += gross * (1.0 - discountRate);
        }

        return roundTo5(total);
    }

    private double bestDiscount(Product product, double quantity) {
        List<DiscountTier> tiers = discounts.get(product);
        if (tiers == null) return 0.0;
        double best = 0.0;
        for (DiscountTier tier : tiers) {
            if (quantity >= tier.threshold && tier.rate > best) {
                best = tier.rate;
            }
        }
        return best;
    }

    private double roundTo5(double amount) {
        double remainder = amount % 10.0;
        if (remainder < 2.5) {
            return amount - remainder;
        } else if (remainder < 5.0) {
            return amount - remainder + 5.0;
        } else if (remainder < 7.5) {
            return amount - remainder + 5.0;
        } else {
            return amount - remainder + 10.0;
        }
    }

    private static class DiscountTier {
        final double threshold;
        final double rate;

        DiscountTier(double threshold, double rate) {
            this.threshold = threshold;
            this.rate = rate;
        }
    }
}
