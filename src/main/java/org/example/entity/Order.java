package org.example.entity;

import org.example.entity.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Order {
    private Long id;
    private Barista barista;
    private List<Coffee> coffeeList;
    private LocalDateTime created;
    private LocalDateTime completed;
    private Double price;

    public Order(Long id, Barista barista, List<Coffee> coffeeList, LocalDateTime created, LocalDateTime completed, Double price) {
        this.id = id;
        this.barista = barista;
        this.coffeeList = coffeeList;
        this.created = created;
        this.completed = completed;
        this.price = price;
    }

    public Order(Barista barista, List<Coffee> coffeeList) {
        this.barista = barista;
        this.coffeeList = coffeeList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if(id == null)
            throw new NullParamException();
        if(id < 0)
            throw new NoValidIdException(id);

        this.id = id;
    }

    public Barista getBarista() {
        return barista;
    }

    public void setBarista(Barista barista) {
        if(barista == null)
            throw new NullParamException();

        this.barista = barista;
    }

    public List<Coffee> getCoffeeList() {
        return coffeeList;
    }

    public void setCoffeeList(List<Coffee> coffeeList) {
        if(coffeeList == null)
            throw new NullParamException();

        this.coffeeList = coffeeList;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        if(created == null)
            throw new NullParamException();

        this.created = created;
    }

    public LocalDateTime getCompleted() {
        return completed;
    }

    public void setCompleted(LocalDateTime completed) {
        if(completed == null)
            throw new NullParamException();
        if(this.created == null)
            throw new CreatedNotDefinedException();
        if(completed.isBefore(this.created))
            throw new CompletedBeforeCreatedException(this.created, completed);

        this.completed = completed;
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;

        Order order = (Order) o;
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
