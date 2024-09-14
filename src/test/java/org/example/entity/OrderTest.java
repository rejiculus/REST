package org.example.entity;

import org.example.entity.exception.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    Random random = new Random();

    @Test
     void allParamConstructorTest(){
        Long expectedId = 99L;
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));
        LocalDateTime expectedCreated = LocalDateTime.now();
        LocalDateTime expectedCompleted = LocalDateTime.now().plusMinutes(1);
        Double expectedPrice = 299.9;

        Order order = new Order(expectedId, expectedBarista, expectedCoffeeList, expectedCreated, expectedCompleted, expectedPrice);

        assertEquals(expectedId, order.getId());
        assertEquals(expectedBarista, order.getBarista());
        assertEquals(expectedCoffeeList, order.getCoffeeList());
        assertEquals(expectedCreated, order.getCreated());
        assertEquals(expectedCompleted, order.getCompleted());
        assertEquals(expectedPrice, order.getPrice());
    }
    @Test
     void allParamConstructorWrongTest(){
        List<Coffee> inputCoffeeList = new ArrayList<>();
        Barista inputBarista = new Barista("John Doe");

        Assertions.assertThrows(NullParamException.class, ()-> new Order(null, inputBarista, inputCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 9.0));
        Assertions.assertThrows(NullParamException.class, ()-> new Order(99L, null, inputCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 9.0));
        Assertions.assertThrows(NullParamException.class, ()-> new Order(99L, inputBarista, null, LocalDateTime.MIN, LocalDateTime.MAX, 9.0));
        Assertions.assertThrows(CreatedNotDefinedException.class, ()-> new Order(99L, inputBarista, inputCoffeeList, null, LocalDateTime.MAX, 9.0));
        Assertions.assertThrows(NullParamException.class, ()-> new Order(99L, inputBarista, inputCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, null));


        Assertions.assertThrows(CompletedBeforeCreatedException.class, ()-> new Order(99L, inputBarista, inputCoffeeList, LocalDateTime.MAX, LocalDateTime.MIN, 9.0));

        //
        for(int i=-5; i<5; i++){
            long finalI = i;
            if(i<0) {
                Assertions.assertThrows(NoValidIdException.class, () -> new Order(finalI, inputBarista, inputCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 9.0));
            }
            else Assertions.assertDoesNotThrow(() -> new Order(finalI, inputBarista, inputCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 9.0));
        }

        Assertions.assertThrows(NoValidPriceException.class, () -> new Order(99L, inputBarista, inputCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, -0.000001));
        Assertions.assertThrows(NoValidPriceException.class, () -> new Order(99L, inputBarista, inputCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, Double.NaN));
        Assertions.assertThrows(NoValidPriceException.class, () -> new Order(99L, inputBarista, inputCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, Double.NEGATIVE_INFINITY));
        Assertions.assertThrows(NoValidPriceException.class, () -> new Order(99L, inputBarista, inputCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, Double.POSITIVE_INFINITY));


    }

    @Test
     void constructorTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);

        assertNull(order.getId());
        assertEquals(expectedBarista, order.getBarista());
        assertEquals(expectedCoffeeList, order.getCoffeeList());
        assertNull(order.getCreated());
        assertNull(order.getCompleted());
        assertNull(order.getPrice());
    }
    @Test
     void constructorWrongTest(){

        List<Coffee> inputCoffeeList = new ArrayList<>();
        Barista inputBarista = new Barista("John Doe");

        Assertions.assertThrows(NullParamException.class, ()-> new Order(99L, null, inputCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 9.0));
        Assertions.assertThrows(NullParamException.class, ()-> new Order(99L, inputBarista, null, LocalDateTime.MIN, LocalDateTime.MAX, 9.0));
    }


    @Test
    void getIdTest(){
        List<Coffee> inputCoffeeList = new ArrayList<>();
        Barista inputBarista = new Barista("John Doe");
        for(int i=0;i<100;i++){
            Long expectedId = random.nextLong(1000);
            Order order =  new Order(expectedId, inputBarista, inputCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 9.0);
            assertEquals(expectedId, order.getId());
        }
    }
    @Test
    void setIdTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);
        for(int i=0;i<100;i++){
            Long expectedId = random.nextLong(1000);
            order.setId(expectedId);
            assertEquals(expectedId, order.getId());
        }
    }
    @Test
    void setWrongIdTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);
        for(int i=-5;i<5;i++){
            long finalI = i;
            if(i<0)
                Assertions.assertThrows(NoValidIdException.class, ()->order.setId(finalI));
            else
                Assertions.assertDoesNotThrow(()->order.setId(finalI));
        }
    }

    @Test
     void getBaristaTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);
        assertEquals(expectedBarista, order.getBarista());
    }
    @Test
     void setBaristaTest(){
        Barista inputBarista = new Barista("John Doe");
        Barista expectedBarista = new Barista("Nope");
        expectedBarista.setId(10l);
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));
        Order order = new Order(inputBarista, expectedCoffeeList);

        order.setBarista(expectedBarista);
        assertEquals(expectedBarista, order.getBarista());
    }
    @Test
     void setWrongBaristaTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);

        Assertions.assertThrows(NullParamException.class, ()-> order.setBarista(null));
    }

    @Test
    void getCoffeeList(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);

        assertEquals(expectedCoffeeList, order.getCoffeeList());
    }
    @Test
    void setCoffeeList(){


        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, new ArrayList<>());

        assertEquals(new ArrayList<>(), order.getCoffeeList());

        order.setCoffeeList(expectedCoffeeList);
        assertEquals(expectedCoffeeList, order.getCoffeeList());

        expectedCoffeeList.remove(0);
        assertNotEquals(expectedCoffeeList, order.getCoffeeList());

    }
    @Test
    void setWrongCoffeeList(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);

        Assertions.assertThrows(NullParamException.class, ()-> order.setCoffeeList(null));
    }


    @Test
     void getCreatedTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order1 = new Order(expectedBarista, expectedCoffeeList);
        Order order2 = new Order(0L,expectedBarista, expectedCoffeeList, LocalDateTime.MIN, null, 299.0);

        assertNull(order1.getCreated());
        assertEquals(LocalDateTime.MIN, order2.getCreated());
    }
    @Test
     void setCreatedTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);

        assertNull(order.getCreated());

        order.setCreated(LocalDateTime.MIN);

        assertEquals(LocalDateTime.MIN, order.getCreated());

        order.setCreated(LocalDateTime.MAX);

        assertEquals(LocalDateTime.MAX, order.getCreated());
    }
    @Test
     void setWrongCreatedTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);

        assertNull(order.getCreated());

        Assertions.assertThrows(NullParamException.class, ()->order.setCreated(null));
    }

    @Test
     void getCompletedTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order1 = new Order(expectedBarista, expectedCoffeeList);
        Order order2 = new Order(0L,expectedBarista, expectedCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 299.0);

        assertNull(order1.getCompleted());
        assertEquals(LocalDateTime.MAX, order2.getCompleted());
    }
    @Test
     void setCompletedTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);
        order.setCreated(LocalDateTime.MIN);

        assertNull(order.getCompleted());

        order.setCompleted(LocalDateTime.MAX);
        assertEquals(LocalDateTime.MAX, order.getCompleted());


        order.setCompleted(LocalDateTime.MAX.minusHours(15));
        assertEquals(LocalDateTime.MAX.minusHours(15), order.getCompleted());
    }
    @Test
     void setWrongCompletedTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);
        LocalDateTime dateTime = LocalDateTime.now();
        order.setCreated(dateTime);

        LocalDateTime moreDateTime = dateTime.plusNanos(1);
        Assertions.assertDoesNotThrow(()->order.setCompleted(moreDateTime));

        Assertions.assertThrows(CompletedBeforeCreatedException.class, ()-> order.setCompleted(dateTime));

        LocalDateTime lessDateTime = dateTime.minusNanos(1);
        Assertions.assertThrows(CompletedBeforeCreatedException.class, ()-> order.setCompleted(lessDateTime));
    }

    @Test
     void getPriceTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order1 = new Order(expectedBarista, expectedCoffeeList);
        Order order2 = new Order(0L,expectedBarista, expectedCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 299.0);

        assertNull(order1.getPrice());
        assertEquals(299.0, order2.getPrice());
    }
    @Test
     void setPriceTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);

        assertNull(order.getPrice());

        order.setPrice(999.9);
        assertEquals(999.9, order.getPrice());

        order.setPrice(0.0);
        assertEquals(0.0, order.getPrice());
    }
    @Test
     void setWrongPriceTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));

        Order order = new Order(expectedBarista, expectedCoffeeList);

        Assertions.assertThrows(NullParamException.class, ()->order.setPrice(null));
        Assertions.assertThrows(NoValidPriceException.class, ()->order.setPrice(-0.000001));
        Assertions.assertThrows(NoValidPriceException.class, ()->order.setPrice(Double.NaN));
        Assertions.assertThrows(NoValidPriceException.class, ()->order.setPrice(Double.POSITIVE_INFINITY));
        Assertions.assertThrows(NoValidPriceException.class, ()->order.setPrice(Double.NEGATIVE_INFINITY));
    }

    @Test
    void equalsTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));
        Order order1 = new Order(0L,expectedBarista, expectedCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 299.0);
        Order order2 = new Order(1L,expectedBarista, expectedCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 299.0);
        Order order3 = new Order(0L,expectedBarista, expectedCoffeeList, LocalDateTime.MAX, null, 100.0);

        assertEquals(order1, order3);
        assertNotEquals(order1, order2);
        assertNotEquals(order3, order2);

        assertNotEquals(null, order1);
        assertNotEquals(new Object(), order1);
    }
    @Test
    void  hashCodeTest(){
        Barista expectedBarista = new Barista("John Doe");
        List<Coffee> expectedCoffeeList = new ArrayList<>(List.of(
                new Coffee("Frapuchino", 999.9),
                new Coffee("Frapuchino bez cofe", 999.9)
        ));
        Order order1 = new Order(0L,expectedBarista, expectedCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 299.0);
        Order order2 = new Order(1L,expectedBarista, expectedCoffeeList, LocalDateTime.MIN, LocalDateTime.MAX, 299.0);
        Order order3 = new Order(0L,expectedBarista, expectedCoffeeList, LocalDateTime.MAX, null, 100.0);

        assertEquals(order1.hashCode(), order3.hashCode());
        assertNotEquals(order1.hashCode(), order2.hashCode());
        assertNotEquals(order3.hashCode(), order2.hashCode());
    }



}