package com.flowiee.dms.storage.service;

import com.flowiee.dms.common.service.BaseCurdService;
import com.flowiee.dms.storage.entity.DocData;

import java.util.List;

public interface DocDataService extends BaseCurdService<DocData> {
    List<DocData> findByDocField(Long docFieldId);

    List<DocData> findByDocument(Long documentId);

    DocData findByFieldIdAndDocId(Long docFieldId, Long documentId);

    String update(String value, Long docDataId);

    void deleteAllByDocument(Long documentId);
}