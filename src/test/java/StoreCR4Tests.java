import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;
import org.store.*;

class StoreCR4Tests {
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
    void test_cr4_example1_oneMAX() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 2.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("A5-MAX10"));
        assertEquals(950.0, price.getPrice(), 0.001);
        assertEquals(List.of(), price.getUnusedCoupons());
    }
    @Test
    void test_cr4_example2_twoA5andMAX10() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("A5", "A5", "A5-MAX10"));
        assertEquals(450.0, price.getPrice(), 0.001);
        assertEquals(List.of("A5"), price.getUnusedCoupons());
    }
    @Test
    void test_cr4_example3_A5_MAX15_and_MAX10() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("A5", "A5-MAX15", "A5-MAX10"));
        assertEquals(450.0, price.getPrice(), 0.001);
        assertEquals(List.of("A5-MAX15"), price.getUnusedCoupons());
    }
    @Test
    void test_cr4_example4_MAX15_and_MAX10() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("A5-MAX15", "A5-MAX10"));
        assertEquals(450.0, price.getPrice(), 0.001);
        assertEquals(List.of(), price.getUnusedCoupons());
    }
    @Test
    void test_cr4_example5_MAX15_and_A5() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("A5", "A5-MAX15"));
        assertEquals(450.0, price.getPrice(), 0.001);
        assertEquals(List.of(), price.getUnusedCoupons());
    }
    @Test
    void test_cr4_example6_promotionStrongerThanCoupon() {
        Period spring = new Period("Spring");
        spring.setUnitPrice(Product.APPLE, 600.0);
        spring.setDiscount(Product.APPLE, 2.0, 0.15);
        spring.setDiscount(Product.APPLE, 5.0, 0.20);
        target.addPeriod(spring);
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 2.0)));
        PriceInfo price = target.getCartPrice(cart, spring, List.of("A5", "A5", "A5-MAX15"));
        assertEquals(1020.0, price.getPrice(), 0.001);
        assertEquals(List.of("A5", "A5", "A5-MAX15"), price.getUnusedCoupons());
    }
    @Test
    void test_cr4_example7_MAX15_and_twoA5() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("A5-MAX15", "A5", "A5"));
        assertEquals(425.0, price.getPrice(), 0.001);
        assertEquals(List.of(), price.getUnusedCoupons());
    }
    @Test
    void test_cr4_example8_A5_MAX15_A5() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("A5", "A5-MAX15", "A5"));
        assertEquals(425.0, price.getPrice(), 0.001);
        assertEquals(List.of(), price.getUnusedCoupons());
    }
    @Test
    void test_cr4_example9_twoA5_and_MAX15() {
        Cart cart = new Cart(List.of(new Item(Product.APPLE, 1.0)));
        PriceInfo price = target.getCartPrice(cart, normal, List.of("A5", "A5", "A5-MAX15"));
        assertEquals(425.0, price.getPrice(), 0.001);
        assertEquals(List.of(), price.getUnusedCoupons());
    }
}