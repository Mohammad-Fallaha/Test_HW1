package main.java;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * بسيط: كلاس تيست لـ ProductStock
 * بحاول أغطي أهم السلوكيات المطلوبة في الواجب.
 */
class ProductStockTest {

    private ProductStock stock;

    // ================== Life-cycle methods ==================

    @BeforeAll
    static void beforeAll() {
        System.out.println(">>> Start ProductStock tests <<<");
    }

    @BeforeEach
    void setUp() {
        // productId, location, initialOnHand, reorderThreshold, maxCapacity
        stock = new ProductStock("P1", "LOC-1", 10, 3, 100);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test finished for one case");
    }

    @AfterAll
    static void afterAll() {
        System.out.println(">>> All ProductStock tests are done <<<");
    }

    // ================== Constructor & basic getters ==================

    @Tag("sanity")
    @Test
    @DisplayName("Constructor with valid data should create correct object")
    void testConstructorWithValidData() {
        assertAll(
                () -> assertEquals("P1", stock.getProductId()),
                () -> assertEquals("LOC-1", stock.getLocation()),
                () -> assertEquals(10, stock.getOnHand()),
                () -> assertEquals(0, stock.getReserved()),
                () -> assertEquals(3, stock.getReorderThreshold()),
                () -> assertEquals(100, stock.getMaxCapacity())
        );
    }

