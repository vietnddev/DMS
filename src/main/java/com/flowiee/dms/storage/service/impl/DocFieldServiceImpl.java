package com.flowiee.dms.storage.service.impl;

import com.flowiee.dms.common.exception.DataInUseException;
import com.flowiee.dms.storage.entity.DocField;
import com.flowiee.dms.common.exception.ResourceNotFoundException;
import com.flowiee.dms.account.model.ACTION;
import com.flowiee.dms.account.model.MODULE;
import com.flowiee.dms.storage.repository.DocFieldRepository;
import com.flowiee.dms.common.service.BaseService;
import com.flowiee.dms.storage.service.DocDataService;
import com.flowiee.dms.storage.service.DocFieldService;
import com.flowiee.dms.audit.service.SystemLogService;
import com.flowiee.dms.common.utils.ChangeLog;
import com.flowiee.dms.common.utils.constants.ErrorCode;
import com.flowiee.dms.common.utils.constants.MasterObject;
import com.flowiee.dms.common.utils.constants.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocFieldServiceImpl extends BaseService implements DocFieldService {
    DocDataService docDataService;
    SystemLogService systemLogService;
    DocFieldRepository docFieldRepository;

    @Override
    public List<DocField> findAll() {
        return docFieldRepository.findAll();
    }

    @Override
    public Optional<DocField> findById(Long id) {
        return docFieldRepository.findById(id);
    }

    @Override
    public List<DocField> findByDocTypeId(Long doctypeId) {
        return docFieldRepository.findByDoctype(doctypeId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DocField save(DocField docField) {
        DocField docFieldSaved = docFieldRepository.save(docField);
        systemLogService.writeLogCreate(MODULE.STORAGE, ACTION.STG_DOC_DOCTYPE_CONFIG, MasterObject.DocField, "Thêm mới DocField", docFieldSaved.getName());
        log.info("{}: Thêm mới doc_field id={}", DocumentInfoServiceImpl.class.getName(), docField.getId());
        return docFieldSaved;
    }

    @Override
    public DocField update(DocField pDocField, Long docFieldId) {
        Optional<DocField> docFieldOpt = this.findById(docFieldId);
        if (docFieldOpt.isEmpty()) {
            throw new ResourceNotFoundException("DocField not found!", true);
        }
        DocField docFieldBefore = ObjectUtils.clone(docFieldOpt.get());

        pDocField.setId(docFieldId);
        DocField docFieldUpdated = docFieldRepository.save(pDocField);

        ChangeLog changeLog = new ChangeLog(docFieldBefore, docFieldUpdated);
        systemLogService.writeLogUpdate(MODULE.STORAGE, ACTION.STG_DOC_DOCTYPE_CONFIG, MasterObject.DocField, "Cập nhật DocField", changeLog);
        log.info(DocumentInfoServiceImpl.class.getName() + ": Cập nhật doc_field " + docFieldId);
        return docFieldUpdated;
    }

    @Transactional
    @Override
    public String delete(Long id) {
        Optional<DocField> docField = this.findById(id);
        if (docField.isEmpty()) {
            throw new ResourceNotFoundException("DocField not found!", true);
        }
        if (!docDataService.findByDocField(id).isEmpty()) {
            throw new DataInUseException(ErrorCode.DATA_LOCKED_ERROR.getDescription());
        }
        docFieldRepository.deleteById(id);
        systemLogService.writeLogDelete(MODULE.STORAGE, ACTION.STG_DOC_DOCTYPE_CONFIG, MasterObject.DocField, "Xóa DocField", docField.get().getName());
        log.info(DocumentInfoServiceImpl.class.getName() + ": Xóa DocField id=" + id);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}