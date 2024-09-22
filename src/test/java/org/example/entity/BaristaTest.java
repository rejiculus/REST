package org.example.entity;

import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidTipSizeException;
import org.example.entity.exception.NullParamException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BaristaTest {
    Random random = new Random();

    @Test
    void allParamConstructorTest() {
        Long expectedId = 0L;
        String expectedFullName = "John Doe";
        List<Order> expectedOrderList = new ArrayList<>();
        Double expectedTipSize = 0.3;

        Barista barista = new Barista(expectedId, expectedFullName, expectedOrderList, expectedTipSize);

        assertEquals(expectedId, barista.getId());
        assertEquals(expectedFullName, barista.getFullName());
        assertEquals(expectedOrderList, barista.getOrderList());
        assertEquals(expectedTipSize, barista.getTipSize());
    }

    @Test
    void allParamConstructorWrongTest() {
        ArrayList<Order> inputOrders = new ArrayList<>();
        Assertions.assertThrows(NullParamException.class, () -> new Barista(null, "John Doe", inputOrders, 0.3));
        Assertions.assertThrows(NullParamException.class, () -> new Barista(0L, null, inputOrders, 0.3));
        Assertions.assertThrows(NullParamException.class, () -> new Barista(0L, "John Doe", null, 0.3));
        Assertions.assertThrows(NullParamException.class, () -> new Barista(0L, "John Doe", inputOrders, null));

        //
        for (int i = -5; i < 5; i++) {
            long finalI = i;
            if (i < 0) {
                Assertions.assertThrows(NoValidIdException.class, () -> new Barista(finalI, "John Doe", inputOrders, 0.3));
            } else Assertions.assertDoesNotThrow(() -> new Barista(finalI, "John Doe", inputOrders, 0.3));
        }

        Assertions.assertThrows(NoValidNameException.class, () -> new Barista(0L, "", inputOrders, 0.3));

        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista(0L, "John Doe", inputOrders, -0.3));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista(0L, "John Doe", inputOrders, Double.NaN));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista(0L, "John Doe", inputOrders, Double.POSITIVE_INFINITY));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista(0L, "John Doe", inputOrders, Double.NEGATIVE_INFINITY));


    }

    @Test
    void constructorTipTest() {
        String expectedFullName = "John Doe";
        Double expectedTipSize = 0.3;

        Barista barista = new Barista(expectedFullName, expectedTipSize);

        assertThrows(NoValidIdException.class, () -> barista.getId());
        assertEquals(expectedFullName, barista.getFullName());
        assertEquals(new ArrayList<>(), barista.getOrderList());
        assertEquals(expectedTipSize, barista.getTipSize());
    }

    @Test
    void constructorTipWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> new Barista(null, 0.3));
        Assertions.assertThrows(NullParamException.class, () -> new Barista("John Doe", (Double) null));//heh)

        Assertions.assertThrows(NoValidNameException.class, () -> new Barista("", 0.3));

        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista("John Doe", -0.3));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista("John Doe", Double.NaN));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista("John Doe", Double.POSITIVE_INFINITY));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista("John Doe", Double.NEGATIVE_INFINITY));


    }

    @Test
    void constructorOrdersTest() {
        String expectedFullName = "John Doe";
        List<Order> expectedOrderList = new ArrayList<>();
        Barista barista = new Barista(expectedFullName, expectedOrderList);

        assertThrows(NoValidIdException.class, () -> barista.getId());
        assertEquals(expectedFullName, barista.getFullName());
        assertEquals(expectedOrderList, barista.getOrderList());
        assertEquals(0.1, barista.getTipSize());
    }

    @Test
    void constructorOrdersWrongTest() {
        List<Order> orders = new ArrayList<>();
        Assertions.assertThrows(NullParamException.class, () -> new Barista(null, orders));
        Assertions.assertThrows(NullParamException.class, () -> new Barista("John Doe", (List<Order>) null));//heh)

        Assertions.assertThrows(NoValidNameException.class, () -> new Barista("", 0.3));
    }

    @Test
    void constructorTest() {
        String expectedFullName = "John Doe";
        Barista barista = new Barista(expectedFullName);

        assertThrows(NoValidIdException.class, () -> barista.getId());
        assertEquals(expectedFullName, barista.getFullName());
        assertEquals(new ArrayList<Order>(), barista.getOrderList());
        assertEquals(0.1, barista.getTipSize());
    }

    @Test
    void constructorWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> new Barista(null));

        Assertions.assertThrows(NoValidNameException.class, () -> new Barista(""));
    }

    @Test
    void getIdTest() {
        for (int i = 0; i < 100; i++) {
            Long expectedId = random.nextLong(1000);
            Barista barista = new Barista(expectedId, "John Doe", new ArrayList<>(), 0.1);
            assertEquals(expectedId, barista.getId());
        }
    }

    @Test
    void setIdTest() {
        Barista barista = new Barista("John Doe", 0.1);
        for (int i = 0; i < 100; i++) {
            Long expectedId = random.nextLong(1000);
            barista.setId(expectedId);
            assertEquals(expectedId, barista.getId());
        }
    }

    @Test
    void setWrongIdTest() {
        Barista barista = new Barista("John Doe", 0.1);
        for (int i = -5; i < 5; i++) {
            long finalI = i;
            if (i < 0)
                Assertions.assertThrows(NoValidIdException.class, () -> barista.setId(finalI));
            else
                Assertions.assertDoesNotThrow(() -> barista.setId(finalI));
        }
    }

    @Test
    void getFullNameTest() {
        String expectedFullName = "Ping pong";
        Barista barista = new Barista(expectedFullName, 0.1);
        assertEquals(expectedFullName, barista.getFullName());
    }

    @Test
    void setFullNameTest() {
        Barista barista = new Barista("John Doe", 0.1);

        barista.setFullName("Kizaru");
        assertEquals("Kizaru", barista.getFullName());

        barista.setFullName("Alabasta");
        assertEquals("Alabasta", barista.getFullName());

        barista.setFullName("Island");
        assertEquals("Island", barista.getFullName());
    }

    @Test
    void setWrongFullNameTest() {
        Barista barista = new Barista("John Doe", 0.1);

        Assertions.assertThrows(NullParamException.class, () -> barista.setFullName(null));
        Assertions.assertThrows(NoValidNameException.class, () -> barista.setFullName(""));
    }

    @Test
    void getOrderListList() {
        Barista barista = new Barista("John Doe", 0.1);

        assertEquals(new ArrayList<Order>(), barista.getOrderList());
    }

    @Test
    void setOrderListList() {
        List<Order> orders = new ArrayList<>();
        Barista barista = new Barista("John Doe", 0.1);
        orders.add(new Order(barista, new ArrayList<>()));
        orders.add(new Order(barista, new ArrayList<>()));
        orders.add(new Order(barista, new ArrayList<>()));

        barista.setOrderList(orders);

        assertEquals(orders, barista.getOrderList());

        orders.remove(0);

        assertNotEquals(orders, barista.getOrderList());

    }

    @Test
    void setWrongOrderListList() {
        Barista barista = new Barista("John Doe", 0.1);

        Assertions.assertThrows(NullParamException.class, () -> barista.setOrderList(null));
    }

    @Test
    void getTipSizeTest() {
        for (int i = 0; i < 100; i++) {
            Double val = random.nextDouble(2.0);
            Barista barista = new Barista("John Doe", val);
            assertEquals(val, barista.getTipSize());
        }
    }

    @Test
    void setTipSizeTest() {
        Barista barista = new Barista("John Doe", 0.1);
        for (int i = 0; i < 100; i++) {
            Double val = random.nextDouble(2.0);
            barista.setTipSize(val);
            assertEquals(val, barista.getTipSize());
        }
    }

    @Test
    void setWrongTipSizeTest() {
        Assertions.assertThrows(NullParamException.class, () -> new Barista("John Doe", (Double) null));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista("John Doe", -0.0001));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista("John Doe", Double.NEGATIVE_INFINITY));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista("John Doe", Double.POSITIVE_INFINITY));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> new Barista("John Doe", Double.NaN));
    }

    @Test
    void equalsTest() {
        Barista barista1 = new Barista(0L, "John Doe", new ArrayList<>(), 0.3);
        Barista barista2 = new Barista(0L, "John Doe", new ArrayList<>(), 0.3);
        Barista barista3 = new Barista(0L, "Wow", new ArrayList<>(), 0.1);

        assertEquals(barista2, barista1);
        assertEquals(barista2, barista3);
        assertEquals(barista1, barista3);

        assertNotEquals(null, barista1);
        assertNotEquals(new Object(), barista1);
    }

    @Test
    void hashCodeTest() {
        Barista barista1 = new Barista(0L, "John Doe", new ArrayList<>(), 0.3);
        Barista barista2 = new Barista(0L, "John Doe", new ArrayList<>(), 0.3);
        Barista barista3 = new Barista(0L, "Wow", new ArrayList<>(), 0.1);

        assertEquals(barista1.hashCode(), barista2.hashCode());
        assertEquals(barista1.hashCode(), barista3.hashCode());
        assertEquals(barista2.hashCode(), barista3.hashCode());
    }

}