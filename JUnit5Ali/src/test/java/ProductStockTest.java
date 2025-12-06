import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@Tag("regression")
@DisplayName("ProductStock – Full Test Suite")
class ProductStockTest {

    private ProductStock stock;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Starting ProductStock Test Suite");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finished ProductStock Test Suite");
    }

    @BeforeEach
    void setUp() {
        stock = new ProductStock("1", "Nablus", 50, 5, 100);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test completed.");
    }

    @Nested
    @Tag("sanity")
    @DisplayName("Constructor Validation")
    class ConstructorTests {

        @Test
        @DisplayName("Valid constructor should create object correctly")
        void validConstructor() {
            assertNotNull(stock);
            assertEquals("1", stock.getProductId());
            assertEquals("Nablus", stock.getLocation());
            assertEquals(50, stock.getOnHand());
            assertEquals(5, stock.getReorderThreshold());
            assertEquals(100, stock.getMaxCapacity());
            assertEquals(0, stock.getReserved());
        }

        @Test
        void negativeOnHandShouldFail() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ProductStock("1", "Nablus", -1, 5, 100));
        }

        @Test
        void negativeThresholdShouldFail() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ProductStock("1", "Nablus", 10, -5, 100));
        }

        @Test
        void negativeCapacityShouldFail() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ProductStock("1", "Nablus", 10, 5, -100));
        }

        @Test
        void onHandGreaterThanCapacityShouldFail() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ProductStock("1", "Nablus", 200, 5, 100));
        }

        @Test
        void nullLocationShouldFail() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ProductStock("1", null, 10, 5, 100));
        }
    }

    @Nested
    @DisplayName("Add Stock")
    class AddStockTests {

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 10, 20})
        void validAddStock(int amount) {
            int before = stock.getOnHand();
            stock.addStock(amount);
            assertEquals(before + amount, stock.getOnHand());
        }

        @Test
        void addStockZeroFails() {
            assertThrows(IllegalArgumentException.class, () -> stock.addStock(0));
        }

        @Test
        void addStockNegativeFails() {
            assertThrows(IllegalArgumentException.class, () -> stock.addStock(-10));
        }

        @Test
        void exceedingMaxCapacityShouldFail() {
            assertThrows(IllegalStateException.class,
                    () -> stock.addStock(1000));
        }

        @Test
        void addStockToMaxCapacity() {
            stock.addStock(50);
            assertEquals(100, stock.getOnHand());
        }

        @Test
        @Timeout(1)
        void addStockPerformanceTest() {
            stock.addStock(1);
        }

        @Test
        void addStockMultipleTimes() {
            stock.addStock(5);
            stock.addStock(5);
            assertEquals(60, stock.getOnHand());
        }
    }

    @Nested
    @DisplayName("Reserve Stock")
    class ReserveTests {

        @Test
        void validReserve() {
            stock.reserve(10);
            assertEquals(10, stock.getReserved());
        }

        @Test
        void reserveZeroFails() {
            assertThrows(IllegalArgumentException.class,
                    () -> stock.reserve(0));
        }

        @Test
        void reserveNegativeFails() {
            assertThrows(IllegalArgumentException.class,
                    () -> stock.reserve(-5));
        }

        @Test
        void reserveMoreThanAvailableFails() {
            assertThrows(IllegalStateException.class,
                    () -> stock.reserve(999));
        }

        @Test
        void reserveAllAvailable() {
            stock.reserve(50);
            assertEquals(50, stock.getReserved());
        }

        @Test
        void multipleReservesIncreaseTotal() {
            stock.reserve(10);
            stock.reserve(5);
            assertEquals(15, stock.getReserved());
        }
    }

    @Nested
    @DisplayName("Release Reservation")
    class ReleaseReservationTests {

        @Test
        void validRelease() {
            stock.reserve(20);
            stock.releaseReservation(5);
            assertEquals(15, stock.getReserved());
        }

        @Test
        void releaseZeroFails() {
            assertThrows(IllegalArgumentException.class,
                    () -> stock.releaseReservation(0));
        }

        @Test
        void releaseNegativeFails() {
            assertThrows(IllegalArgumentException.class,
                    () -> stock.releaseReservation(-5));
        }

        @Test
        void releaseMoreThanReservedFails() {
            stock.reserve(10);
            assertThrows(IllegalStateException.class,
                    () -> stock.releaseReservation(50));
        }

        @Test
        void releaseAll() {
            stock.reserve(20);
            stock.releaseReservation(20);
            assertEquals(0, stock.getReserved());
        }
    }

    @Nested
    @DisplayName("Ship Reserved")
    class ShipReservedTests {

        @Test
        void validShip() {
            stock.reserve(10);
            stock.shipReserved(5);

            assertEquals(45, stock.getOnHand());
            assertEquals(5, stock.getReserved());
        }

        @Test
        void shipZeroFails() {
            assertThrows(IllegalArgumentException.class,
                    () -> stock.shipReserved(0));
        }

        @Test
        void shipNegativeFails() {
            assertThrows(IllegalArgumentException.class,
                    () -> stock.shipReserved(-5));
        }

        @Test
        void shipMoreThanReservedFails() {
            stock.reserve(5);
            assertThrows(IllegalStateException.class,
                    () -> stock.shipReserved(10));
        }

        @Test
        void shipWithReflectionManipulation() throws Exception {
            Field f = ProductStock.class.getDeclaredField("reserved");
            f.setAccessible(true);
            f.set(stock, 2);

            assertThrows(IllegalStateException.class,
                    () -> stock.shipReserved(5));
        }
    }

    @Nested
    @DisplayName("Remove Damaged")
    class RemoveDamagedTests {

        @Test
        void validRemove() {
            stock.removeDamaged(10);
            assertEquals(40, stock.getOnHand());
        }

        @Test
        void removeZeroFails() {
            assertThrows(IllegalArgumentException.class,
                    () -> stock.removeDamaged(0));
        }

        @Test
        void removeNegativeFails() {
            assertThrows(IllegalArgumentException.class,
                    () -> stock.removeDamaged(-5));
        }

        @Test
        void removeTooMuchFails() {
            assertThrows(IllegalStateException.class,
                    () -> stock.removeDamaged(1000));
        }

        @Test
        void removeAll() {
            stock.removeDamaged(50);
            assertEquals(0, stock.getOnHand());
        }
    }

    @Nested
    @DisplayName("Reorder Logic")
    class ReorderTests {

        @Test
        void reorderNeededTrue() {
            ProductStock low = new ProductStock("2", "Jenin", 3, 5, 100);
            assertTrue(low.isReorderNeeded());
        }

        @Test
        void reorderNeededFalse() {
            assertFalse(stock.isReorderNeeded());
        }

        @Test
        void reorderAfterRemoval() {
            stock.removeDamaged(47);
            assertTrue(stock.isReorderNeeded());
        }

        @Test
        void reorderAfterAddStock() {
            stock.updateReorderThreshold(60);
            assertTrue(stock.isReorderNeeded());
        }
    }

    @Nested
    @DisplayName("Update Limits")
    class UpdateLimitTests {

        @Test
        void updateThresholdValid() {
            stock.updateReorderThreshold(20);
            assertEquals(20, stock.getReorderThreshold());
        }

        @Test
        void updateCapacityValid() {
            stock.updateMaxCapacity(200);
            assertEquals(200, stock.getMaxCapacity());
        }

        @Test
        void updateCapacityBelowOnHandFails() {
            assertThrows(IllegalStateException.class,
                    () -> stock.updateMaxCapacity(10));
        }
    }

    @Test
    void availableWithoutReservation() {
        assertEquals(50, stock.getAvailable());
    }

    @Test
    void availableWithReservation() {
        stock.reserve(20);
        assertEquals(30, stock.getAvailable());
    }

    @Disabled("Future: Supplier auto-restock")
    @Test
    void autoRestockFromSupplier() {
        fail("Not implemented yet");
    }
}
