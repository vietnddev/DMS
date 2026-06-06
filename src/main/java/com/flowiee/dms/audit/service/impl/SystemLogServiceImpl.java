package com.flowiee.dms.audit.service.impl;

import com.flowiee.dms.account.entity.Account;
import com.flowiee.dms.audit.entity.SystemLog;
import com.flowiee.dms.account.model.ACTION;
import com.flowiee.dms.account.model.MODULE;
import com.flowiee.dms.audit.repository.SystemLogRepository;
import com.flowiee.dms.common.service.BaseService;
import com.flowiee.dms.audit.service.SystemLogService;
import com.flowiee.dms.common.utils.ChangeLog;
import com.flowiee.dms.common.utils.SecurityUtils;
import com.flowiee.dms.common.utils.constants.LogType;
import com.flowiee.dms.common.utils.constants.MasterObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SystemLogServiceImpl extends BaseService implements SystemLogService {
    SystemLogRepository systemLogRepository;

    @Override
    public Page<SystemLog> findAll(int pageSize, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("id").descending());
        Page<SystemLog> logs = systemLogRepository.findAll(pageable);
        for (SystemLog systemLog : logs.getContent()) {
            if (systemLog.getAccount() != null) {
                systemLog.setAccountName(systemLog.getAccount().getFullName());
            }
        }
        return logs;
    }

    @Override
    public SystemLog writeLogCreate(MODULE module, ACTION function, MasterObject object, String title, String content) {
        return this.writeLog(module, function, object, LogType.I, title, content, SystemLog.EMPTY);
    }

    @Override
    public SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, ChangeLog changeLog) {
        return this.writeLog(module, function, object, LogType.U, title, changeLog.getOldValues(), changeLog.getNewValues());
    }

    @Override
    public SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, String content) {
        return this.writeLog(module, function, object, LogType.U, title, content, SystemLog.EMPTY);
    }

    @Override
    public SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, String content, String contentChange) {
        return this.writeLog(module, function, object, LogType.U, title, content, contentChange);
    }

    @Override
    public SystemLog writeLogDelete(MODULE module, ACTION function, MasterObject object, String title, String content) {
        return this.writeLog(module, function, object, LogType.D, title, content, SystemLog.EMPTY);
    }

    @Override
    public SystemLog writeLog(MODULE module, ACTION function, MasterObject object, LogType mode, String title, String content, String contentChange) {
        return this.writeLog(module, function, object, mode, title, content, contentChange, null);
    }

    @Override
    public SystemLog writeLog(MODULE module, ACTION function, MasterObject object, LogType mode, String title, String content, String contentChange, SystemLog pSystemLog) {
        SystemLog systemLog = new SystemLog();
        systemLog.setModule(module.name());
        systemLog.setFunction(function.name());
        systemLog.setObject(object.name());
        systemLog.setMode(mode.name());
        systemLog.setTitle(title);
        systemLog.setContent(content);
        systemLog.setContentChange(contentChange);
        if (pSystemLog != null) {
            systemLog.setIp(pSystemLog.getIp());
            systemLog.setAccount(pSystemLog.getAccount());
            systemLog.setCreatedBy(pSystemLog.getCreatedBy());
        }
        if (systemLog.getIp() == null)
            systemLog.setIp(SecurityUtils.getCurrentUser().getIp());
        if (systemLog.getAccount() == null)
            systemLog.setAccount(new Account(SecurityUtils.getCurrentUser().getId()));
        if (systemLog.getCreatedBy() == null)
            systemLog.setCreatedBy(SecurityUtils.getCurrentUser().getId());

        return systemLogRepository.save(systemLog);
    }
}