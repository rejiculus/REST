package org.example.entity;

import org.example.entity.exception.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private Long id;
    private Barista barista;
    private List<Coffee> coffeeList;
    private LocalDateTime created;
    private LocalDateTime completed;
    private Double price;

    /**
     * @param id
     * @param barista
     * @param coffeeList
     * @param created
     * @param completed
     * @param price
     * @throws NullParamException
     * @throws CreatedNotDefinedException
     * @throws NoValidIdException
     * @throws CompletedBeforeCreatedException
     * @throws NoValidPriceException
     */
    public Order(Long id, Barista barista, List<Coffee> coffeeList, LocalDateTime created, LocalDateTime completed, Double price) {
        if (id == null || barista == null || coffeeList == null || price == null)
            throw new NullParamException();
        if (created == null)
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
     * @param barista
     * @param coffeeList
     * @throws NullParamException
     */
    public Order(Barista barista, List<Coffee> coffeeList) {
        if (barista == null || coffeeList == null)
            throw new NullParamException();

        this.barista = barista;
        this.coffeeList = new ArrayList<>(coffeeList);
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

    public Barista getBarista() {
        return barista;
    }

    /**
     * @param barista
     * @throws NullParamException
     */
    public void setBarista(Barista barista) {
        if (barista == null)
            throw new NullParamException();

        this.barista = barista;
    }

    public List<Coffee> getCoffeeList() {
        return coffeeList;
    }

    /**
     * @param coffeeList
     * @throws NullParamException
     */
    public void setCoffeeList(List<Coffee> coffeeList) {
        if (coffeeList == null)
            throw new NullParamException();

        this.coffeeList = new ArrayList<>(coffeeList);
    }

    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * @param created
     * @throws NullParamException
     */
    public void setCreated(LocalDateTime created) {
        if (created == null)
            throw new NullParamException();

        this.created = created;
    }

    public LocalDateTime getCompleted() {
        return completed;
    }

    /**
     * @param completed
     * @throws NullParamException
     * @throws CreatedNotDefinedException
     * @throws CompletedBeforeCreatedException
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
