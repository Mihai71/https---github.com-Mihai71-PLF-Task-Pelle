import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;
import org.store.*;

class StoreCR2Tests {
    static Store target;
    static Period normal;

    @BeforeAll
    static void init() {
        target = new Store();
        normal = new Period("Normal");
        normal.setUnitPrice(Product.APPLE, 500.0);
        normal.setUnitPrice(Product.BANANA, 450.0);
        normal.setDiscount(Product.APPLE, 5.0, 0.1);
        normal.setDiscount(Product.APPLE, 20.0, 0.15);
        normal.setDiscount(Product.BANANA, 2.0, 0.1);
        target.addPeriod(normal);
    }

    @Test
    void test_cr2_example1_b10_not_applicable() {
        Cart cart = new Cart(List.of(new Item(Product.BANANA, 2.0)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("B10"));
        assertEquals(810.0, info.getPrice(), 0.001);
        assertEquals(List.of("B10"), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example2_a10_applicable() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("A10"));
        assertEquals(450.0, info.getPrice(), 0.001);
        assertEquals(List.of(), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example3_wrong_order() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("A5", "A10"));
        assertEquals(475.0, info.getPrice(), 0.001);
        assertEquals(List.of("A10"), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example4_better_order() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("A10", "A5"));
        assertEquals(450.0, info.getPrice(), 0.001);
        assertEquals(List.of("A5"), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example5_free_apple() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 0.5)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("A-FREE1"));
        assertEquals(0.0, info.getPrice(), 0.001);
        assertEquals(List.of(), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example6_free_and_quantity() {
        Cart cart = new Cart(List.of(new Item(Product.BANANA, 3.0)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("B-FREE1"));
        assertEquals(810.0, info.getPrice(), 0.001);
        assertEquals(List.of(), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example7_two_different_coupons() {
        Cart cart = new Cart(List.of(
                new Item(Product.APPLE, 1.0),
                new Item(Product.BANANA, 1.0)
        ));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("A5", "B5"));
        assertEquals(905.0, info.getPrice(), 0.001);
        assertEquals(List.of(), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example8_free_excluded_by_a10() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("A10", "A-FREE1"));
        assertEquals(450.0, info.getPrice(), 0.001);
        assertEquals(List.of("A-FREE1"), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example9_free_first_then_no_merge() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("A-FREE1", "A10"));
        assertEquals(0.0, info.getPrice(), 0.001);
        assertEquals(List.of("A10"), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example10_free_then_percentage_not_applicable() {
        Cart cart = new Cart(List.of(new Item(Product.BANANA, 1.5)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("B-FREE1", "B10"));
        double expected = roundTo5(0.5 * 450.0); // no discount
        assertEquals(expected, info.getPrice(), 0.001);
        assertEquals(List.of("B10"), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example11_coupon_worse_than_discount() {
        Cart cart = new Cart(List.of(new Item(Product.BANANA, 3.0)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("B5")); // already has 10% discount
        double expected = roundTo5(3.0 * 450.0 * 0.9);
        assertEquals(expected, info.getPrice(), 0.001);
        assertEquals(List.of("B5"), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example12_free_removes_discount() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 2.2)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("A-FREE1"));
        double expected = roundTo5(1.2 * 500.0);
        assertEquals(expected, info.getPrice(), 0.001);
        assertEquals(List.of(), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example13_two_coupons_same_product() {
        Cart cart = new Cart(List.of(new Item(Product.BANANA, 1.0)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("B5", "B-FREE1"));
        double expected = roundTo5(450.0 * 0.95);
        assertEquals(expected, info.getPrice(), 0.001);
        assertEquals(List.of("B-FREE1"), info.getUnusedCoupons());
    }

    @Test
    void test_cr2_example14_coupon_for_wrong_product() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo info = target.getCartPrice(cart, normal, List.of("B10"));
        assertEquals(500.0, info.getPrice(), 0.001);
        assertEquals(List.of("B10"), info.getUnusedCoupons());
    }

    // Helper function for rounding to nearest 5
    private double roundTo5(double amount) {
        double remainder = amount % 10.0;
        if (remainder < 2.5) return amount - remainder;
        if (remainder < 5.0) return amount - remainder + 5.0;
        if (remainder < 7.5) return amount - remainder + 5.0;
        return amount - remainder + 10.0;
    }
}