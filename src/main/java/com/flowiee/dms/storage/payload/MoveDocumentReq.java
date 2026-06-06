package com.flowiee.dms.storage.payload;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class MoveDocumentReq implements Serializable {
    long destinationId;
    List<Integer> selectedDocuments;
}