package com.ykj.calendar.enums;

/**
 * Created by fulushan on 2019/1/23.
 */

public enum TimeTypeEnum {
    DAY("DAY","YYYY-MM-DD"),
    WEEK("WEEK", "YYYY-MM-DD"),
    MONTH("MONTH", "YYYY-MM-DD"),
    SEASON("SEASON", "YYYY-MM-DD");

    TimeTypeEnum(String type, String format) {
        this.type = type;
        this.format = format;
    }

    private String type;
    private String format;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}