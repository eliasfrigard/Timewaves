package sql.table_enums;

public enum DBTables {
    USERS("Users"),
    TIMELINES("Timelines"),
    EVENTS("TimeNode"),
    RATINGS("Ratings");

    private String tableName;

    DBTables(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return tableName;
    }
}
