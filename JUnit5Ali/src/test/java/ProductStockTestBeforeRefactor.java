import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class ProductStockTestBeforeRefactor {

    @Test
    void constructor_shouldCreateProductStock_whenInputIsValid() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        assertNotNull(productStock);
        assertEquals("1", productStock.getProductId());
        assertEquals("Nablus", productStock.getLocation());
        assertEquals(4, productStock.getOnHand());
        assertEquals(2, productStock.getReorderThreshold());
        assertEquals(100, productStock.getMaxCapacity());
        assertEquals(0, productStock.getReserved());
    }

    @Test
    void constructor_shouldThrowException_whenProductIdIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ProductStock(null, "Nablus", 4, 2, 100)
        );

        assertEquals("productId must not be null or blank", exception.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenProductIdIsBlank() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("", "Nablus", 4, 2, 100)
        );

        assertEquals("productId must not be null or blank", exception.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenLocationIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("1", null, 4, 2, 100)
        );

        assertEquals("location must not be null or blank", exception.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenLocationIsBlank() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("1", "", 4, 2, 100)
        );

        assertEquals("location must not be null or blank", exception.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenInitialOnHandIsLessThanZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("1", "Nablus", -2, 2, 100)
        );

        assertEquals("initialOnHand must be >= 0", exception.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenReorderThresholdIsLessThanZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("1", "Nablus", 4, -2, 100)
        );

        assertEquals("reorderThreshold must be >= 0", exception.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenMaxCapacityIsLessThanZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("1", "Nablus", 4, 7, -10)
        );

        assertEquals("maxCapacity must be > 0", exception.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenMaxCapacityEqualsZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("1", "Nablus", 4, 7, 0)
        );

        assertEquals("maxCapacity must be > 0", exception.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenInitialOnHandGreaterThanMaxCapacity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("1", "Nablus", 4, 7, 2)
        );

        assertEquals("initialOnHand exceeds maxCapacity", exception.getMessage());
    }

    @Test
    void addStock_shouldSuccess_whenStockAmountPlusOnHandLessThanMaxCapacity() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        productStock.addStock(30);

        assertEquals(34, productStock.getOnHand());
    }

    @Test
    void addStock_shouldSuccess_whenStockAmountPlusOnHandLessThanMaxCapacityByOne() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        productStock.addStock(95);

        assertEquals(99, productStock.getOnHand());
    }

    @Test
    void addStock_shouldThrowException_whenStockAmountLessThanZero() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.addStock(-10)
        );

        assertEquals("Amount to add must be positive", exception.getMessage());
    }

    @Test
    void addStock_shouldThrowException_whenStockAmountEqualsZero() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.addStock(0)
        );

        assertEquals("Amount to add must be positive", exception.getMessage());
    }

    @Test
    void addStock_shouldThrowException_whenStockAmountPlusOnHandGreaterThanMaxCapacity() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                productStock.addStock(102)
        );

        assertEquals("Cannot add stock beyond maxCapacity", exception.getMessage());
    }

    @Test
    void addStock_shouldSuccess_whenStockAmountPlusOnHandEqualsMaxCapacity() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        productStock.addStock(96);

        assertEquals(100, productStock.getOnHand());
    }

    @Test
    void reserve_shouldSuccess_whenAmountLessThanAvailableStocks() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        productStock.reserve(2);

        assertEquals(productStock.getOnHand() - 2, productStock.getAvailable());
    }

    @Test
    void reserve_shouldThrowException_whenAmountGreaterThanAvailableStocks() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                productStock.reserve(6)
        );

        assertEquals("Insufficient available stock to reserve", exception.getMessage());
    }

    @Test
    void reserve_shouldThrowException_whenAmountLessThanZero() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.reserve(-10)
        );

        assertEquals("Amount to reserve must be positive", exception.getMessage());
    }

    @Test
    void releaseReservation_shouldThrowException_whenAmountLessThanZero() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.releaseReservation(-10)
        );

        assertEquals("Amount to release must be positive", exception.getMessage());
    }

    @Test
    void releaseReservation_shouldThrowException_whenAmountEqualsZero() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.releaseReservation(0)
        );

        assertEquals("Amount to release must be positive", exception.getMessage());
    }

    @Test
    void releaseReservation_shouldThrowException_whenAmountGreaterThanReserved() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);
        productStock.reserve(2);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                productStock.releaseReservation(3)
        );

        assertEquals("Cannot release more than reserved", exception.getMessage());
    }

    @Test
    void releaseReservation_shouldSuccess_whenAmountLessThanReserved() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);
        productStock.reserve(2);

        productStock.releaseReservation(1);

        assertEquals(1, productStock.getReserved());
    }

    @Test
    void shipReserved_shouldThrowException_whenAmountLessThanZero() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.shipReserved(-10)
        );

        assertEquals("Amount to ship must be positive", exception.getMessage());
    }

    @Test
    void shipReserved_shouldThrowException_whenAmountGreaterThanReserved() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);
        productStock.reserve(2);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                productStock.shipReserved(3)
        );

        assertEquals("Cannot ship more than reserved", exception.getMessage());
    }

    @Test
    void shipReserved_shouldThrowException_whenAmountGreaterThanOnInitialHold() throws Exception {
        ProductStock productStock = new ProductStock("1", "Nablus", 5, 2, 100);

        Field reservedField = ProductStock.class.getDeclaredField("reserved");
        reservedField.setAccessible(true);
        reservedField.set(productStock, 20);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                productStock.shipReserved(6)
        );

        assertEquals("On-hand quantity is not enough to ship", exception.getMessage());
    }

    @Test
    void shipReserved_shouldSuccess_whenAmountLessThanReservedAndLessThanInitialOnHand() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);
        productStock.reserve(2);

        productStock.shipReserved(2);

        assertEquals(0, productStock.getReserved());
        assertEquals(2, productStock.getOnHand());
    }

    @Test
    void removeDamaged_shouldThrowException_whenAmountLessThanZero() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.removeDamaged(-10)
        );

        assertEquals("Amount to remove must be positive", exception.getMessage());
    }

    @Test
    void removeDamaged_shouldThrowException_whenAmountEqualsZero() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.removeDamaged(0)
        );

        assertEquals("Amount to remove must be positive", exception.getMessage());
    }

    @Test
    void removeDamaged_shouldThrowException_whenAmountGreaterThanInitialOnHand() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                productStock.removeDamaged(5)
        );

        assertEquals("Cannot remove more than on-hand quantity", exception.getMessage());
    }

    @Test
    void removeDamaged_shouldSuccess_whenAmountLessThanInitialOnHand() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        productStock.removeDamaged(2);

        assertEquals(2, productStock.getOnHand());
    }

    @Test
    void removeDamaged_shouldSuccess_whenAmountEqualsInitialOnHand() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        productStock.removeDamaged(4);

        assertEquals(0, productStock.getOnHand());
    }

    @Test
    void removeDamaged_shouldChangeReserved_whenReservedGreaterThanInitialOnHand() throws Exception {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Field reservedField = ProductStock.class.getDeclaredField("reserved");
        reservedField.setAccessible(true);
        reservedField.set(productStock, 10);

        productStock.removeDamaged(2);

        assertEquals(productStock.getReserved(), productStock.getOnHand());
    }

    @Test
    void isReorderNeeded_shouldReturnTrue_whenAvailableStocksLessThanReOrderThreshold() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 5, 100);

        assertTrue(productStock.isReorderNeeded());
    }

    @Test
    void isReorderNeeded_shouldReturnFalse_whenAvailableStocksGreaterThanReOrderThreshold() {
        ProductStock productStock = new ProductStock("1", "Nablus", 10, 5, 100);
        productStock.reserve(2);

        assertFalse(productStock.isReorderNeeded());
    }

    @Test
    void updateReorderThreshold_shouldThrowException_whenNewThresholdLessThanZero() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.updateReorderThreshold(-10)
        );

        assertEquals("reorderThreshold must be >= 0", exception.getMessage());
    }

    @Test
    void updateReorderThreshold_shouldThrowException_whenNewThresholdGreaterThanMaxCapacity() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.updateReorderThreshold(101)
        );

        assertEquals("reorderThreshold cannot exceed maxCapacity", exception.getMessage());
    }

    @Test
    void updateReorderThreshold_shouldSuccess_whenNewThresholdLessThanMaxCapacity() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        productStock.updateReorderThreshold(20);

        assertEquals(20, productStock.getReorderThreshold());
    }

    @Test
    void updateMaxCapacity_shouldThrowException_whenNewMaxCapacityLessThanZero() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.updateMaxCapacity(-10)
        );

        assertEquals("maxCapacity must be > 0", exception.getMessage());
    }

    @Test
    void updateMaxCapacity_shouldThrowException_whenNewMaxCapacityEqualsZero() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                productStock.updateMaxCapacity(0)
        );

        assertEquals("maxCapacity must be > 0", exception.getMessage());
    }

    @Test
    void updateMaxCapacity_shouldThrowException_whenNewMaxCapacityLessThanInitialOnHand() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                productStock.updateMaxCapacity(2)
        );

        assertEquals("New maxCapacity is less than current onHand", exception.getMessage());
    }

    @Test
    void updateMaxCapacity_shouldSuccess_whenNewMaxCapacityGreaterThanInitialOnHand() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 2, 100);

        productStock.updateMaxCapacity(5);

        assertEquals(5, productStock.getMaxCapacity());
    }

    @Test
    void updateMaxCapacity_shouldChange_whenReorderThresholdGreaterThanMaxCapacity() {
        ProductStock productStock = new ProductStock("1", "Nablus", 4, 105, 100);

        productStock.updateMaxCapacity(101);

        assertEquals(productStock.getReorderThreshold(), productStock.getMaxCapacity());
    }
}
