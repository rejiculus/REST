package org.example.entity;

import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidPriceException;
import org.example.entity.exception.NullParamException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Coffee entity. Contains id, name, price and order list fields.
 * Name and price required. Default values: id=-1, orderList= empty list.
 */
public class Coffee {
    private Long id;
    private String name;
    private Double price;
    private List<Order> orderList;

    /**
     * All params coffee constructor
     *
     * @param id        unequal identifier. Can't be less than zero.
     * @param name      name of the coffee. Can't be null or empty.
     * @param price     coffee price. Must be more than zero. Can't be NaN, Infinity or null.
     * @param orderList list of orders that contains this coffee. Can't be null.
     * @throws NullParamException    thrown when one of param equals null.
     * @throws NoValidIdException    thrown when id less than zero.
     * @throws NoValidNameException  thrown when name is empty.
     * @throws NoValidPriceException thrown when price is NaN, Infinite or less than zero.
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
     * Coffee constructor without id.
     *
     * @param name      name of the coffee. Can't be null or empty.
     * @param price     coffee price. Must be more than zero. Can't be NaN, Infinity or null.
     * @param orderList list of orders that contains this coffee. Can't be null.
     * @throws NullParamException    thrown when one of param equals null.
     * @throws NoValidNameException  thrown when name is empty.
     * @throws NoValidPriceException thrown when price is NaN, Infinite or less than zero.
     */
    public Coffee(String name, Double price, List<Order> orderList) {
        if (name == null || price == null || orderList == null)
            throw new NullParamException();
        if (name.isEmpty())
            throw new NoValidNameException();
        if (price.isNaN() || price.isInfinite() || price < 0.0)
            throw new NoValidPriceException(price);

        this.id = -1L;
        this.name = name;
        this.price = price;
        this.orderList = new ArrayList<>(orderList);
    }

    /**
     * Constructor of necessary params. Set default value: id=-1, orderList=empty list.
     *
     * @param name  name of the coffee. Can't be null or empty.
     * @param price coffee price. Must be more than zero. Can't be NaN, Infinity or null.
     * @throws NullParamException    thrown when one of param equals null.
     * @throws NoValidNameException  thrown when name is empty.
     * @throws NoValidPriceException thrown when price is NaN, Infinite or less than zero.
     */
    public Coffee(String name, Double price) {
        if (name == null || price == null)
            throw new NullParamException();
        if (name.isEmpty())
            throw new NoValidNameException();
        if (price.isNaN() || price.isInfinite() || price < 0.0)
            throw new NoValidPriceException(price);

        this.id = -1L;
        this.name = name;
        this.price = price;
        this.orderList = new ArrayList<>();
    }

    /**
     * Get unequal identifier of coffee.
     *
     * @return unequal identifier of coffee.
     * @throws NoValidIdException thrown if id is not defined.
     */
    public Long getId() {
        if (id.equals(-1L))
            throw new NoValidIdException();
        return id;
    }

    /**
     * Set unequal identifier of coffee.
     *
     * @param id unequal identifier. Can't be less than zero.
     * @throws NullParamException thrown when one of param equals null.
     * @throws NoValidIdException thrown when id less than zero.
     */
    public void setId(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        this.id = id;
    }

    /**
     * Get coffee name.
     *
     * @return name of coffee.
     */
    public String getName() {
        return name;
    }

    /**
     * Set coffee name.
     *
     * @param name name of the coffee. Can't be null or empty.
     * @throws NullParamException   thrown when one of param equals null.
     * @throws NoValidNameException thrown when name is empty.
     */
    public void setName(String name) {
        if (name == null)
            throw new NullParamException();
        if (name.isEmpty())
            throw new NoValidNameException();

        this.name = name;
    }

    /**
     * Get coffee price.
     *
     * @return price of coffee.
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Set coffee price.
     *
     * @param price coffee price. Must be more than zero. Can't be NaN, Infinity or null.
     * @throws NullParamException    thrown when one of param equals null.
     * @throws NoValidPriceException thrown when price is NaN, Infinite or less than zero.
     */
    public void setPrice(Double price) {
        if (price == null)
            throw new NullParamException();
        if (price.isNaN() || price.isInfinite() || price < 0.0)
            throw new NoValidPriceException(price);

        this.price = price;
    }

    /**
     * Get order list, that contains this coffee.
     *
     * @return order list, that contains this coffee.
     */
    public List<Order> getOrderList() {
        return orderList;
    }

    /**
     * Set order list that contains this coffee.
     *
     * @param orderList list of orders that contains this coffee. Can't be null.
     * @throws NullParamException thrown when one of param equals null.
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
