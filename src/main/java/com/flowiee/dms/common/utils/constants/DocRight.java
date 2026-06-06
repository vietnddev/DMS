package com.flowiee.dms.common.utils.constants;

import lombok.Getter;

@Getter
public enum DocRight {
    READ("R", "Xem"),
    CREATE("C", "Tạo mới"),
    UPDATE("U", "Cập nhật"),
    DELETE("D", "Xóa"),
    MOVE("M", "Di chuyển"),
    SHARE("S", "Chia sẽ");

    final String value;
    final String label;

    DocRight(String value, String label) {
      this.value = value;
      this.label = label;
    }
}