package org.example.entity;

import org.example.entity.exception.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Order entity. Contains fields: id, barista - hwo prepared order, coffeeList - ordered coffee list,
 * created - order created datetime, completed - order completed datetime and
 * price - price for order (sum of coffee's prices * barista's tip size).
 * Required fields: barista, coffeeList.
 * Default values: id = -1, price = 0, created = null, completed = null.
 */
public class Order {
    private Long id;
    private Barista barista;
    private List<Coffee> coffeeList;
    private LocalDateTime created;
    private LocalDateTime completed;
    private Double price;

    /**
     * All fields constructor.
     *
     * @param id         unique identifier. Can't be null or less than zero.
     * @param barista    who prepared this order. Can't be null.
     * @param coffeeList list of ordered coffee. Can't be null.
     * @param created    datetime when order is ordered.
     * @param completed  datetime when order is complete. Can't be before created.
     * @param price      price for order (sum of coffee's prices * barista's tip size). Can't be NaN, Infinity, null or less than zero
     * @throws NullParamException              thrown when one of param equals null.
     * @throws CreatedNotDefinedException      thrown when completed is defined but created is not.
     * @throws NoValidIdException              thrown when id less than zero.
     * @throws CompletedBeforeCreatedException thrown when completed is before created.
     * @throws NoValidPriceException           thrown when price is NaN, Infinite or less than zero.
     */
    public Order(Long id, Barista barista, List<Coffee> coffeeList, LocalDateTime created, LocalDateTime completed, Double price) {
        if (id == null || barista == null || coffeeList == null || price == null)
            throw new NullParamException();
        if (created == null && completed != null)
            throw new CreatedNotDefinedException();
        if (id < 0)
            throw new NoValidIdException(id);
        if (completed != null && completed.isBefore(created))
            throw new CompletedBeforeCreatedException(created, completed);
        if (price.isNaN() || price.isInfinite() || price < 0.0)
            throw new NoValidPriceException(price);

        this.id = id;
        this.barista = barista;
        this.coffeeList = new ArrayList<>(coffeeList);
        this.created = created;
        this.completed = completed;
        this.price = price;
    }

    /**
     * Without id constructor.
     *
     * @param barista    who prepared this order. Can't be null.
     * @param coffeeList list of ordered coffee. Can't be null.
     * @param created    datetime when order is ordered.
     * @param completed  datetime when order is complete. Can't be before created.
     * @param price      price for order (sum of coffee's prices * barista's tip size). Can't be NaN, Infinity, null or less than zero
     * @throws NullParamException              thrown when one of param equals null.
     * @throws CreatedNotDefinedException      thrown when completed is defined but created is not.
     * @throws CompletedBeforeCreatedException thrown when completed is before created.
     * @throws NoValidPriceException           thrown when price is NaN, Infinite or less than zero.
     */
    public Order(Barista barista, List<Coffee> coffeeList, LocalDateTime created, LocalDateTime completed, Double price) {
        if (barista == null || coffeeList == null || price == null)
            throw new NullParamException();
        if (created == null && completed != null)
            throw new CreatedNotDefinedException();
        if (completed != null && completed.isBefore(created))
            throw new CompletedBeforeCreatedException(created, completed);
        if (price.isNaN() || price.isInfinite() || price < 0.0)
            throw new NoValidPriceException(price);

        this.id = -1L;
        this.barista = barista;
        this.coffeeList = new ArrayList<>(coffeeList);
        this.created = created;
        this.completed = completed;
        this.price = price;
    }

    /**
     * Minimum required constructor.
     *
     * @param barista    who prepared this order. Can't be null.
     * @param coffeeList list of ordered coffee. Can't be null.
     * @throws NullParamException thrown when one of param equals null.
     */
    public Order(Barista barista, List<Coffee> coffeeList) {
        if (barista == null || coffeeList == null)
            throw new NullParamException();

        this.id = -1L;
        this.barista = barista;
        this.coffeeList = new ArrayList<>(coffeeList);
        this.price = 0.0;
    }

    /**
     * Minimum required constructor with created param.
     *
     * @param barista    who prepared this order. Can't be null.
     * @param created    datetime when order is ordered.
     * @param coffeeList list of ordered coffee. Can't be null.
     * @throws NullParamException thrown when one of param equals null.
     */
    public Order(Barista barista, List<Coffee> coffeeList, LocalDateTime created) {
        if (barista == null || coffeeList == null || created==null)
            throw new NullParamException();

        this.id = -1L;
        this.barista = barista;
        this.coffeeList = new ArrayList<>(coffeeList);
        this.created = created;
        this.price = 0.0;
    }

    /**
     * Get unique identifier.
     *
     * @return unique identifier.
     * @throws NoValidIdException thrown if id is not defined.
     */
    public Long getId() {
        if (id.equals(-1L))
            throw new NoValidIdException();
        return id;
    }

    /**
     * Set unique identifier.
     *
     * @param id unique identifier. Can't be null or less than zero.
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
     * Get barista, who prepared this order.
     *
     * @return barista.
     */
    public Barista getBarista() {
        return barista;
    }

    /**
     * Set barista, who prepared this order.
     *
     * @param barista who prepared this order. Can't be null.
     * @throws NullParamException thrown when one of param equals null.
     */
    public void setBarista(Barista barista) {
        if (barista == null)
            throw new NullParamException();

        this.barista = barista;
    }

    /**
     * Get list of ordered coffee.
     *
     * @return list of ordered coffee.
     */
    public List<Coffee> getCoffeeList() {
        return coffeeList;
    }

    /**
     * Set list of ordered coffee.
     *
     * @param coffeeList list of ordered coffee. Can't be null.
     * @throws NullParamException thrown when one of param equals null.
     */
    public void setCoffeeList(List<Coffee> coffeeList) {
        if (coffeeList == null)
            throw new NullParamException();

        this.coffeeList = new ArrayList<>(coffeeList);
    }

    /**
     * Get datetime when order is created.
     *
     * @return datetime when order is created.
     */
    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * Set datetime when order is created.
     *
     * @param created datetime when order is ordered.
     * @throws NullParamException thrown when one of param equals null.
     */
    public void setCreated(LocalDateTime created) {
        if (created == null)
            throw new NullParamException();

        this.created = created;
    }

    /**
     * Get datetime when order is completed.
     *
     * @return datetime when order is completed.
     */
    public LocalDateTime getCompleted() {
        return completed;
    }

    /**
     * Set datetime when order is completed.
     *
     * @param completed datetime when order is complete. Can't be before created.
     * @throws NullParamException              thrown when one of param equals null.
     * @throws CreatedNotDefinedException      thrown when completed is defined but created is not.
     * @throws CompletedBeforeCreatedException thrown when completed is before created.
     */
    public void setCompleted(LocalDateTime completed) {
        if (completed == null)
            throw new NullParamException();
        if (this.created == null)
            throw new CreatedNotDefinedException();
        if (!completed.isAfter(this.created))
            throw new CompletedBeforeCreatedException(this.created, completed);

        this.completed = completed;
    }

    /**
     * Get order's price.
     *
     * @return order's price.
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Set order's price.
     *
     * @param price price for order (sum of coffee's prices * barista's tip size). Can't be NaN, Infinity, null or less than zero
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;

        return Objects.equals(getId(), order.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", barista=" + barista +
                ", coffeeList=" + coffeeList +
                ", created=" + created +
                ", completed=" + completed +
                ", price=" + price +
                '}';
    }
}
