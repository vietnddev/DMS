package com.flowiee.dms.storage.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocShareModel {
    Long documentId;
    Long accountId;
    String accountName;
    Boolean canRead;
    Boolean canUpdate;
    Boolean canDelete;
    Boolean canMove;
    Boolean canShare;

    public DocShareModel() {
        canRead = false;
        canUpdate = false;
        canDelete = false;
        canMove = false;
        canShare = false;
    }
}