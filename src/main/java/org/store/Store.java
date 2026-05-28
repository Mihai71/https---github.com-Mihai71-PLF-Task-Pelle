package org.store;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Store {

    private final Map<Product, Double> prices = new EnumMap<>(Product.class);
    private final Map<Product, List<Period.DiscountTier>> discounts = new EnumMap<>(Product.class);
    private final Random rng = new Random();

    public Store(Product product, double unitPrice) { prices.put(product, unitPrice); }

    public Store(List<ProductPrice> productPrices) {
        for (ProductPrice pp : productPrices) prices.put(pp.getProduct(), pp.getPrice());
    }

    public Store() {}

    public void setDiscount(Product product, double threshold, double discountRate) {
        discounts.computeIfAbsent(product, p -> new ArrayList<>())
                 .add(new Period.DiscountTier(threshold, discountRate));
    }

    public void addPeriod(Period period) {}

    public double getCartPrice(Cart cart) {
        return compute(cart, prices::getOrDefault, this::bestDiscountDirect);
    }

    public double getCartPrice(Cart cart, Period period) {
        return compute(cart, (p, def) -> period.getUnitPrice(p), (p, qty) -> period.bestDiscount(p, qty));
    }

    public PriceInfo getCartPrice(Cart cart, Period period, List<String> coupons) {
        return getCartPrice(cart, period, coupons, PaymentMethod.CASH);
    }

    public PriceInfo getCartPrice(Cart cart, Period period, List<String> coupons, PaymentMethod method) {
        // CR3: ULTRAMAX always last
        List<String> ultramaxList = new ArrayList<>();
        List<String> regular = new ArrayList<>();
        for (String c : coupons) {
            if (isUltramax(c)) ultramaxList.add(c); else regular.add(c);
        }

        Map<Product, Double> totals = new EnumMap<>(Product.class);
        for (Item item : cart.getItems()) totals.merge(item.getProduct(), item.getQuantity(), Double::sum);

        Map<Product, Double> effectiveQty = new EnumMap<>(totals);
        Map<Product, Double> couponDiscount = new EnumMap<>(Product.class);
        Map<Product, Boolean> couponUsed = new EnumMap<>(Product.class);
        List<String> unused = new ArrayList<>();
        boolean[] handled = new boolean[regular.size()];

        // CR5: find first X-simple coupon (X5, X10 — no -MAX)
        int xSimpleIdx = -1;
        for (int i = 0; i < regular.size(); i++) {
            if (isXSimple(regular.get(i))) { xSimpleIdx = i; break; }
        }

        if (xSimpleIdx >= 0) {
            // --- X-simple path ---
            String xCoupon = regular.get(xSimpleIdx);
            handled[xSimpleIdx] = true;
            double xRate = couponRate(xCoupon);

            boolean xApplies = false;
            for (Product p : totals.keySet()) {
                if (xRate > period.bestDiscount(p, totals.get(p))) xApplies = true;
            }

            if (!xApplies) {
                unused.add(xCoupon);
            } else {
                // Apply X to all products where it beats QD
                for (Product p : totals.keySet()) {
                    double qd = period.bestDiscount(p, totals.get(p));
                    if (xRate > qd) { couponDiscount.put(p, xRate); couponUsed.put(p, true); }
                }

                if (xRate >= 0.10) {
                    // X10: non-combinable — return everything else
                    for (int i = 0; i < regular.size(); i++) {
                        if (!handled[i]) { handled[i] = true; unused.add(regular.get(i)); }
                    }
                } else {
                    // X5: special exception — can combine with A5-MAX10 for apple
                    boolean appleInCart = totals.containsKey(Product.APPLE) && totals.get(Product.APPLE) > 0;
                    if (appleInCart) {
                        for (int i = 0; i < regular.size(); i++) {
                            if (!handled[i] && regular.get(i).equals("A5-MAX10")) {
                                handled[i] = true;
                                double combined = xRate + couponRate("A5-MAX10");
                                double maxVal = maxCouponMaxValue("A5-MAX10");
                                couponDiscount.put(Product.APPLE, Math.min(combined, maxVal));
                                break;
                            }
                        }
                    }
                    // Return all other non-handled coupons
                    for (int i = 0; i < regular.size(); i++) {
                        if (!handled[i]) { handled[i] = true; unused.add(regular.get(i)); }
                    }
                }
            }
        } else {
            // --- CR4 pre-pass: MAX combination groups, including X-MAX coupons ---

            // Mark all X-MAX coupons as handled (they participate in every product's group)
            List<Integer> xMaxIndices = new ArrayList<>();
            for (int i = 0; i < regular.size(); i++) {
                if (isXMax(regular.get(i))) { xMaxIndices.add(i); handled[i] = true; }
            }

            boolean xMaxApplied = false;

            for (Product product : Product.values()) {
                if (!totals.containsKey(product) || totals.get(product) <= 0) continue;

                List<Integer> idxList = new ArrayList<>();
                List<String> group = new ArrayList<>();
                boolean hasMax = false;

                for (int i = 0; i < regular.size(); i++) {
                    String c = regular.get(i);
                    if (!handled[i] && productFor(c) == product && !isFree(c)) {
                        idxList.add(i); group.add(c);
                        if (isMaxCoupon(c)) hasMax = true;
                    }
                }
                // Add X-MAX coupons to this product's group
                for (int idx : xMaxIndices) { group.add(regular.get(idx)); hasMax = true; }

                if (!hasMax || group.isEmpty()) continue;

                // Mark product-specific coupons as handled
                for (int idx : idxList) handled[idx] = true;

                double qty = totals.get(product);
                List<String> maxList = new ArrayList<>();
                List<String> regList = new ArrayList<>();
                for (String c : group) {
                    if (isMaxCoupon(c)) maxList.add(c); else regList.add(c);
                }

                double strictestMax = maxList.stream().mapToDouble(this::maxCouponMaxValue).min().orElse(0);
                double sumRates = group.stream().mapToDouble(this::couponRate).sum();
                double effectiveRate = Math.min(sumRates, strictestMax);
                double qd = period.bestDiscount(product, qty);

                if (effectiveRate <= qd) {
                    for (String c : regList) unused.add(c);
                    for (String c : maxList) { if (!isXMax(c)) unused.add(c); }
                    continue;
                }

                xMaxApplied = true;

                // Select required MAX: prefer non-X-MAX with lowest max value
                int reqIdx = -1;
                double lowestMax = Double.MAX_VALUE;
                for (int i = 0; i < maxList.size(); i++) {
                    if (!isXMax(maxList.get(i))) {
                        double mv = maxCouponMaxValue(maxList.get(i));
                        if (mv < lowestMax) { lowestMax = mv; reqIdx = i; }
                    }
                }
                // Fallback: use X-MAX with lowest max value
                if (reqIdx == -1) {
                    for (int i = 0; i < maxList.size(); i++) {
                        double mv = maxCouponMaxValue(maxList.get(i));
                        if (mv < lowestMax) { lowestMax = mv; reqIdx = i; }
                    }
                }

                double remaining = effectiveRate - couponRate(maxList.get(reqIdx));

                for (String c : regList) {
                    if (remaining > 1e-9) remaining -= couponRate(c);
                    else unused.add(c);
                }
                for (int i = 0; i < maxList.size(); i++) {
                    if (i == reqIdx) continue;
                    String c = maxList.get(i);
                    if (remaining > 1e-9) remaining -= couponRate(c);
                    else if (!isXMax(c)) unused.add(c);
                    // X-MAX excess: cannot be returned, silently absorbed
                }

                couponDiscount.put(product, effectiveRate);
                couponUsed.put(product, true);
            }

            // If X-MAX didn't apply anywhere (all groups had effectiveRate <= qd), return them
            if (!xMaxApplied) {
                for (int idx : xMaxIndices) unused.add(regular.get(idx));
            }
        }

        // CR2 main loop for non-handled coupons (FREE + remaining single coupons)
        for (int i = 0; i < regular.size(); i++) {
            if (handled[i]) continue;
            String coupon = regular.get(i);
            Product product = productFor(coupon);
            if (product == null) { unused.add(coupon); continue; }
            double qty = totals.getOrDefault(product, 0.0);
            if (qty <= 0) { unused.add(coupon); continue; }
            if (Boolean.TRUE.equals(couponUsed.get(product))) { unused.add(coupon); continue; }
            if (isFree(coupon)) {
                effectiveQty.put(product, Math.max(0.0, qty - 1.0));
                couponUsed.put(product, true);
            } else {
                double rate = couponRate(coupon);
                double qd = period.bestDiscount(product, qty);
                if (rate <= qd) { unused.add(coupon); continue; }
                couponDiscount.put(product, rate);
                couponUsed.put(product, true);
            }
        }

        double total = 0.0;
        for (Map.Entry<Product, Double> e : totals.entrySet()) {
            Product p = e.getKey();
            double eff = effectiveQty.getOrDefault(p, e.getValue());
            double qd = period.bestDiscount(p, eff);
            double cd = couponDiscount.getOrDefault(p, 0.0);
            total += eff * period.getUnitPrice(p) * (1.0 - Math.max(qd, cd));
        }

        if (method == PaymentMethod.CASH) {
            total = roundTo5(total);
            for (int i = 0; i < ultramaxList.size(); i++) total = Math.max(0.0, total - 2000.0);
        } else {
            for (int i = 0; i < ultramaxList.size(); i++) total = Math.max(0.0, total - 2000.0);
            // CR7: card bonus = 0.0% (was 0.5% in CR6); round to 0.1 HUF
            total = Math.round(total * 10.0) / 10.0;
        }

        // CR7: gift paper bags based on PHYSICAL weight (ignore FREE coupon reductions)
        double physicalWeight = 0.0;
        for (Item item : cart.getItems()) physicalWeight += item.getQuantity();
        int giftBagCount = (int) Math.floor(physicalWeight / 5.0);

        // CR7: gift coupons — 1 per 20,000 HUF of final amount
        int giftCouponCount = (int) Math.floor(total / 20000.0);
        List<String> giftCoupons = new ArrayList<>();
        for (int i = 0; i < giftCouponCount; i++) giftCoupons.add(drawGiftCoupon());

        return new PriceInfo(total, unused, giftBagCount, giftCoupons);
    }

    // CR7: weighted random gift coupon draw
    private String drawGiftCoupon() {
        double r = rng.nextDouble() * 100;
        if (r < 10)   return "A10";
        if (r < 20)   return "B10";
        if (r < 25)   return "A-FREE1";
        if (r < 30)   return "B-FREE1";
        if (r < 32.5) return "A5-MAX10";
        if (r < 35)   return "B5-MAX10";
        if (r < 40)   return "X5";
        if (r < 41)   return "A5-MAX15";
        if (r < 42)   return "B5-MAX15";
        if (r < 44)   return "X10";
        if (r < 45)   return "X5-MAX10";
        if (r < 46)   return "KUPON-2000-ULTRAMAX";
        if (r < 73)   return "A5";
        return "B5";
    }

    // --- Coupon type helpers ---

    private boolean isUltramax(String c) { return c.startsWith("KUPON"); }
    private boolean isXCoupon(String c)  { return c.startsWith("X"); }
    private boolean isXSimple(String c)  { return isXCoupon(c) && !isMaxCoupon(c); }
    private boolean isXMax(String c)     { return isXCoupon(c) && isMaxCoupon(c); }
    private boolean isMaxCoupon(String c){ return c.contains("-MAX"); }
    private boolean isFree(String c)     { return c.contains("FREE"); }

    private Product productFor(String c) {
        if (c.startsWith("A")) return Product.APPLE;
        if (c.startsWith("B")) return Product.BANANA;
        return null;
    }

    private double couponRate(String c) {
        if (isMaxCoupon(c)) {
            int dash = c.indexOf("-MAX");
            return Integer.parseInt(c.substring(1, dash)) / 100.0;
        }
        return Integer.parseInt(c.replaceAll("[^0-9]", "")) / 100.0;
    }

    private double maxCouponMaxValue(String c) {
        int idx = c.indexOf("-MAX");
        return Integer.parseInt(c.substring(idx + 4)) / 100.0;
    }

    // --- CR0/CR1 helpers ---

    private double compute(Cart cart,
                           java.util.function.BiFunction<Product, Double, Double> priceFor,
                           java.util.function.BiFunction<Product, Double, Double> discountFor) {
        Map<Product, Double> totals = new EnumMap<>(Product.class);
        for (Item item : cart.getItems()) totals.merge(item.getProduct(), item.getQuantity(), Double::sum);
        double total = 0.0;
        for (Map.Entry<Product, Double> e : totals.entrySet()) {
            double gross = e.getValue() * priceFor.apply(e.getKey(), 0.0);
            total += gross * (1.0 - discountFor.apply(e.getKey(), e.getValue()));
        }
        return roundTo5(total);
    }

    private double bestDiscountDirect(Product product, double quantity) {
        List<Period.DiscountTier> tiers = discounts.get(product);
        if (tiers == null) return 0.0;
        double best = 0.0;
        for (Period.DiscountTier t : tiers) {
            if (quantity >= t.threshold && t.rate > best) best = t.rate;
        }
        return best;
    }

    private double roundTo5(double amount) {
        double remainder = amount % 10.0;
        if (remainder < 2.5) return amount - remainder;
        else if (remainder < 5.0) return amount - remainder + 5.0;
        else if (remainder < 7.5) return amount - remainder + 5.0;
        else return amount - remainder + 10.0;
    }
}
