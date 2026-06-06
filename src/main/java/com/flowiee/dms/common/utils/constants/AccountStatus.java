package com.flowiee.dms.common.utils.constants;

import lombok.Getter;

@Getter
public enum AccountStatus {
    N("Normal"),
    L("Locked"),
    C("Closed");

    private String description;

    AccountStatus(String description) {
        this.description = description;
    }
}