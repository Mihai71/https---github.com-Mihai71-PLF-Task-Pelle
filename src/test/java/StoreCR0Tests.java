import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import org.store.Store;
import org.store.Cart;
import org.store.Item;
import org.store.Product;
import org.store.ProductPrice;

class StoreCR0Tests {

    @Test
    void test_cr0_example1_1kgApple() {
        double unitPrice = 500.0;
        double quantity = 1.0;
        Store target = new Store(Product.APPLE, unitPrice);
        double expected = unitPrice * quantity;
        Cart cart = new Cart(List.of(new Item(Product.APPLE, quantity)));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example2_2kgApple() {
        double unitPrice = 500.0;
        double quantity = 2.0;
        Store target = new Store(Product.APPLE, unitPrice);
        double expected = unitPrice * quantity;
        Cart cart = new Cart(List.of(new Item(Product.APPLE, quantity)));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example3_5kgApple_withDiscount() {
        double unitPrice = 500.0;
        double threshold1 = 5.0;
        double discount1 = 0.1;
        double quantity = threshold1;
        Store target = new Store(Product.APPLE, unitPrice);
        target.setDiscount(Product.APPLE, threshold1, discount1);
        double expected = unitPrice * quantity * (1.0 - discount1);
        Cart cart = new Cart(List.of(new Item(Product.APPLE, quantity)));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example4_20kgApple_withDiscount() {
        double unitPrice = 500.0;
        double threshold1 = 5.0;
        double discount1 = 0.1;
        double threshold2 = 20.0;
        double discount2 = 0.15;
        double quantity = threshold2;
        Store target = new Store(Product.APPLE, unitPrice);
        target.setDiscount(Product.APPLE, threshold1, discount1);
        target.setDiscount(Product.APPLE, threshold2, discount2);
        double expected = unitPrice * quantity * (1.0 - discount2);
        Cart cart = new Cart(List.of(new Item(Product.APPLE, quantity)));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example5_1kgApple_1kgBanana() {
        double unitPriceApple = 500.0;
        double quantityApple = 1.0;
        double unitPriceBanana = 450.0;
        double quantityBanana = 1.0;
        Store target = new Store(List.of(
                new ProductPrice(Product.APPLE, unitPriceApple),
                new ProductPrice(Product.BANANA, unitPriceBanana)
        ));
        Cart cart = new Cart(List.of(
                new Item(Product.APPLE, quantityApple),
                new Item(Product.BANANA, quantityBanana)
        ));
        double expected = unitPriceApple * quantityApple
                + unitPriceBanana * quantityBanana;
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example6_2kgBanana_withDiscount() {
        double unitPrice = 450.0;
        double threshold1 = 2.0;
        double discount1 = 0.1;
        double quantity = threshold1;
        Store target = new Store(Product.BANANA, unitPrice);
        target.setDiscount(Product.BANANA, threshold1, discount1);
        double expected = unitPrice * quantity * (1.0 - discount1);
        Cart cart = new Cart(List.of(new Item(Product.BANANA, quantity)));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example7_moreApples_total5kg() {
        double unitPrice = 500.0;
        double quantity1 = 1.0;
        double quantity2 = 2.0;
        double quantity3 = 2.0;
        double totalQuantity = quantity1 + quantity2 + quantity3;
        Store target = new Store(Product.APPLE, unitPrice);
        double threshold1 = 5.0;
        double discount1 = 0.1;
        target.setDiscount(Product.APPLE, threshold1, discount1);
        Cart cart = new Cart(List.of(
                new Item(Product.APPLE, quantity1),
                new Item(Product.APPLE, quantity2),
                new Item(Product.APPLE, quantity3)
        ));
        double expected = unitPrice * totalQuantity * (1.0 - discount1);
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example8_2kgApple_2kgBanana() {
        double unitPriceApple = 500.0;
        double unitPriceBanana = 450.0;
        double discountThresholdBanana = 2.0;
        double bananaDiscount = 0.1;
        double quantityApple = 2.0;
        double quantityBanana = 2.0;
        Store target = new Store(List.of(
                new ProductPrice(Product.APPLE, unitPriceApple),
                new ProductPrice(Product.BANANA, unitPriceBanana)
        ));
        target.setDiscount(Product.BANANA, discountThresholdBanana, bananaDiscount);
        double grossApple = unitPriceApple * quantityApple;
        double grossBanana = unitPriceBanana * quantityBanana;
        double netBanana = grossBanana * (1.0 - bananaDiscount);
        double expected = grossApple + netBanana;
        Cart cart = new Cart(List.of(
                new Item(Product.APPLE, quantityApple),
                new Item(Product.BANANA, quantityBanana)
        ));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example9_1_01kgApple() {
        double unitPrice = 500.0;
        double quantity = 1.01;
        Store target = new Store(Product.APPLE, unitPrice);
        double expected = unitPrice * quantity;
        Cart cart = new Cart(List.of(new Item(Product.APPLE, quantity)));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
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

    @Test
    void test_cr0_example10_1_99kgBanana() {
        double unitPrice = 450.0;
        double quantity = 1.99;
        Store target = new Store(Product.BANANA, unitPrice);
        double gross = unitPrice * quantity;
        double expected = roundTo5(gross);
        Cart cart = new Cart(List.of(new Item(Product.BANANA, quantity)));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example11_1_789kgApple() {
        double unitPrice = 500.0;
        double quantity = 1.789;
        Store target = new Store(Product.APPLE, unitPrice);
        double gross = unitPrice * quantity;
        double expected = roundTo5(gross);
        Cart cart = new Cart(List.of(new Item(Product.APPLE, quantity)));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example12_2_01kgBanana_withDiscount() {
        double unitPrice = 450.0;
        double quantity = 2.01;
        double discountThreshold = 2.0;
        double discount = 0.1;
        Store target = new Store(Product.BANANA, unitPrice);
        target.setDiscount(Product.BANANA, discountThreshold, discount);
        double gross = unitPrice * quantity;
        double net = gross * (1.0 - discount);
        System.out.println(net);
        double expected = roundTo5(net);
        System.out.println(expected);
        Cart cart = new Cart(List.of(new Item(Product.BANANA, quantity)));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example13_3_333kgApple_1_777kgBanana() {
        double unitPriceApple = 500.0;
        double unitPriceBanana = 450.0;
        double quantityApple = 3.333;
        double quantityBanana = 1.777;
        Store target = new Store(List.of(
                new ProductPrice(Product.APPLE, unitPriceApple),
                new ProductPrice(Product.BANANA, unitPriceBanana)
        ));
        double grossApple = unitPriceApple * quantityApple;
        double grossBanana = unitPriceBanana * quantityBanana;
        double gross = grossApple + grossBanana;
        double expected = roundTo5(gross);
        Cart cart = new Cart(List.of(
                new Item(Product.APPLE, quantityApple),
                new Item(Product.BANANA, quantityBanana)
        ));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example14_5_001kgApple_withDiscount() {
        double unitPrice = 500.0;
        double quantity = 5.001;
        double discountThreshold = 5.0;
        double discount = 0.1;
        Store target = new Store(Product.APPLE, unitPrice);
        target.setDiscount(Product.APPLE, discountThreshold, discount);
        double gross = unitPrice * quantity;
        double net = gross * (1.0 - discount);
        double expected = roundTo5(net);
        Cart cart = new Cart(List.of(new Item(Product.APPLE, quantity)));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void test_cr0_example15_3_789kgApple_2_777kgBanana() {
        double unitPriceApple = 500.0;
        double unitPriceBanana = 450.0;
        double quantityApple = 3.789;
        double quantityBanana = 2.777;
        double discountThresholdBanana = 2.0;
        double bananaDiscount = 0.1;
        Store target = new Store(List.of(
                new ProductPrice(Product.APPLE, unitPriceApple),
                new ProductPrice(Product.BANANA, unitPriceBanana)
        ));
        target.setDiscount(Product.BANANA, discountThresholdBanana, bananaDiscount);
        double grossApple = unitPriceApple * quantityApple;
        double grossBanana = unitPriceBanana * quantityBanana;
        double netBanana = grossBanana * (1.0 - bananaDiscount);
        double total = grossApple + netBanana;
        double expected = roundTo5(total);
        Cart cart = new Cart(List.of(
                new Item(Product.APPLE, quantityApple),
                new Item(Product.BANANA, quantityBanana)
        ));
        double actual = target.getCartPrice(cart);
        assertEquals(expected, actual, 0.001);
    }
}
