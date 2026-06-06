package com.flowiee.dms.storage.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardModel implements Serializable {
    int totalDoc;
    int totalFile;
    int totalFolder;
    String totalSize;
}