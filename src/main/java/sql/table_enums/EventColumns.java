package sql.table_enums;

public enum EventColumns {
    EVENTID("NodeID"),
    TIMELINEID("TimelineID"),
    NAME("Name"),
    TIME("Time"),
    SHORTDESC("ShortDesc"),
    LONGDESC("LongDesc"),
    IMAGEPATH("ImagePath"),
    IMAGEFILE("ImageFile");

    private String columnName;

    EventColumns(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() { return columnName; }
}
