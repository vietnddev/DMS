package com.flowiee.dms.account.model;

import lombok.Getter;

@Getter
public enum MODULE {
    STORAGE("Kho tài liệu"),
    SYSTEM("Quản trị hệ thống"),
    CATEGORY("Danh mục hệ thống");

    private final String label;

    MODULE(String label) {
        this.label = label;
    }
}