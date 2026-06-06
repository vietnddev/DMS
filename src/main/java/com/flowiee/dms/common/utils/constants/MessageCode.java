package com.flowiee.dms.common.utils.constants;

import lombok.Getter;

@Getter
public enum MessageCode {
    CREATE_SUCCESS(1001, "Created successfully!"),
    UPDATE_SUCCESS(1002, "Updated successfully!"),
    DELETE_SUCCESS(1003, "Deleted successfully!"),
    IMPORT_SUCCESS(1004, "Deleted successfully!");

    private final int code;
    private final String description;

    MessageCode(int code, String description) {
        this.code = code;
        this.description = description;
    }
}