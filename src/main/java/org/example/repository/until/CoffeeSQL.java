package org.example.repository.until;

public enum CoffeeSQL {
    CREATE("""
            INSERT INTO coffee ("name", price)
            VALUES (?, ?)
            """),
    FIND_BY_ORDER("""
            SELECT "id", "name", price FROM coffee
            LEFT JOIN order_coffee
                ON coffee."id"=order_coffee.coffee_id
            WHERE order_coffee.order_id=?
            """),
    UPDATE("""
            UPDATE coffee
            SET "name"=?, price=?
            WHERE "id"=?
            """),
    DELETE("""
            DELETE FROM coffee
            WHERE "id"=?
            """),
    FIND_ALL("""
            SELECT "id","name", price
            FROM coffee
            """),
    FIND_ALL_BY_PAGE("""
            SELECT "id","name", price
            FROM coffee
            OFFSET ? LIMIT ?
            """),
    FIND_BY_ID("""
            SELECT "id","name", price
            FROM coffee
            WHERE "id"=?
            """),
    FIND_ALL_BY_ID("""
            SELECT "id","name", price
            FROM coffee
            WHERE "id" IN (%s)
            """);


    private final String sql;

    CoffeeSQL(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }
}
