package org.example.entity;

import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidPriceException;
import org.example.entity.exception.NullParamException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Coffee {
    private Long id;
    private String name;
    private Double price;
    private List<Order> orderList;

    /**
     * @param id
     * @param name
     * @param price
     * @param orderList
     * @throws NullParamException
     * @throws NoValidIdException
     * @throws NoValidNameException
     * @throws NoValidPriceException
     */
    public Coffee(Long id, String name, Double price, List<Order> orderList) {
        if (id == null || name == null || price == null || orderList == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);
        if (name.isEmpty())
            throw new NoValidNameException();
        if (price.isNaN() || price.isInfinite() || price < 0.0)
            throw new NoValidPriceException(price);

        this.id = id;
        this.name = name;
        this.price = price;
        this.orderList = new ArrayList<>(orderList);
    }

    /**
     * @param name
     * @param price
     * @throws NullParamException
     * @throws NoValidNameException
     * @throws NoValidPriceException
     */
    public Coffee(String name, Double price) {
        if (name == null || price == null)
            throw new NullParamException();
        if (name.isEmpty())
            throw new NoValidNameException();
        if (price.isNaN() || price.isInfinite() || price < 0.0)
            throw new NoValidPriceException(price);


        this.name = name;
        this.price = price;
        this.orderList = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    /**
     * @param id
     * @throws NullParamException
     * @throws NoValidIdException
     */
    public void setId(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * @param name
     * @throws NullParamException
     * @throws NoValidNameException
     */
    public void setName(String name) {
        if (name == null)
            throw new NullParamException();
        if (name.isEmpty())
            throw new NoValidNameException();

        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    /**
     * @param price
     * @throws NullParamException
     * @throws NoValidPriceException
     */
    public void setPrice(Double price) {
        if (price == null)
            throw new NullParamException();
        if (price.isNaN() || price.isInfinite() || price < 0.0)
            throw new NoValidPriceException(price);

        this.price = price;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    /**
     * @param orderList
     * @throws NullParamException
     */
    public void setOrderList(List<Order> orderList) {
        if (orderList == null)
            throw new NullParamException();

        this.orderList = new ArrayList<>(orderList);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coffee coffee)) return false;

        return Objects.equals(getId(), coffee.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Coffee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
