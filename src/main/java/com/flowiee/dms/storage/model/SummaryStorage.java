package com.flowiee.dms.storage.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SummaryStorage {
    int totalDocument;
    int totalFolder;
    int totalFile;
    String totalSize;
}