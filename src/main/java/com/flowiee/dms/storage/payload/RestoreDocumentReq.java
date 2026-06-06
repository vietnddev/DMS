package com.flowiee.dms.storage.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestoreDocumentReq {
    List<Integer> selectedDocuments;

    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getName())
                .append("{")
                .append("selectedDocuments=").append(selectedDocuments)
                .append("}")
                .toString();
    }
}