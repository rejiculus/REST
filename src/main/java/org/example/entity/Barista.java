package org.example.entity;

import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidTipSizeException;
import org.example.entity.exception.NullParamException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Barista entity. Contains full name, tip size and order list.
 * Full name is required. Others fields are initialized: tipSize = 0.1, orderList = empty list, id = -1;
 */
public class Barista {
    private Long id;
    private String fullName;
    private Double tipSize;
    private List<Order> orderList;

    /**
     * All field constructor.
     *
     * @param id        unequal entity identifier. Must be more not less than zero.
     * @param fullName  barista's full name. Can't be empty or null.
     * @param orderList list of orders prepared by the barista. Can't be null.
     * @param tipSize   tip that barista take per order, in percent. Can't be less than zero, NaN, infinite or null.
     * @throws NullParamException      thrown when one of param equals null.
     * @throws NoValidIdException      thrown when id less than zero.
     * @throws NoValidNameException    thrown when name is empty.
     * @throws NoValidTipSizeException thrown when tip size is NaN, Infinite or less than zero.
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
     * Without id constructor.
     *
     * @param fullName  barista's full name. Can't be empty or null.
     * @param orderList list of orders prepared by the barista. Can't be null.
     * @param tipSize   tip that barista take per order, in percent. Can't be less than zero, NaN, infinite or null.
     * @throws NullParamException      thrown when one of param equals null.
     * @throws NoValidNameException    thrown when name is empty.
     * @throws NoValidTipSizeException thrown when tip size is NaN, Infinite or less than zero.
     */
    public Barista(String fullName, List<Order> orderList, Double tipSize) {
        if (fullName == null || orderList == null || tipSize == null)
            throw new NullParamException();
        if (fullName.isEmpty())
            throw new NoValidNameException();
        if (tipSize.isNaN() || tipSize.isInfinite() || tipSize < 0.0)
            throw new NoValidTipSizeException(tipSize);

        this.id = -1L;
        this.fullName = fullName;
        this.orderList = new ArrayList<>(orderList);
        this.tipSize = tipSize;
    }

    /**
     * Constructor with full name and tip size params
     *
     * @param fullName barista's full name. Can't be empty or null.
     * @param tipSize  tip that barista take per order, in percent. Can't be less than zero, NaN, infinite or null.
     * @throws NullParamException      thrown when one of param equals null.
     * @throws NoValidNameException    thrown when name is empty.
     * @throws NoValidTipSizeException thrown when tip size is NaN, Infinite or less than zero.
     */
    public Barista(String fullName, Double tipSize) {
        if (fullName == null || tipSize == null)
            throw new NullParamException();
        if (fullName.isEmpty())
            throw new NoValidNameException();
        if (tipSize.isNaN() || tipSize.isInfinite() || tipSize < 0.0)
            throw new NoValidTipSizeException(tipSize);

        this.id = -1L;
        this.fullName = fullName;
        this.tipSize = tipSize;
        this.orderList = new ArrayList<>();
    }

    /**
     * Minimum necessary Constructor
     *
     * @param fullName barista's full name. Can't be empty or null.
     * @throws NullParamException   thrown when one of param equals null.
     * @throws NoValidNameException thrown when name is empty.
     */
    public Barista(String fullName) {
        if (fullName == null)
            throw new NullParamException();
        if (fullName.isEmpty())
            throw new NoValidNameException();

        this.id = -1L;
        this.fullName = fullName;
        this.orderList = new ArrayList<>();
        this.tipSize = 0.1;
    }

    /**
     * Constructor with full name and order list params
     *
     * @param fullName  barista's full name. Can't be empty or null.
     * @param orderList list of orders prepared by the barista. Can't be null.
     * @throws NullParamException   thrown when one of param equals null.
     * @throws NoValidNameException thrown when name is empty.
     */
    public Barista(String fullName, List<Order> orderList) {
        if (fullName == null || orderList == null)
            throw new NullParamException();
        if (fullName.isEmpty())
            throw new NoValidNameException();

        this.id = -1L;
        this.fullName = fullName;
        this.orderList = orderList;
        this.tipSize = 0.1;
    }

    /**
     * Get unequal identifier.
     *
     * @return unequal identifier.
     * @throws NoValidIdException thrown if id is not defined.
     */
    public Long getId() {
        if (id.equals(-1L))
            throw new NoValidIdException();
        return id;
    }

    /**
     * Set unequal identifier.
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
     * Get full name of barista.
     *
     * @return full name.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Set barista's full name.
     *
     * @param fullName barista's full name.
     * @throws NullParamException   thrown when one of param equals null.
     * @throws NoValidNameException thrown when name is empty.
     */
    public void setFullName(String fullName) {
        if (fullName == null)
            throw new NullParamException();
        if (fullName.isEmpty())
            throw new NoValidNameException();

        this.fullName = fullName;
    }

    /**
     * Get order's that prepared by this barista.
     *
     * @return order list.
     */
    public List<Order> getOrderList() {
        return orderList;
    }

    /**
     * Set order list. List of orders that prepared by this barista.
     *
     * @param orderList that prepared by this barista.
     * @throws NullParamException thrown when one of param equals null.
     */
    public void setOrderList(List<Order> orderList) {
        if (orderList == null)
            throw new NullParamException();

        this.orderList = new ArrayList<>(orderList);
    }

    /**
     * Get tip that barista take per order, in percent. Can't be less than zero, NaN, infinite or null.
     *
     * @return tip size in percent.
     */
    public Double getTipSize() {
        return tipSize;
    }

    /**
     * Set tip that barista take per order, in percent. Can't be less than zero, NaN, infinite or null.
     *
     * @param tipSize tip that barista take per order, in percent.
     * @throws NullParamException      thrown when one of param equals null.
     * @throws NoValidTipSizeException thrown when tip size is NaN, Infinite or less than zero.
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
