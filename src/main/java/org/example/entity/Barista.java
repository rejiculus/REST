package org.example.entity;

import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidTipSizeException;
import org.example.entity.exception.NullParamException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Barista {
    private Long id;
    private String fullName;
    private List<Order> orderList;
    private Double tipSize;

    /**
     * @param id
     * @param fullName
     * @param orderList
     * @param tipSize
     * @throws NullParamException
     * @throws NoValidIdException
     * @throws NoValidNameException
     * @throws NoValidTipSizeException
     */
    public Barista(Long id, String fullName, List<Order> orderList, Double tipSize) {
        if (id == null || fullName == null || orderList == null || tipSize == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);
        if (fullName.isEmpty())
            throw new NoValidNameException();
        if (tipSize.isNaN() || tipSize.isInfinite() || tipSize < 0.0)
            throw new NoValidTipSizeException(tipSize);


        this.id = id;
        this.fullName = fullName;
        this.orderList = new ArrayList<>(orderList);
        this.tipSize = tipSize;
    }

    /**
     * @param fullName
     * @param tipSize
     * @throws NullParamException
     * @throws NoValidNameException
     * @throws NoValidTipSizeException
     */
    public Barista(String fullName, Double tipSize) {
        if (fullName == null || tipSize == null)
            throw new NullParamException();
        if (fullName.isEmpty())
            throw new NoValidNameException();
        if (tipSize.isNaN() || tipSize.isInfinite() || tipSize < 0.0)
            throw new NoValidTipSizeException(tipSize);

        this.fullName = fullName;
        this.tipSize = tipSize;
        this.orderList = new ArrayList<>();
    }

    /**
     * @param fullName
     * @throws NullParamException
     * @throws NoValidNameException
     */
    public Barista(String fullName) {
        if (fullName == null)
            throw new NullParamException();
        if (fullName.isEmpty())
            throw new NoValidNameException();

        this.fullName = fullName;
        this.orderList = new ArrayList<>();
        this.tipSize = 0.1;
    }

    /**
     * @param fullName
     * @param orderList
     * @throws NullParamException
     * @throws NoValidNameException
     */
    public Barista(String fullName, List<Order> orderList) {
        if (fullName == null || orderList == null)
            throw new NullParamException();
        if (fullName.isEmpty())
            throw new NoValidNameException();

        this.fullName = fullName;
        this.orderList = orderList;
        tipSize = 0.1;
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

    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName
     * @throws NullParamException
     * @throws NoValidNameException
     */
    public void setFullName(String fullName) {
        if (fullName == null)
            throw new NullParamException();
        if (fullName.isEmpty())
            throw new NoValidNameException();

        this.fullName = fullName;
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

    public Double getTipSize() {
        return tipSize;
    }

    /**
     * @param tipSize
     * @throws NullParamException
     * @throws NoValidTipSizeException
     */
    public void setTipSize(Double tipSize) {
        if (tipSize == null)
            throw new NullParamException();
        if (tipSize.isNaN() || tipSize.isInfinite() || tipSize < 0.0)
            throw new NoValidTipSizeException(tipSize);

        this.tipSize = tipSize;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Barista barista)) return false;

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
