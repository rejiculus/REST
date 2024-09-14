package org.example.entity;

import org.example.entity.exception.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CoffeeTest {
    Random random = new Random();

    @Test
    void allParamConstructorTest(){
        Long expectedId = 99L;
        String expectedName = "Frapuchino";
        Double expectedPrice = 299.9;
        List<Order> expectedOrderList = new ArrayList<>();

        Coffee coffee = new Coffee(expectedId, expectedName, expectedPrice, expectedOrderList);

        assertEquals(expectedId, coffee.getId());
        assertEquals(expectedName, coffee.getName());
        assertEquals(expectedPrice, coffee.getPrice());
        assertEquals(expectedOrderList, coffee.getOrderList());
    }
    @Test
     void allParamConstructorWrongTest(){
        List<Order> inputOrders = new ArrayList<>();

        Assertions.assertThrows(NullParamException.class, () -> new Coffee(null, "Frapuchino", 199.9, inputOrders));
        Assertions.assertThrows(NullParamException.class, () -> new Coffee(99L, null, 199.9, inputOrders));
        Assertions.assertThrows(NullParamException.class, () -> new Coffee(99L, "Frapuchino", null, inputOrders));
        Assertions.assertThrows(NullParamException.class, () -> new Coffee(99L, "Frapuchino", 199.9, null));


        //
        for(int i=-5; i<5; i++){
            long finalI = i;
            if(i<0) {
                Assertions.assertThrows(NoValidIdException.class, () -> new Coffee(finalI, "Frapuchino", 199.9, inputOrders));
            }
            else Assertions.assertDoesNotThrow(() -> new Coffee(finalI, "Frapuchino", 199.9, inputOrders));
        }

        Assertions.assertThrows(NoValidNameException.class, () -> new Coffee(99L, "", 199.9, inputOrders));

        Assertions.assertThrows(NoValidPriceException.class, () -> new Coffee(99L, "Frapuchino", -0.000001, inputOrders));
        Assertions.assertThrows(NoValidPriceException.class, () -> new Coffee(99L, "Frapuchino", Double.NaN, inputOrders));
        Assertions.assertThrows(NoValidPriceException.class, () -> new Coffee(99L, "Frapuchino", Double.NEGATIVE_INFINITY, inputOrders));
        Assertions.assertThrows(NoValidPriceException.class, () -> new Coffee(99L, "Frapuchino", Double.POSITIVE_INFINITY, inputOrders));

    }

    @Test
     void constructorTest(){
        String expectedName = "Frapuchino";
        Double expectedPrice = 299.9;

        Coffee coffee = new Coffee(expectedName, expectedPrice);

        assertNull(coffee.getId());
        assertEquals(expectedName, coffee.getName());
        assertEquals(expectedPrice, coffee.getPrice());
        assertEquals(new ArrayList<>(), coffee.getOrderList());
    }
    @Test
     void constructorWrongTest(){
        Assertions.assertThrows(NullParamException.class, () -> new Coffee(null, 199.9));
        Assertions.assertThrows(NullParamException.class, () -> new Coffee("Frapuchino", null));

        Assertions.assertThrows(NoValidNameException.class, () -> new Coffee("", 199.9));

        Assertions.assertThrows(NoValidPriceException.class, () -> new Coffee("Frapuchino", -0.000001));
        Assertions.assertThrows(NoValidPriceException.class, () -> new Coffee("Frapuchino", Double.NaN));
        Assertions.assertThrows(NoValidPriceException.class, () -> new Coffee("Frapuchino", Double.NEGATIVE_INFINITY));
        Assertions.assertThrows(NoValidPriceException.class, () -> new Coffee("Frapuchino", Double.POSITIVE_INFINITY));
    }


    @Test
    void getIdTest(){
        for(int i=0;i<100;i++){
            Long expectedId = random.nextLong(1000);
            Coffee coffee =  new Coffee(expectedId, "Frapuchino", 199.9, new ArrayList<>());
            assertEquals(expectedId, coffee.getId());
        }
    }
    @Test
    void setIdTest(){
        Coffee coffee =  new Coffee("Frapuchino", 199.9);
        for(int i=0;i<100;i++){
            Long expectedId = random.nextLong(1000);
            coffee.setId(expectedId);
            assertEquals(expectedId, coffee.getId());
        }
    }
    @Test
    void setWrongIdTest(){
        Coffee coffee =  new Coffee("Frapuchino", 199.9);
        for(int i=-5;i<5;i++){
            long finalI = i;
            if(i<0)
                Assertions.assertThrows(NoValidIdException.class, ()->coffee.setId(finalI));
            else
                Assertions.assertDoesNotThrow(()->coffee.setId(finalI));
        }
    }

    @Test
     void getNameTest(){
        String expectedName = "Ping pong";
        Coffee coffee = new Coffee(expectedName,0.1);
        assertEquals(expectedName, coffee.getName());
    }
    @Test
     void setNameTest(){
        Coffee coffee = new Coffee("John Doe", 0.1);

        coffee.setName("Kizaru");
        assertEquals("Kizaru", coffee.getName());

        coffee.setName("Alabasta");
        assertEquals("Alabasta", coffee.getName());

        coffee.setName("Island");
        assertEquals("Island", coffee.getName());
    }
    @Test
     void setWrongNameTest(){
        Coffee coffee = new Coffee("John Doe", 0.1);

        Assertions.assertThrows(NullParamException.class, ()->coffee.setName(null));
        Assertions.assertThrows(NoValidNameException.class, ()->coffee.setName(""));
    }

    @Test
    void getPriceTest(){
        for(int i=0;i<100;i++){
            Double val = random.nextDouble(2.0);
            Coffee coffee = new Coffee("John Doe", val);
            assertEquals(val, coffee.getPrice());
        }
    }
    @Test
    void setPriceTest(){
        Coffee coffee = new Coffee("John Doe", 999.0);
        for(int i=0;i<100;i++){
            Double val = random.nextDouble(999999.999999);
            coffee.setPrice(val);
            assertEquals(val, coffee.getPrice());
        }
    }
    @Test
    void setWrongPriceTest(){
        Assertions.assertThrows(NullParamException.class, ()-> new Coffee("John Doe",(Double) null));
        Assertions.assertThrows(NoValidPriceException.class, ()-> new Coffee("John Doe",-0.0001));
        Assertions.assertThrows(NoValidPriceException.class, ()-> new Coffee("John Doe",Double.NEGATIVE_INFINITY));
        Assertions.assertThrows(NoValidPriceException.class, ()-> new Coffee("John Doe",Double.POSITIVE_INFINITY));
        Assertions.assertThrows(NoValidPriceException.class, ()-> new Coffee("John Doe",Double.NaN));
    }



    @Test
    void getOrderListList(){
        Coffee coffee = new Coffee("John Doe", 999.0);

        assertEquals(new ArrayList<Order>(), coffee.getOrderList());
    }
    @Test
    void setOrderListList(){
        List<Order> orders = new ArrayList<>();
        Barista barista = new Barista("John Doe", 0.1);
        Coffee coffee = new Coffee("John Doe", 999.0);

        orders.add(new Order(barista,new ArrayList<>(List.of(coffee,coffee))));
        orders.add(new Order(barista,new ArrayList<>(List.of(coffee,coffee,coffee))));
        orders.add(new Order(barista,new ArrayList<>(List.of(coffee,coffee,coffee,coffee))));

        coffee.setOrderList(orders);

        assertEquals(orders, coffee.getOrderList());

        orders.remove(0);

        assertNotEquals(orders, coffee.getOrderList());

    }
    @Test
    void setWrongOrderListList(){
        Coffee coffee = new Coffee("John Doe", 999.0);

        Assertions.assertThrows(NullParamException.class, ()-> coffee.setOrderList(null));
    }

    @Test
    void equalsTest(){
        Coffee coffee1 = new Coffee(0L, "John Doe", 199.0, new ArrayList<>());
        Coffee coffee2 = new Coffee(0L, "John Doe", 129.0, new ArrayList<>());
        Coffee coffee3 = new Coffee(0L, "Wow", 439.0, new ArrayList<>());

        assertEquals(coffee1, coffee2);
        assertEquals(coffee1, coffee3);
        assertEquals(coffee2, coffee3);

        assertNotEquals(null, coffee1);
        assertNotEquals(new Object(), coffee1);
    }
    @Test
    void  hashCodeTest(){
        Coffee coffee1 = new Coffee(0L, "John Doe", 199.0, new ArrayList<>());
        Coffee coffee2 = new Coffee(0L, "John Doe", 129.0, new ArrayList<>());
        Coffee coffee3 = new Coffee(0L, "Wow", 439.0, new ArrayList<>());

        assertEquals(coffee1.hashCode(), coffee2.hashCode());
        assertEquals(coffee1.hashCode(), coffee3.hashCode());
        assertEquals(coffee2.hashCode(), coffee3.hashCode());
    }

}