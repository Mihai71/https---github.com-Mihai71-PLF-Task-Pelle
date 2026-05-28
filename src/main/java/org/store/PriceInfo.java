package org.store;

import java.util.List;

public class PriceInfo {
    private final double price;
    private final List<String> unusedCoupons;
    private final int giftBagCount;
    private final List<String> giftCoupons;

    public PriceInfo(double price, List<String> unusedCoupons) {
        this(price, unusedCoupons, 0, List.of());
    }

    public PriceInfo(double price, List<String> unusedCoupons, int giftBagCount, List<String> giftCoupons) {
        this.price = price;
        this.unusedCoupons = List.copyOf(unusedCoupons);
        this.giftBagCount = giftBagCount;
        this.giftCoupons = List.copyOf(giftCoupons);
    }

    public double getPrice()              { return price; }
    public double getAmount()             { return price; }
    public List<String> getUnusedCoupons(){ return unusedCoupons; }
    public int getGiftBagCount()          { return giftBagCount; }
    public List<String> getGiftCoupons()  { return giftCoupons; }
}
