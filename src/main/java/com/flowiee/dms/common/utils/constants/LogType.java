package com.flowiee.dms.common.utils.constants;

import lombok.Getter;

@Getter
public enum LogType {
    LI("Đăng nhập"),
    LO("Đăng xuất"),
    R("Xem dữ liệu"),
    I("Thêm mới"),
    U("Cập nhật"),
    D("Xóa"),
    IM("Import data"),
    EX("Export data");

    private final String description;

    LogType(final String description) {
        this.description = description;
    }
}