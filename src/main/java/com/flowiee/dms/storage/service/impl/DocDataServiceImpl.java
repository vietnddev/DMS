package com.flowiee.dms.storage.service.impl;

import com.flowiee.dms.storage.entity.DocData;
import com.flowiee.dms.storage.repository.DocDataRepository;
import com.flowiee.dms.common.service.BaseService;
import com.flowiee.dms.storage.service.DocDataService;
import com.flowiee.dms.common.utils.constants.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocDataServiceImpl extends BaseService implements DocDataService {
    DocDataRepository docDataRepository;

    public List<DocData> findAll() {
        return docDataRepository.findAll();
    }

    public Optional<DocData> findById(Long id) {
        return docDataRepository.findById(id);
    }

    public DocData save(DocData docData) {
        return docDataRepository.save(docData);
    }

    @Override
    public DocData update(DocData entity, Long entityId) {
        entity.setId(entityId);
        return docDataRepository.save(entity);
    }

    public String delete(Long id) {
        docDataRepository.deleteById(id);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<DocData> findByDocField(Long docFieldId) {
        return docDataRepository.findByDocField(docFieldId);
    }

    @Override
    public List<DocData> findByDocument(Long documentId) {
        return docDataRepository.findByDocumentId(documentId);
    }

    @Override
    public DocData findByFieldIdAndDocId(Long docFieldId, Long documentId) {
        return docDataRepository.findByFieldIdAndDocId(docFieldId, documentId);
    }

    @Transactional
    @Override
    public String update(String value, Long docDataId) {
        docDataRepository.updateMetaData(value, docDataId);
        return MessageCode.UPDATE_SUCCESS.getDescription();
    }

    @Transactional
    @Override
    public void deleteAllByDocument(Long documentId) {
        docDataRepository.deleteAllByDocument(documentId);
    }
}