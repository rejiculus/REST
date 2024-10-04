package org.example.repository.until;

public enum BaristaSQL {
    CREATE("INSERT INTO barista (full_name, tip_size) VALUES (?, ?)"),
    UPDATE("UPDATE barista SET full_name=?, tip_size=? WHERE \"id\"=?"),
    DELETE("DELETE FROM barista WHERE \"id\"=?"),
    FIND_ALL("SELECT \"id\", full_name, tip_size FROM barista"),
    FIND_BY_ID("SELECT \"id\", full_name, tip_size FROM barista WHERE \"id\"=?"),
    FIND_ALL_BY_PAGE("SELECT \"id\", full_name, tip_size FROM barista OFFSET ? LIMIT ?");


    private final String sql;

    BaristaSQL(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }
}