    @Tag("regression")
    @Test
    @DisplayName("Constructor should reject invalid productId (null or blank)")
    void testConstructorInvalidProductId() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock(null, "WH-1", 5, 2, 50));

        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("   ", "WH-1", 5, 2, 50));
    }

    @Tag("regression")
    @Test
    @DisplayName("Constructor should reject invalid location (null or blank)")
    void testConstructorInvalidLocation() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("P1", null, 5, 2, 50));

        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("P1", "   ", 5, 2, 50));
    }

    @Tag("regression")
    @Test
    @DisplayName("Constructor should reject negative numbers and zero capacity")
    void testConstructorInvalidNumbers() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("P1", "WH-1", -1, 2, 50));

        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("P1", "WH-1", 5, -1, 50));

        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("P1", "WH-1", 5, 2, 0));
    }

    @Tag("regression")
    @Test
    @DisplayName("Constructor should reject initialOnHand > maxCapacity")
    void testConstructorRejectsOnHandGreaterThanCapacity() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("P1", "WH-1", 60, 2, 50));
    }

    @Tag("sanity")
    @Test
    @DisplayName("Getter getProductId should return the same id")
    void testGetProductId() {
        assertEquals("P1", stock.getProductId());
    }

    @Tag("sanity")
    @Test
    @DisplayName("Getter getLocation should return initial location")
    void testGetLocation() {
        assertEquals("LOC-1", stock.getLocation());
    }

    @Tag("sanity")
    @Test
    @DisplayName("Getter getOnHand should return current onHand")
    void testGetOnHand() {
        assertEquals(10, stock.getOnHand());
    }

    // ================== changeLocation ==================

    @Tag("sanity")
    @Test
    @DisplayName("changeLocation with valid string should update location")
    void testChangeLocationValid() {
        stock.changeLocation("LOC-2");
        assertEquals("LOC-2", stock.getLocation());
    }

    @Tag("regression")
    @Test
    @DisplayName("changeLocation should reject null and blank")
    void testChangeLocationInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.changeLocation(null));

        assertThrows(IllegalArgumentException.class,
                () -> stock.changeLocation("   "));
    }

    // ================== addStock ==================

    @Tag("sanity")
    @Test
    @DisplayName("addStock normal case should increase onHand")
    void testAddStockNormal() {
        stock.addStock(20);   // 10 + 20 = 30
        assertEquals(30, stock.getOnHand());
    }

    @Tag("regression")
    @Test
    @DisplayName("addStock should throw when amount is 0 or negative")
    void testAddStockNonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.addStock(0));

        assertThrows(IllegalArgumentException.class,
                () -> stock.addStock(-5));
    }

    @Tag("regression")
    @Test
    @DisplayName("addStock should not allow exceeding maxCapacity")
    void testAddStockBeyondCapacity() {
        // onHand = 10, maxCapacity = 100 -> adding 100 = 110 > 100
        assertThrows(IllegalStateException.class,
                () -> stock.addStock(100));
    }

    // ================== removeDamaged ==================

    @Tag("sanity")
    @Test
    @DisplayName("removeDamaged normal case should reduce onHand")
    void testRemoveDamagedNormal() {
        stock.removeDamaged(3); // 10 - 3 = 7
        assertEquals(7, stock.getOnHand());
    }

    @Tag("regression")
    @Test
    @DisplayName("removeDamaged should reject non-positive amount")
    void testRemoveDamagedNonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.removeDamaged(0));

        assertThrows(IllegalArgumentException.class,
                () -> stock.removeDamaged(-1));
    }

    @Tag("regression")
    @Test
    @DisplayName("removeDamaged should reject amount > onHand")
    void testRemoveDamagedMoreThanOnHand() {
        assertThrows(IllegalStateException.class,
                () -> stock.removeDamaged(20));
    }

    // ================== reserve / releaseReservation / shipReserved ==================

    @Tag("sanity")
    @Test
    @DisplayName("reserve normal case should update reserved and available")
    void testReserveNormal() {
        stock.reserve(4);   // reserved = 4, available = 6
        assertEquals(4, stock.getReserved());
        assertEquals(6, stock.getAvailable());
    }

    @Tag("regression")
    @Test
    @DisplayName("reserve should reject amount <= 0")
    void testReserveNonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.reserve(0));

        assertThrows(IllegalArgumentException.class,
                () -> stock.reserve(-3));
    }

    @Tag("regression")
    @Test
    @DisplayName("reserve should reject amount > available")
    void testReserveMoreThanAvailable() {
        assertThrows(IllegalStateException.class,
                () -> stock.reserve(20));
    }

    @Tag("sanity")
    @Test
    @DisplayName("releaseReservation normal case should decrease reserved")
    void testReleaseReservationNormal() {
        stock.reserve(5);
        stock.releaseReservation(3);
        assertEquals(2, stock.getReserved());
        assertEquals(8, stock.getAvailable());
    }

    @Tag("regression")
    @Test
    @DisplayName("releaseReservation should reject amount > reserved")
    void testReleaseMoreThanReserved() {
        stock.reserve(4);
        assertThrows(IllegalStateException.class,
                () -> stock.releaseReservation(5));
    }

    @Tag("regression")
    @Test
    @DisplayName("releaseReservation should reject amount <= 0")
    void testReleaseReservationNonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.releaseReservation(0));

        assertThrows(IllegalArgumentException.class,
                () -> stock.releaseReservation(-1));
    }

    @Tag("sanity")
    @Test
    @DisplayName("shipReserved normal case should reduce reserved and onHand")
    void testShipReservedNormal() {
        stock.reserve(4);
        stock.shipReserved(4);
        assertEquals(6, stock.getOnHand());   // 10 - 4
        assertEquals(0, stock.getReserved());
        assertEquals(6, stock.getAvailable());
    }

    @Tag("regression")
    @Test
    @DisplayName("shipReserved should reject amount > reserved")
    void testShipMoreThanReserved() {
        stock.reserve(3);
        assertThrows(IllegalStateException.class,
                () -> stock.shipReserved(4));
    }

    @Tag("regression")
    @Test
    @DisplayName("shipReserved should reject amount <= 0")
    void testShipReservedNonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.shipReserved(0));

        assertThrows(IllegalArgumentException.class,
                () -> stock.shipReserved(-1));
    }

    // ================== isReorderNeeded ==================

    @Tag("sanity")
    @Test
    @DisplayName("isReorderNeeded should return true when available < threshold")
    void testIsReorderNeededTrue() {
        // threshold = 3, available بعد الحجز = 2
        stock.reserve(8); // 10 - 8 = 2 < 3
        assertTrue(stock.isReorderNeeded());
    }

    @Tag("regression")
    @Test
    @DisplayName("isReorderNeeded should return false when available >= threshold")
    void testIsReorderNeededFalse() {
        // available = 10, threshold = 3
        assertFalse(stock.isReorderNeeded());
    }

    // ================== updateReorderThreshold / updateMaxCapacity ==================

    @Tag("sanity")
    @Test
    @DisplayName("updateReorderThreshold with valid value should update field")
    void testUpdateReorderThresholdValid() {
        stock.updateReorderThreshold(5);
        assertEquals(5, stock.getReorderThreshold());
    }

    @Tag("regression")
    @Test
    @DisplayName("updateReorderThreshold should reject negative or > maxCapacity")
    void testUpdateReorderThresholdInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.updateReorderThreshold(-1));

        assertThrows(IllegalArgumentException.class,
                () -> stock.updateReorderThreshold(101)); // > maxCapacity 100
    }

    @Tag("sanity")
    @Test
    @DisplayName("updateMaxCapacity should update capacity and adjust threshold if needed")
    void testUpdateMaxCapacityValidAdjustsThreshold() {
        stock.updateReorderThreshold(80);
        stock.updateMaxCapacity(50);
        assertEquals(50, stock.getMaxCapacity());
        assertEquals(50, stock.getReorderThreshold()); // تم ضبطها مع السعة الجديدة
    }

    @Tag("regression")
    @Test
    @DisplayName("updateMaxCapacity should reject non positive values")
    void testUpdateMaxCapacityNonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.updateMaxCapacity(0));

        assertThrows(IllegalArgumentException.class,
                () -> stock.updateMaxCapacity(-10));
    }

    @Tag("regression")
    @Test
    @DisplayName("updateMaxCapacity should reject value less than onHand")
    void testUpdateMaxCapacityLessThanOnHand() {
        // onHand = 10، فـ newMaxCapacity = 5 لازم ترمي استثناء
        assertThrows(IllegalStateException.class,
                () -> stock.updateMaxCapacity(5));
    }

    // ================== toString, Timeout, Disabled ==================

    @Tag("sanity")
    @Test
    @DisplayName("toString should return non-empty string")
    @Timeout(value = 50, unit = TimeUnit.MILLISECONDS)
    void testToStringIsNotEmpty() {
        String s = stock.toString();
        assertNotNull(s);
        assertFalse(s.isBlank());
    }

    @Disabled("Future feature: performance / stress test, not needed now")
    @Test
    @DisplayName("Disabled test example for future work")
    void disabledFutureTest() {
        // ما في كود هون، بس مجرد placeholder للمستقبل
    }
}
