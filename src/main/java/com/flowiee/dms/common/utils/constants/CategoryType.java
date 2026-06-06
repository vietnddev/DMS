package com.flowiee.dms.common.utils.constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum CategoryType {
    DOCUMENT_TYPE("document-type", "DOCUMENT_TYPE", "Loại tài liệu");

    final String key;
    final String name;
    @Setter
    String label;

    CategoryType(String key, String name, String label) {
        this.key = key;
        this.name = name;
        this.label = label;
    }

    public static boolean isValid(String type) {
        for (CategoryType c : CategoryType.values()) {
            if (c.getKey().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static CategoryType getByKey(String key) {
        for (CategoryType c : CategoryType.values()) {
            if (c.getKey().equals(key)) {
                return c;
            }
        }
        throw new NullPointerException();
    }
}