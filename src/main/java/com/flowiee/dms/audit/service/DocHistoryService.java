package com.flowiee.dms.audit.service;

import com.flowiee.dms.storage.entity.DocData;
import com.flowiee.dms.audit.entity.StorageHistory;
import com.flowiee.dms.storage.entity.Document;
import com.flowiee.dms.storage.entity.FileStorage;
import com.flowiee.dms.common.utils.ChangeLog;

import java.util.List;

public interface DocHistoryService {
    List<StorageHistory> findAll();

    List<StorageHistory> findByDocData(Long docDataId);

    StorageHistory save(StorageHistory storageHistory);

    List<StorageHistory> save(Document document, DocData docData, FileStorage fileStorage, ChangeLog changeLog, String title);

    StorageHistory saveDocDataHistory(Document document, DocData docData, String field, Object oldValue, Object newValue);
}