package sql.table_enums;

public enum TimelineColumns {
    TIMELINEID("TimelineID"),
    NAME("Name"),
    START("Start"),
    END("End"),
    TIMEFRAME("Timeframe"),
    KEYWORDS("Keywords"),
    TODD("TODD");

    private String columnName;

    TimelineColumns(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public String toString() { return columnName; }
}
