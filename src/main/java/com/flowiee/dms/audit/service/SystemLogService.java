package com.flowiee.dms.audit.service;

import com.flowiee.dms.audit.entity.SystemLog;
import com.flowiee.dms.account.model.ACTION;
import com.flowiee.dms.account.model.MODULE;
import com.flowiee.dms.common.utils.ChangeLog;
import com.flowiee.dms.common.utils.constants.LogType;
import com.flowiee.dms.common.utils.constants.MasterObject;
import org.springframework.data.domain.Page;

public interface SystemLogService {
    Page<SystemLog> findAll(int pageSize, int pageNum);

    SystemLog writeLogCreate(MODULE module, ACTION function, MasterObject object, String title, String content);

    SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, ChangeLog changeLog);

    SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, String content);

    SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, String content, String contentChange);

    SystemLog writeLogDelete(MODULE module, ACTION function, MasterObject object, String title, String content);

    SystemLog writeLog(MODULE module, ACTION function, MasterObject object, LogType mode, String title, String content, String contentChange);

    SystemLog writeLog(MODULE module, ACTION function, MasterObject object, LogType mode, String title, String content, String contentChange, SystemLog systemLog);
}