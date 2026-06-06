package com.flowiee.dms.common.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationModel {
    int pageNum;
    int pageSize;
    int totalPage;
    long totalElements;

    public PaginationModel(int pageNum, int pageSize, int totalPage, long totalElements) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalElements = totalElements;
    }
}