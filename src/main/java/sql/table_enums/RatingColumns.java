package sql.table_enums;

public enum RatingColumns {
    TIMELINEID("TimelineID"),
    USERID("UserID"),
    RATING("Rating");

    private String columnName;

    RatingColumns(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() { return columnName; }
}
