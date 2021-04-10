package sql.table_enums;

public enum UserColumns {

    USERID("db_userID"),
    USERNAME("db_username"),
    PASSWORD("db_password"),
    EMAIL("db_email"),
    ISADMIN("db_isAdmin"),
    INRECOVERY("db_inRecovery");


    private String columnName;

    UserColumns(String columnName) {
        this.columnName = columnName;
    }

    public String toString() {
        return columnName;
    }
}