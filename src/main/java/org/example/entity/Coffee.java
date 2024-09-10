package org.example.entity;

import org.example.entity.exception.*;

import java.util.List;
import java.util.Objects;

public class Coffee {
    private Long id;
    private String name;
    private Double price;
    private List<Order> orderList;

    public Coffee(Long id, String name, Double price, List<Order> orderList) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.orderList = orderList;
    }

    public Coffee(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if(id == null)
            throw new NullParamException();
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null)
            throw new NullParamException();
        if(name.isEmpty())
            throw new NoValidNameException();

        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        if(price == null)
            throw new NullParamException();
        if(price.isNaN())
            throw new NaNException();
        if(price.isInfinite())
            throw new InfiniteException();
        if(price < 0)
            throw new LessZeroException(price);

        this.price = price;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        if(orderList == null)
            throw new NullParamException();

        this.orderList = orderList;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coffee)) return false;

        Coffee coffee = (Coffee) o;
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
