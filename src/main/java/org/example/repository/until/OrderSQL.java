package org.example.repository.until;

public enum OrderSQL {
    CREATE("INSERT INTO \"order\" (barista, created, completed, price) VALUES (?,?,?,?)"),
    UPDATE("UPDATE \"order\" SET barista=?, created=?, completed=?, price=? WHERE \"id\"=?"),
    DELETE("DELETE FROM \"order\" WHERE \"id\"=?"),
    FIND_ALL("SELECT \"id\", barista, created, completed, price FROM \"order\""),
    FIND_ALL_BY_PAGE("SELECT \"id\", barista, created, completed, price FROM \"order\" OFFSET ? LIMIT ?"),
    FIND_BY_ID("SELECT \"id\", barista, created, completed, price FROM \"order\" WHERE \"id\"=?"),
    FIND_BY_COFFEE("SELECT \"id\", barista, created, completed, price FROM \"order\" " +
            "LEFT JOIN order_coffee ON \"order\".\"id\"=order_coffee.order_id " +
            "WHERE order_coffee.coffee_id=?"),
    FIND_BY_BARISTA("SELECT \"id\", barista, created, completed, price FROM \"order\" WHERE barista=?"),
    SET_BARISTA_DEFAULT("UPDATE \"order\" SET barista=0 WHERE \"id\"=?");


    private final String sql;

    OrderSQL(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }
}
