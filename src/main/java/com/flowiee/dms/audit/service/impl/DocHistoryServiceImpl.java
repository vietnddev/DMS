package com.flowiee.dms.audit.service.impl;

import com.flowiee.dms.storage.entity.DocData;
import com.flowiee.dms.audit.entity.StorageHistory;
import com.flowiee.dms.storage.entity.Document;
import com.flowiee.dms.storage.entity.FileStorage;
import com.flowiee.dms.audit.repository.StorageHistoryRepository;
import com.flowiee.dms.common.service.BaseService;
import com.flowiee.dms.audit.service.DocHistoryService;
import com.flowiee.dms.common.utils.ChangeLog;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocHistoryServiceImpl extends BaseService implements DocHistoryService {
    StorageHistoryRepository storageHistoryRepository;

    @Override
    public List<StorageHistory> findAll() {
        return storageHistoryRepository.findAll();
    }

    @Override
    public StorageHistory save(StorageHistory storageHistory) {
        return storageHistoryRepository.save(storageHistory);
    }

    @Override
    public List<StorageHistory> save(Document document, DocData docData, FileStorage fileStorage, ChangeLog changeLog, String title) {
        List<StorageHistory> docHistories = new ArrayList<>();
        for (Map.Entry<String, Object[]> log : changeLog.getLogChanges().entrySet()) {
            Object oldValue = log.getValue()[0];
            Object newValue = log.getValue()[1];
            docHistories.add(this.save(StorageHistory.builder()
                    .document(document)
                    .docData(docData != null ? docData : null)
                    .fileStorage(fileStorage != null ? fileStorage : null)
                    .title(title != null ? title : "Modify " + document.getName())
                    .fieldName(log.getKey())
                    .oldValue(oldValue != null ? oldValue.toString() : StorageHistory.EMPTY)
                    .newValue(newValue != null ? newValue.toString() : StorageHistory.EMPTY)
                    .build()));
        }
        return docHistories;
    }

    @Override
    public StorageHistory saveDocDataHistory(Document document, DocData docData, String field, Object oldValue, Object newValue) {
        return this.save(StorageHistory.builder()
                .document(document)
                .docData(docData)
                .title("Modify metadata of " + document.getName())
                .fieldName(field)
                .oldValue(oldValue != null ? oldValue.toString() : StorageHistory.EMPTY)
                .newValue(newValue != null ? newValue.toString() : StorageHistory.EMPTY)
                .build());
    }

    @Override
    public List<StorageHistory> findByDocData(Long docDataId) {
        return storageHistoryRepository.findByDocData(docDataId);
    }
}