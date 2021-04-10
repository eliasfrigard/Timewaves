package com.timeline;

public enum RangeTypes {
    BILLIONS ("Billion years"),
    MILLIONS ("Million years"),
    MILLENNIUMS ("Millenniums"),
    CENTURIES ("Centuries"),
    DECADES ("Decades"),
    YEARS ("Years"),
    MONTHS ("Months"),
    DAYS ("Days"),
    HOURS ("Hours"),
    MINUTES ("Minutes"),
    SECONDS ("Seconds"),
    MILLISECONDS ("Milliseconds");

    private final String type;

    RangeTypes(String typeName) {
        this.type = typeName;
    }

    @Override
    public String toString() {
        return type;
    }
}
