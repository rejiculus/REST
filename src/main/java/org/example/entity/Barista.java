package org.example.entity;

import org.example.entity.exception.*;

import java.util.List;
import java.util.Objects;

public class Barista {
    private Long id;
    private String fullName;
    private List<Order> orderList;
    private Double tipSize;

    public Barista(Long id, String fullName, List<Order> orderList, Double tipSize) {
        this.id = id;
        this.fullName = fullName;
        this.orderList = orderList;
        this.tipSize = tipSize;
    }

    public Barista(String fullName, List<Order> orderList) {
        this.fullName = fullName;
        this.orderList = orderList;
        tipSize=0.1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if(id == null)
            throw new NullParamException();
        if(id<0)
            throw new NoValidIdException(id);

        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if(fullName ==null )
            throw new NullParamException();
        if(fullName.isEmpty())
            throw new NoValidNameException();

        this.fullName = fullName;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        if(orderList == null)
            throw new NullParamException();

        this.orderList = orderList;
    }

    public Double getTipSize() {
        return tipSize;
    }

    public void setTipSize(Double tipSize) {
        if(tipSize == null)
            throw new NullParamException();
        if(tipSize.isNaN())
            throw new NaNException();
        if(tipSize.isInfinite())
            throw new InfiniteException();
        if(tipSize < 0)
            throw new LessZeroException(tipSize);

        this.tipSize = tipSize;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Barista)) return false;

        Barista barista = (Barista) o;
        return Objects.equals(getId(), barista.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Barista{" +
                "id=" + id +
                ", name='" + fullName + '\'' +
                '}';
    }
}
