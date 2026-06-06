package com.flowiee.dms.storage.service;

import com.flowiee.dms.common.service.BaseCurdService;
import com.flowiee.dms.storage.entity.DocField;

import java.util.List;

public interface DocFieldService extends BaseCurdService<DocField> {
    List<DocField> findAll();

    List<DocField> findByDocTypeId(Long doctypeId);
}