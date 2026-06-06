package com.flowiee.dms.storage.service;

import com.flowiee.dms.common.service.BaseCurdService;
import com.flowiee.dms.storage.entity.DocShare;
import com.flowiee.dms.storage.model.DocShareModel;

import java.util.List;

public interface DocShareService extends BaseCurdService<DocShare> {
    List<DocShare> findAll();

    List<DocShareModel> findDetailRolesOfDocument(Long documentId);

    boolean isShared(long documentId, String role);

    void deleteByAccount(Long accountId);

    void deleteByDocument(Long documentId);

    void deleteAllByDocument(Long documentId);
}