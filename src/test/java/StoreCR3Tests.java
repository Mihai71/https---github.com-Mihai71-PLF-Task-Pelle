import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;
import org.store.*;

class StoreCR3Tests {
    static Store target;
    static Period normal;
    @BeforeAll
    public static void initStore() {
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
    void test_cr3_example1_onlyUltramax() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 3.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("KUPON-2000-ULTRAMAX"));
        assertEquals(0.0, price.getAmount(), 0.001);
        assertEquals(List.of(), price.getUnusedCoupons());
    }
    @Test
    void test_cr3_example2_a10PlusUltramax() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 4.1)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("A10", "KUPON-2000-ULTRAMAX"));
        assertEquals(0.0, price.getAmount(), 0.001);
        assertEquals(List.of(), price.getUnusedCoupons());
    }
    @Test
    void test_cr3_example2b_ultramaxPlusA10() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 4.1)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("KUPON-2000-ULTRAMAX", "A10"));
        assertEquals(0.0, price.getAmount(), 0.001);
        assertEquals(List.of(), price.getUnusedCoupons());
    }
    @Test
    void test_cr3_example3_twoUltramax() {
        Cart cart = new Cart(List.of(new Item(Product.BANANA, 7.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("KUPON-2000-ULTRAMAX", "KUPON-2000-ULTRAMAX"));
        assertEquals(0.0, price.getAmount(), 0.001);
    }
    @Test
    void test_cr3_example4_a10AndUltramax() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.5)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("A10", "KUPON-2000-ULTRAMAX"));
        assertEquals(0.0, price.getAmount(), 0.001);
    }
    @Test
    void test_cr3_example5_twoProducts_oneCoupon() {
        Cart cart = new Cart(List.of(
                new Item(Product.APPLE, 1.0),
                new Item(Product.BANANA, 1.0)
        ));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("A5", "KUPON-2000-ULTRAMAX"));
        assertEquals(0.0, price.getAmount(), 0.001);
    }
    @Test
    void test_cr3_example6_b10Unused() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("B10", "KUPON-2000-ULTRAMAX"));
        assertEquals(0.0, price.getAmount(), 0.001);
        assertEquals(List.of("B10"), price.getUnusedCoupons());
    }
    @Test
    void test_cr3_example7_notZeroFinal() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 10.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("KUPON-2000-ULTRAMAX"));
        assertEquals(2500.0, price.getAmount(), 0.001);
    }
    @Test
    void test_cr3_example8_bigBananaCart() {
        Cart cart = new Cart(List.of(new Item(Product.BANANA, 15.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("KUPON-2000-ULTRAMAX"));
        assertEquals(4075.0, price.getAmount(), 0.001);
    }
    @Test
    void test_cr3_example9_twoUltramax_notZero() {
        Cart cart = new Cart(List.of(new Item(Product.BANANA, 10.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("KUPON-2000-ULTRAMAX", "KUPON-2000-ULTRAMAX"));
        assertEquals(50.0, price.getAmount(), 0.001);
    }
    @Test
    void test_cr3_example10_criticalOrder() {
        Cart cart = new Cart(List.of(
                new Item(Product.APPLE, 2.0),
                new Item(Product.BANANA, 2.0)
        ));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("KUPON-2000-ULTRAMAX", "A5"));
        assertEquals(0.0, price.getAmount(), 0.001);
    }
}