package org.store;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Period {

    private final String name;
    private final Map<Product, Double> prices = new EnumMap<>(Product.class);
    private final Map<Product, List<DiscountTier>> discounts = new EnumMap<>(Product.class);

    public Period(String name) {
        this.name = name;
    }

    public void setUnitPrice(Product product, double price) {
        prices.put(product, price);
    }

    public void setDiscount(Product product, double threshold, double discountRate) {
        discounts.computeIfAbsent(product, p -> new ArrayList<>())
                 .add(new DiscountTier(threshold, discountRate));
    }

    double getUnitPrice(Product product) {
        return prices.getOrDefault(product, 0.0);
    }

    double bestDiscount(Product product, double quantity) {
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

    static class DiscountTier {
        final double threshold;
        final double rate;

        DiscountTier(double threshold, double rate) {
            this.threshold = threshold;
            this.rate = rate;
        }
    }
}
